package com.agosme.api.event;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static java.time.LocalDateTime.now;

@Data
@NoArgsConstructor
public class Event<K, T> {

  private Event.Type eventType;
  private K key;
  private T data;
  private LocalDateTime eventCreatedAt;

  public Event(Type eventType, K key, T data) {
    this.eventType = eventType;
    this.key = key;
    this.data = data;
    this.eventCreatedAt = now();
  }

  public enum Type {
    CREATE,
    DELETE
  }
}
