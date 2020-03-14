package com.agosme.api.core.review;

import lombok.*;

@Getter
@Setter
//@RequiredArgsConstructor

@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Review {
  private int productId;
  private int reviewId;
  private String author;
  private String subject;
  private String content;
  private String serviceAddress;
}


