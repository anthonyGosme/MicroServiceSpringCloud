package com.agosme.api.composite;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RecommendationSummary {
  private final int recommendationId;
  private final String author;
  private final int rate;
  private final String content;
}
