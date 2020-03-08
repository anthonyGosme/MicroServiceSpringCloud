package com.agosme.api.core.review;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
public class Review {
  private final int productId;
  private final int reviewId;
  private final String author;
  private final String subject;
  private final String content;
  private final String serviceAddress;
}
