FROM openjdk:12.0.2
EXPOSE 8080
ADD ./build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]


# ./gradlew :microservices:product-service:build
# docker build -t product-service .
# docker run --rm -p8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" product-service
