package com.agosme.api.composite;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationSummary {
  private  int recommendationId;
  private  String author;
  private  int rate;
  private  String content;
}
