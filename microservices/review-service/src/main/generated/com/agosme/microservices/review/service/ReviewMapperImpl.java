package com.agosme.microservices.review.service;

import com.agosme.api.core.review.Review;
import com.agosme.microservices.review.persistance.ReviewEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2020-03-14T19:41:31+0100",
    comments = "version: 1.3.0.Beta2, compiler: javac, environment: Java 1.8.0_231 (Oracle Corporation)"
)
@Component
public class ReviewMapperImpl implements ReviewMapper {

    @Override
    public Review entityToApi(ReviewEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Review review = new Review();

        review.setProductId( entity.getProductId() );
        review.setReviewId( entity.getReviewId() );
        review.setAuthor( entity.getAuthor() );
        review.setSubject( entity.getSubject() );
        review.setContent( entity.getContent() );

        return review;
    }

    @Override
    public ReviewEntity apiToEntity(Review api) {
        if ( api == null ) {
            return null;
        }

        ReviewEntity reviewEntity = new ReviewEntity();

        reviewEntity.setProductId( api.getProductId() );
        reviewEntity.setReviewId( api.getReviewId() );
        reviewEntity.setAuthor( api.getAuthor() );
        reviewEntity.setSubject( api.getSubject() );
        reviewEntity.setContent( api.getContent() );

        return reviewEntity;
    }

    @Override
    public List<Review> entityListToApiList(List<ReviewEntity> entity) {
        if ( entity == null ) {
            return null;
        }

        List<Review> list = new ArrayList<Review>( entity.size() );
        for ( ReviewEntity reviewEntity : entity ) {
            list.add( entityToApi( reviewEntity ) );
        }

        return list;
    }

    @Override
    public List<ReviewEntity> apiListToEntityList(List<Review> api) {
        if ( api == null ) {
            return null;
        }

        List<ReviewEntity> list = new ArrayList<ReviewEntity>( api.size() );
        for ( Review review : api ) {
            list.add( apiToEntity( review ) );
        }

        return list;
    }
}
