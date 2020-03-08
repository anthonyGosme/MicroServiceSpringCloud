package com.agosme.api.core.product;

import lombok.Getter;
import lombok.NoArgsConstructor;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class Product{
    private final int productId;
    private final String name;
    private final int weight;
    private final String serviceName ;

/*
    public Product() {
        this.productId = 0;
        this.name = null;
        this.weight = 0;
        this.serviceName = null;
    }

    public Product(int productId, String name, int weight, String serviceName) {
        this.productId = productId;
        this.name = name;
        this.weight = weight;
        this.serviceName = serviceName;
    }

 */
}