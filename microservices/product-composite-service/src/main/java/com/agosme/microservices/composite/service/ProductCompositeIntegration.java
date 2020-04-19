package com.agosme.microservices.composite.service;

import com.agosme.api.core.product.Product;
import com.agosme.api.core.product.ProductService;
import com.agosme.api.core.recommendation.Recommendation;
import com.agosme.api.core.recommendation.RecommendationService;
import com.agosme.api.core.review.Review;
import com.agosme.api.core.review.ReviewService;
import com.agosme.api.event.Event;
import com.agosme.util.exceptions.InvalidInputException;
import com.agosme.util.exceptions.NotFoundException;
import com.agosme.util.http.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static com.agosme.api.event.Event.Type.CREATE;
import static com.agosme.api.event.Event.Type.DELETE;
import static reactor.core.publisher.Flux.empty;

@EnableBinding(MessageSources.class)
@Component
public class ProductCompositeIntegration
    implements ProductService, RecommendationService, ReviewService {
  protected static final Logger LOG = LoggerFactory.getLogger(ProductCompositeIntegration.class);
  private final ObjectMapper mapper;
  private final String productServiceUrl = "http://product";
  private final String recommendationServiceUrl = "http://recommendation";
  private final String reviewServiceUrl = "http://review";
  WebClientFactory webClientFactory;
  private MessageSources messageSources;

  @Autowired
  public ProductCompositeIntegration(
      MessageSources messageSources, ObjectMapper mapper, WebClient.Builder webClientBuilder) {
    webClientFactory = new WebClientFactory(webClientBuilder);

    this.mapper = mapper;
    this.messageSources = messageSources;
  }

  @Override
  public Product createProduct(Product body) {
    messageSources
        .outputProducts()
        .send(MessageBuilder.withPayload(new Event(CREATE, body.getProductId(), body)).build());
    return body;
  }

  @Override
  public Mono<Product> getProduct(int productId) {
    String url = productServiceUrl + "/product/" + productId;
    LOG.debug("Will call the getProduct API on URL: {}", url);

    return webClientFactory
        .getWebClient()
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(Product.class)
        .log()
        .onErrorMap(WebClientResponseException.class, ex -> handleException(ex));

    /*
    try {
      String url = productServiceUrl + "/" + productId;
      LOG.debug("Will call the getProduct API on URL: {}", url);

      Product product = restTemplate.getForObject(url, Product.class);
      LOG.debug("Found a product with id: {}", product.getProductId());

      return product;

    } catch (HttpClientErrorException ex) {
      throw handleHttpClientException(ex);
    }*/
  }

  @Override
  public void deleteProduct(int productId) {
    messageSources
        .outputProducts()
        .send(MessageBuilder.withPayload(new Event(DELETE, productId, null)).build());
  }

  protected String getErrorMessage(HttpClientErrorException ex) {
    try {
      return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    } catch (IOException ioex) {
      return ex.getMessage();
    }
  }

  protected String getErrorMessage(WebClientResponseException ex) {
    try {
      return mapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
    } catch (IOException ioex) {
      return ex.getMessage();
    }
  }

  @Override
  public Recommendation createRecommendation(Recommendation body) {

    messageSources
        .outputRecommendations()
        .send(MessageBuilder.withPayload(new Event(CREATE, body.getProductId(), body)).build());
    return body;
  }

  @Override
  public Flux<Recommendation> getRecommendations(int productId) {

    String url = recommendationServiceUrl + "/recommendation?productId=" + productId;

    LOG.debug("Will call the getRecommendations API on URL: {}", url);

    // Return an empty result if something goes wrong to make it possible for the composite service
    // to return partial responses
    return webClientFactory
        .getWebClient()
        .get()
        .uri(url)
        .retrieve()
        .bodyToFlux(Recommendation.class)
        .log()
        .onErrorResume(error -> empty());
  }

  @Override
  public void deleteRecommendations(int productId) {
    messageSources
        .outputRecommendations()
        .send(MessageBuilder.withPayload(new Event(DELETE, productId, null)).build());
  }

  @Override
  public Review createReview(Review body) {
    messageSources
        .outputReviews()
        .send(MessageBuilder.withPayload(new Event(CREATE, body.getProductId(), body)).build());
    return body;
  }

  @Override
  public Flux<Review> getReviews(int productId) {

    String url = reviewServiceUrl + "/review?productId=" + productId;

    LOG.debug("Will call getReviews API on URL: {}", url);
    return webClientFactory
        .getWebClient()
        .get()
        .uri(url)
        .retrieve()
        .bodyToFlux(Review.class)
        .log()
        .onErrorResume(error -> empty());
  }
/*
  protected RuntimeException handleHttpClientException(HttpClientErrorException ex) {
    switch (ex.getStatusCode()) {
      case NOT_FOUND:
        return new NotFoundException(getErrorMessage(ex));

      case UNPROCESSABLE_ENTITY:
        return new InvalidInputException(getErrorMessage(ex));

      default:
        LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", ex.getStatusCode());
        LOG.warn("Error body: {}", ex.getResponseBodyAsString());
        return ex;
    }
  }
*/
  private Throwable handleException(Throwable ex) {

    if (!(ex instanceof WebClientResponseException)) {
      LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
      return ex;
    }

    WebClientResponseException wcre = (WebClientResponseException) ex;

    switch (wcre.getStatusCode()) {
      case NOT_FOUND:
        return new NotFoundException(getErrorMessage(wcre));

      case UNPROCESSABLE_ENTITY:
        return new InvalidInputException(getErrorMessage(wcre));

      default:
        LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
        LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
        return ex;
    }
  }

  @Override
  public void deleteReviews(int productId) {
    messageSources
        .outputReviews()
        .send(MessageBuilder.withPayload(new Event(DELETE, productId, null)).build());
  }

  public Mono<Health> getProductHealth() {
    return getHealth(productServiceUrl);
  }

  public Mono<Health> getRecommendationHealth() {
    return getHealth(recommendationServiceUrl);
  }

  public Mono<Health> getReviewHealth() {
    return getHealth(reviewServiceUrl);
  }

  private Mono<Health> getHealth(String url) {
    url += "/actuator/health";
    LOG.debug("Will call the Health API on URL: {}", url);
    return webClientFactory
        .getWebClient()
        .get()
        .uri(url)
        .retrieve()
        .bodyToMono(String.class)
        .map(s -> new Health.Builder().up().build())
        .onErrorResume(ex -> Mono.just(new Health.Builder().down(ex).build()))
        .log();
  }
}
