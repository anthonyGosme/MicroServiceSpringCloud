package com.agosme.api.core.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class Product {
  private final int productId;
  private final String name;
  private final int weight;
  private final String serviceAddress;
}
