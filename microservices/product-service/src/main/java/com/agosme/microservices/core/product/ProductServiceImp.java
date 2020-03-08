package com.agosme.microservices.core.product;


import com.agosme.api.core.product.Product;
import com.agosme.api.core.product.ProductService;
import com.agosme.util.http.ServiceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductServiceImp implements ProductService {
  private final ServiceUtil serviceUtil;

  @Autowired
  public ProductServiceImp(ServiceUtil serviceUtil) {
    this.serviceUtil = serviceUtil;
  }

  @Override
  public Product getProduct(int productId) {
      return new Product(productId,"name-" +productId,123,serviceUtil.getServiceAddress()) ;

  }
}
