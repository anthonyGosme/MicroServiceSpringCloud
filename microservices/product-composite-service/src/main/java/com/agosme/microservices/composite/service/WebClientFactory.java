package com.agosme.microservices.composite.service;

import org.springframework.web.reactive.function.client.WebClient;

public class WebClientFactory {

  public WebClient webClient;
  private WebClient.Builder webClientBuilder;

  public WebClientFactory(WebClient.Builder webClientBuilder) {
    this.webClientBuilder = webClientBuilder;
  }

  public WebClient getWebClient() {
    if (webClient == null) {
      webClient = webClientBuilder.build();
    }
    return webClient;
  }
}
