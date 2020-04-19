package com.agosme.microservices.recommendation.service;

import com.agosme.api.core.recommendation.Recommendation;
import com.agosme.microservices.recommendation.persistance.RecommendationEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

  @Mapping(target = "rate", source = "entity.rating")
  @Mapping(target = "serviceAddress", ignore = true)
  Recommendation entityToApi(RecommendationEntity entity);

  @Mapping(target = "rating", source = "api.rate")
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "version", ignore = true)
  RecommendationEntity apiToEntity(Recommendation api);

  List<Recommendation> entityListToApiList(List<RecommendationEntity> entity);

  List<RecommendationEntity> apiListToEntityList(List<Recommendation> api);
}
