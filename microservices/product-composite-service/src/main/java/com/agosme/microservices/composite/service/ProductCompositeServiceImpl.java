package com.agosme.microservices.composite.service;

import com.agosme.api.composite.*;
import com.agosme.api.core.product.Product;
import com.agosme.api.core.recommendation.Recommendation;
import com.agosme.api.core.review.Review;
import com.agosme.util.exceptions.NotFoundException;
import com.agosme.util.http.ServiceUtil;
import io.github.resilience4j.reactor.retry.RetryExceptionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.handler.advice.RequestHandlerCircuitBreakerAdvice;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RestController
public class ProductCompositeServiceImpl implements ProductCompositeService {
  private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);
  private final SecurityContext nullSC = new SecurityContextImpl();
  private final ServiceUtil serviceUtil;
  private final ProductCompositeIntegration integration;

  @Autowired
  public ProductCompositeServiceImpl(
      ServiceUtil serviceUtil, ProductCompositeIntegration integration) {
    this.serviceUtil = serviceUtil;
    this.integration = integration;
  }

  @Override
  public Mono<Void> createCompositeProduct(ProductAggregate body) {
    return ReactiveSecurityContextHolder.getContext()
        .doOnSuccess(sc -> internalCreateCompositeProduct(sc, body))
        .then();
  }

  public void internalCreateCompositeProduct(SecurityContext sc, ProductAggregate body) {

    try {

      logAuthorizationInfo(sc);
      LOG.debug(
          "createCompositeProduct: creates a new composite entity for productId: {}",
          body.getProductId());

      Product product = new Product(body.getProductId(), body.getName(), body.getWeight(), null);
      integration.createProduct(product);

      if (body.getRecommendations() != null) {
        body.getRecommendations()
            .forEach(
                r -> {
                  Recommendation recommendation =
                      new Recommendation(
                          body.getProductId(),
                          r.getRecommendationId(),
                          r.getAuthor(),
                          r.getRate(),
                          r.getContent(),
                          null);
                  integration.createRecommendation(recommendation);
                });
      }

      if (body.getReviews() != null) {
        body.getReviews()
            .forEach(
                r -> {
                  Review review =
                      new Review(
                          body.getProductId(),
                          r.getReviewId(),
                          r.getAuthor(),
                          r.getSubject(),
                          r.getContent(),
                          null);
                  integration.createReview(review);
                });
      }

      LOG.debug(
          "createCompositeProduct: composite entites created for productId: {}",
          body.getProductId());

    } catch (RuntimeException re) {

      throw new RuntimeException("createCompositeProduct failed", re);
    }
  }

  @Override
  public Mono<ProductAggregate> getCompositeProduct(int productId, int delay, int faultPercent) {


    return Mono.zip(
            values ->
                    createProductAggregate(
                            (SecurityContext) values[0],
                            (Product) values[1],
                            (List<Recommendation>) values[2],
                            (List<Review>) values[3],
                            serviceUtil.getServiceAddress()),
            ReactiveSecurityContextHolder.getContext().defaultIfEmpty(nullSC),
            integration
                    .getProduct(productId, delay, faultPercent)
                    .onErrorMap(
                            RetryExceptionWrapper.class, retryException -> retryException.getCause())
                   // .onErrorResume(
                     //       TimeoutException.class,
                       //     error -> Mono.just(getProductFallbackValue(productId)))
                    .onErrorReturn(
                            RequestHandlerCircuitBreakerAdvice.CircuitBreakerOpenException.class,
                            getProductFallbackValue(productId))
            ,
            integration.getRecommendations(productId).collectList(),
            integration.getReviews(productId).collectList())
            .doOnError(ex -> LOG.warn("getCompositeProduct failed: {}", ex.toString()))
            .log();
  }

  private Product getProductFallbackValue(int productId) {

    LOG.warn("Creating a fallback product for productId = {}", productId);

    if (productId == 13) {
      String errMsg = "Product Id: " + productId + " not found in fallback cache!";
      LOG.warn(errMsg);
      throw new NotFoundException(errMsg);
    }

    return new Product(
            productId, "Fallback product" + productId, productId, serviceUtil.getServiceAddress());
  }

  @Override
  public Mono<Void> deleteCompositeProduct(int productId) {
    return ReactiveSecurityContextHolder.getContext()
            .doOnSuccess(sc -> internalDeleteCompositeProduct(sc, productId))
            .then();
  }

  private void internalDeleteCompositeProduct(SecurityContext sc, int productId) {
    try {
      logAuthorizationInfo(sc);

      LOG.debug("deleteCompositeProduct: Deletes a product aggregate for productId: {}", productId);

      integration.deleteProduct(productId);
      integration.deleteRecommendations(productId);
      integration.deleteReviews(productId);

      LOG.debug("deleteCompositeProduct: aggregate entities deleted for productId: {}", productId);

    } catch (RuntimeException re) {

      throw new RuntimeException("createCompositeProduct failed", re);
    }
  }

  private ProductAggregate createProductAggregate(
      SecurityContext sc,
      Product product,
      List<Recommendation> recommendations,
      List<Review> reviews,
      String serviceAddress) {

    logAuthorizationInfo(sc);

    // 1. Setup product info
    int productId = product.getProductId();
    String name = product.getName();
    int weight = product.getWeight();

    // 2. Copy summary recommendation info, if available
    List<RecommendationSummary> recommendationSummaries =
        (recommendations == null)
            ? null
            : recommendations.stream()
                .map(
                    r ->
                        new RecommendationSummary(
                            r.getRecommendationId(), r.getAuthor(), r.getRate(), r.getContent()))
                .collect(Collectors.toList());

    // 3. Copy summary review info, if available
    List<ReviewSummary> reviewSummaries =
        (reviews == null)
            ? null
            : reviews.stream()
                .map(
                    r ->
                        new ReviewSummary(
                            r.getReviewId(), r.getAuthor(), r.getSubject(), r.getContent()))
                .collect(Collectors.toList());

    // 4. Create info regarding the involved microservices addresses
    String productAddress = product.getServiceAddress();
    String reviewAddress =
        (reviews != null && !reviews.isEmpty()) ? reviews.get(0).getServiceAddress() : "";
    String recommendationAddress =
        (recommendations != null && !recommendations.isEmpty())
            ? recommendations.get(0).getServiceAddress()
            : "";
    ServiceAddresses serviceAddresses =
        new ServiceAddresses(serviceAddress, productAddress, reviewAddress, recommendationAddress);

    return new ProductAggregate(
        productId, name, weight, recommendationSummaries, reviewSummaries, serviceAddresses);
  }

  private void logAuthorizationInfo(SecurityContext sc) {
    if (sc != null
        && sc.getAuthentication() != null
        && sc.getAuthentication() instanceof JwtAuthenticationToken) {
      Jwt jwtToken = ((JwtAuthenticationToken) sc.getAuthentication()).getToken();
      logAuthorizationInfo(jwtToken);
    } else {
      LOG.warn("No JWT based Authentication supplied, running tests are we?");
    }
  }

  private void logAuthorizationInfo(Jwt jwt) {
    if (jwt == null) {
      LOG.warn("No JWT supplied, running tests are we?");
    } else {
      if (LOG.isDebugEnabled()) {
        URL issuer = jwt.getIssuer();
        List<String> audience = jwt.getAudience();
        Object subject = jwt.getClaims().get("sub");
        Object scopes = jwt.getClaims().get("scope");
        Object expires = jwt.getClaims().get("exp");

        LOG.debug(
            "Authorization info: Subject: {}, scopes: {}, expires {}: issuer: {}, audience: {}",
            subject,
            scopes,
            expires,
            issuer,
            audience);
      }
    }
  }
}
