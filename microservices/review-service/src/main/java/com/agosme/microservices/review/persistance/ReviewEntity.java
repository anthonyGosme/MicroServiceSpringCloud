package com.agosme.microservices.review.persistance;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;

import javax.persistence.GeneratedValue;
import javax.persistence.Index;
import javax.persistence.Table;

@Getter
@Setter
@Table(
    name = "reviews",
    indexes = {
      @Index(name = "reviews_unique_idx", unique = true, columnList = "productId,reviewId")
    })
public class ReviewEntity {

  @Id @GeneratedValue private int id;

  @Version private int version;

  private int productId;
  private int reviewId;
  private String author;
  private String subject;
  private String content;
}
