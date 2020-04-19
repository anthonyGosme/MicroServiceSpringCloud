package com.agosme.microservices.review;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.Executors;

@SpringBootApplication
@ComponentScan("com.agosme") // pour inject les API et util
public class ReviewApplication {
  private static final Logger LOG = LoggerFactory.getLogger(ReviewApplication.class);

  private final Integer connectionPoolSize;

  @Autowired
  public ReviewApplication(
      @Value("${spring.datasource.maximum-pool-size:10}") Integer connectionPoolSize) {
    this.connectionPoolSize = connectionPoolSize;
  }

  public static void main(String[] args) {
    ConfigurableApplicationContext ctx = SpringApplication.run(ReviewApplication.class, args);

    String mysqlUrl = ctx.getEnvironment().getProperty("spring.datasource.url");
    LOG.info("Connected to MysSQl: " + mysqlUrl);
  }

  @Bean
  public Scheduler jdbcScheduler() {
    LOG.info("Creates a jdbcScheduler with connectionPoolSize = " + connectionPoolSize);
    return Schedulers.fromExecutor(Executors.newFixedThreadPool(connectionPoolSize));
  }
}
