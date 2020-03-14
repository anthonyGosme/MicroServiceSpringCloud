package com.agosme.api.core.product;

import lombok.*;



@Getter
@Setter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Product {
  private int productId;
  private String name;
  private int weight;
  private String serviceAddress;

  public void setServiceAddress(String serviceAddress) {}
}
