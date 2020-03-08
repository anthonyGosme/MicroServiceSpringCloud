package com.agosme.microservices.core.product;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

public interface ProductService {
  @GetMapping(value = "/product/{prouctId}", produces = "application/json")
  Product getProduct(@PathVariable int productId);
}
