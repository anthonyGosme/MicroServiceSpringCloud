package com.agosme.microservices.composite;

import com.agosme.microservices.composite.service.ProductCompositeServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@ComponentScan("com.agosme")
public class ProductCompositeServiceApplication {
  private static final Logger LOG = LoggerFactory.getLogger(ProductCompositeServiceImpl.class);
  @Bean
  RestTemplate restTemplate() {
    return new RestTemplate();
  }

  public static void main(String[] args) {
    SpringApplication.run(ProductCompositeServiceApplication.class, args);
  }
}
