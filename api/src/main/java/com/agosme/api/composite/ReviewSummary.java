package com.agosme.api.composite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ReviewSummary {
  private int reviewId;
  private String author;
  private String subject;
  private String content;
}
