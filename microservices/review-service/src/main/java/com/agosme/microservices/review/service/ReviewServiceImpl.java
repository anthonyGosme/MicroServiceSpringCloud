package com.agosme.microservices.review.service;

import com.agosme.api.core.review.Review;
import com.agosme.api.core.review.ReviewService;
import com.agosme.microservices.review.persistance.ReviewEntity;
import com.agosme.microservices.review.persistance.ReviewRepository;
import com.agosme.util.exceptions.InvalidInputException;
import com.agosme.util.http.ServiceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;

import java.util.List;

@RestController
public class ReviewServiceImpl implements ReviewService {

  private static final Logger LOG = LoggerFactory.getLogger(ReviewServiceImpl.class);

  private final ReviewRepository repository;

  private final ReviewMapper mapper;

  private final ServiceUtil serviceUtil;
  private final Scheduler scheduler;

  @Autowired
  public ReviewServiceImpl(
      ReviewRepository repository,
      ReviewMapper mapper,
      ServiceUtil serviceUtil,
      Scheduler scheduler) {
    this.repository = repository;
    this.mapper = mapper;
    this.serviceUtil = serviceUtil;
    this.scheduler = scheduler;
  }

  @Override
  public Review createReview(Review body) {
    if (body.getProductId() < 1)
      throw new InvalidInputException("Invalid productId: " + body.getProductId());
    try {
      ReviewEntity entity = mapper.apiToEntity(body);
      ReviewEntity newEntity = repository.save(entity);

      LOG.debug(
          "createReview: created a review entity: {}/{}", body.getProductId(), body.getReviewId());
      return mapper.entityToApi(newEntity);

    } catch (DataIntegrityViolationException dive) {
      throw new InvalidInputException(
          "Duplicate key, Product Id: "
              + body.getProductId()
              + ", Review Id:"
              + body.getReviewId());
    }
  }

  @Override
  public Flux<Review> getReviews(int productId) {

    if (productId < 1) throw new InvalidInputException("Invalid productId: " + productId);

    return asyncFlux(getByProductId(productId)).log();
  }

  private <T> Flux<T> asyncFlux(Iterable<T> iterable) {
    return Flux.fromIterable(iterable).publishOn(scheduler);
  }
  protected List<Review> getByProductId(int productId) {
    List<ReviewEntity> entityList = repository.findByProductId(productId);
    List<Review> list = mapper.entityListToApiList(entityList);
    list.forEach(e -> e.setServiceAddress(serviceUtil.getServiceAddress()));

    LOG.debug("getReviews: response size: {}", list.size());

    return list;
  }

  @Override
  public void deleteReviews(int productId) {
    LOG.debug(
        "deleteReviews: tries to delete reviews for the product with productId: {}", productId);
    repository.deleteAll(repository.findByProductId(productId));
  }
}
