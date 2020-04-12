package com.agosme.microservices.composite;

import com.agosme.api.composite.ProductAggregate;
import com.agosme.api.composite.RecommendationSummary;
import com.agosme.api.composite.ReviewSummary;
import com.agosme.api.core.product.Product;
import com.agosme.api.core.recommendation.Recommendation;
import com.agosme.api.core.review.Review;
import com.agosme.microservices.composite.service.ProductCompositeIntegration;
import com.agosme.util.exceptions.InvalidInputException;
import com.agosme.util.exceptions.NotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static reactor.core.publisher.Mono.just;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ProductCompositeServiceApplicationTests {

  private static final int PRODUCT_ID_OK = 1;
  private static final int PRODUCT_ID_NOT_FOUND = 2;
  private static final int PRODUCT_ID_INVALID = 3;
  @Autowired private WebTestClient client;
  @MockBean private ProductCompositeIntegration compositeIntegration;

  public ProductCompositeServiceApplicationTests() {}

  @Before
  public void setUp() {

    when(compositeIntegration.getProduct(PRODUCT_ID_OK))
        .thenReturn(Mono.just(new Product(PRODUCT_ID_OK, "name", 1, "mock-address")));
    when(compositeIntegration.getRecommendations(PRODUCT_ID_OK))
        .thenReturn(
            Flux.fromIterable(
                singletonList(
                    new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address"))));
    when(compositeIntegration.getReviews(PRODUCT_ID_OK))
        .thenReturn(
            Flux.fromIterable(
                singletonList(
                    new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address"))));

    when(compositeIntegration.getProduct(PRODUCT_ID_NOT_FOUND))
        .thenThrow(new NotFoundException("NOT FOUND: " + PRODUCT_ID_NOT_FOUND));

    when(compositeIntegration.getProduct(PRODUCT_ID_INVALID))
        .thenThrow(new InvalidInputException("INVALID: " + PRODUCT_ID_INVALID));
  }

  @Test
  public void getProductById() {

    client
        .get()
        .uri("/product-composite/" + PRODUCT_ID_OK)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isOk()
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.productId")
        .isEqualTo(PRODUCT_ID_OK)
        .jsonPath("$.recommendations.length()")
        .isEqualTo(1)
        .jsonPath("$.reviews.length()")
        .isEqualTo(1);
  }

  @Test
  public void getProductNotFound() {

    client
        .get()
        .uri("/product-composite/" + PRODUCT_ID_NOT_FOUND)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isNotFound()
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody()
        .jsonPath("$.path")
        .isEqualTo("/product-composite/" + PRODUCT_ID_NOT_FOUND)
        .jsonPath("$.message")
        .isEqualTo("NOT FOUND: " + PRODUCT_ID_NOT_FOUND);
  }

  @Test
  public void createCompositeProduct1() {

    ProductAggregate compositeProduct = new ProductAggregate(1, "name", 1, null, null, null);

    postAndVerifyProduct(compositeProduct, OK);
  }

  @Test
  public void createCompositeProduct2() {
    ProductAggregate compositeProduct =
        new ProductAggregate(
            1,
            "name",
            1,
            singletonList(new RecommendationSummary(1, "a", 1, "c")),
            singletonList(new ReviewSummary(1, "a", "s", "c")),
            null);

    postAndVerifyProduct(compositeProduct, OK);
  }

  @Test
  public void deleteCompositeProduct() {
    ProductAggregate compositeProduct =
        new ProductAggregate(
            1,
            "name",
            1,
            singletonList(new RecommendationSummary(1, "a", 1, "c")),
            singletonList(new ReviewSummary(1, "a", "s", "c")),
            null);

    postAndVerifyProduct(compositeProduct, OK);

    deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
    deleteAndVerifyProduct(compositeProduct.getProductId(), OK);
  }

  @Test
  public void getProductInvalidInput() {

    getAndVerifyProduct(PRODUCT_ID_INVALID, UNPROCESSABLE_ENTITY)
        .jsonPath("$.path")
        .isEqualTo("/product-composite/" + PRODUCT_ID_INVALID)
        .jsonPath("$.message")
        .isEqualTo("INVALID: " + PRODUCT_ID_INVALID);
  }

  private WebTestClient.BodyContentSpec getAndVerifyProduct(
      int productId, HttpStatus expectedStatus) {
    return client
        .get()
        .uri("/product-composite/" + productId)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus)
        .expectHeader()
        .contentType(APPLICATION_JSON)
        .expectBody();
  }

  private void postAndVerifyProduct(ProductAggregate compositeProduct, HttpStatus expectedStatus) {
    client
        .post()
        .uri("/product-composite")
        .body(just(compositeProduct), ProductAggregate.class)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus);
  }

  private void deleteAndVerifyProduct(int productId, HttpStatus expectedStatus) {
    client
        .delete()
        .uri("/product-composite/" + productId)
        .exchange()
        .expectStatus()
        .isEqualTo(expectedStatus);
  }
}
