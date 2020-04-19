package com.agosme.microservices.recommendation.service;

import com.agosme.api.core.recommendation.Recommendation;
import com.agosme.api.core.recommendation.RecommendationService;
import com.agosme.microservices.recommendation.persistance.RecommendationEntity;
import com.agosme.microservices.recommendation.persistance.RecommendationRepository;
import com.agosme.util.exceptions.InvalidInputException;
import com.agosme.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class RecommendationServiceImpl implements RecommendationService {

  private static final Logger LOG = LoggerFactory.getLogger(RecommendationServiceImpl.class);

  private final RecommendationRepository repository;

  private final RecommendationMapper mapper;

  private final ServiceUtil serviceUtil;

  @Autowired
  public RecommendationServiceImpl(
      RecommendationRepository repository, RecommendationMapper mapper, ServiceUtil serviceUtil) {
    this.repository = repository;
    this.mapper = mapper;
    this.serviceUtil = serviceUtil;
  }

  @Override
  public Recommendation createRecommendation(Recommendation body) {

    if (body.getProductId() < 1)
      throw new InvalidInputException("can't create Invalid productId: " + body.getProductId());

    RecommendationEntity entity = mapper.apiToEntity(body);
    Mono<Recommendation> newEntity =
        repository
            .save(entity)
            .log()
            .onErrorMap(
                DuplicateKeyException.class,
                ex ->
                    new InvalidInputException(
                        "Duplicate key, Product Id: "
                            + body.getProductId()
                            + ", Recommendation Id:"
                            + body.getRecommendationId()))
            .map(mapper::entityToApi);

    return newEntity.block();
  }

  @Override
  public Flux<Recommendation> getRecommendations(int productId) {

    if (productId < 1) throw new InvalidInputException("can't get Invalid productId: " + productId);

    return repository
        .findByProductId(productId)
        .log()
        .map(mapper::entityToApi)
        .map(
            e -> {
              e.setServiceAddress(serviceUtil.getServiceAddress());
              return e;
            });
  }

  @Override
  public void deleteRecommendations(int productId) {

    if (productId < 1) throw new InvalidInputException("can't delete Invalid productId: " + productId);

    LOG.debug(
        "deleteRecommendations: tries to delete recommendations for the product with productId: {}",
        productId);
    repository.deleteAll(repository.findByProductId(productId)).block();
  }
}
