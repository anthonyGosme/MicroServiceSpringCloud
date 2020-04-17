package com.agosme.api.composite;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ServiceAddresses {
  private final String compositeUrl;
  private final String productUrl;
  private final String reviewUrl;
  private final String recommendationUrl;
}
