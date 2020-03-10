package com.agosme.microservices.composite;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;


@SpringBootTest(webEnvironment = RANDOM_PORT)
@RunWith(SpringRunner.class)
public class ProductServiceApplicationTests {

  @Autowired private WebTestClient client;
  private static final int PRODUCT_ID_OK = 1;
  private static final int PRODUCT_ID_NOT_FOUND = 2;
  private static final int PRODUCT_ID_INVALID = 3;

  public ProductServiceApplicationTests() {}

  @Test
  public void getProductById() {
    System.out.println("mmllm");
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
        .isEqualTo(3)
        .jsonPath("$.reviews.length()")
        .isEqualTo(3);
  }
}
