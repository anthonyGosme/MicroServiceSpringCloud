package com.agosme.microservices.core.product;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@NoArgsConstructor(force = true)
public class Product{
    private final int productId;
    private final String name;
    private final int weight;
    private final String serviceName ;



}