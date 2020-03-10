package com.agosme.microservices.composite;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ProductServiceApplicationTests {

  @Autowired private WebTestClient client;
  private static final int PRODUCT_ID_OK = 1;
  private static final int PRODUCT_ID_NOT_FOUND = 2;
  private static final int PRODUCT_ID_INVALID = 3;
  @MockBean private ProductCompositeIntegration compositeIntegration;

  public ProductServiceApplicationTests() {}

  @Before
  public void setUp() {

    when(compositeIntegration.getProduct(PRODUCT_ID_OK))
        .thenReturn(new Product(PRODUCT_ID_OK, "name", 1, "mock-address"));
    when(compositeIntegration.getRecommendations(PRODUCT_ID_OK))
        .thenReturn(
            singletonList(
                new Recommendation(PRODUCT_ID_OK, 1, "author", 1, "content", "mock address")));
    when(compositeIntegration.getReviews(PRODUCT_ID_OK))
        .thenReturn(
            singletonList(
                new Review(PRODUCT_ID_OK, 1, "author", "subject", "content", "mock address")));

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
}
