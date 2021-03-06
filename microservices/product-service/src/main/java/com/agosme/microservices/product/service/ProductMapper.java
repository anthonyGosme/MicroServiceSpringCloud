package com.agosme.microservices.product.service;

import com.agosme.api.core.product.Product;
import com.agosme.microservices.product.persistance.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Service;

@Mapper(componentModel = "spring")
@Service
public interface ProductMapper {

  @Mapping(target = "serviceAddress", ignore = true)
  Product entityToApi(ProductEntity entity);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  ProductEntity apiToEntity(Product api);
}
