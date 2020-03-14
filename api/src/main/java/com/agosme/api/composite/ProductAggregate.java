package com.agosme.api.composite;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductAggregate {
  private int productId;
  private String name;
  private int weight;
  private List<RecommendationSummary> recommendations;
  private List<ReviewSummary> reviews;

  @JsonIgnore
  private ServiceAddresses serviceAddresses;

  public ProductAggregate() {}

  public ProductAggregate(
      int productId,
      String name,
      int weight,
      List<RecommendationSummary> recommendations,
      List<ReviewSummary> reviews,
      ServiceAddresses serviceAddresses) {
    this.productId = productId;
    this.name = name;
    this.weight = weight;
    this.recommendations = recommendations;
    this.reviews = reviews;
    this.serviceAddresses = serviceAddresses;
  }
}
