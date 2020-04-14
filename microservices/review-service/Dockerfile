FROM openjdk:12.0.2
EXPOSE 8080
EXPOSE 8001
ADD ./build/libs/*.jar app.jar
ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8001", "-jar","/app.jar"]

# ./gradlew :microservices:product-service:build
# docker build -t product-service .
# docker run -d -p8080:8080 -e "SPRING_PROFILES_ACTIVE=docker" product-service --name my-prod-srv

# yum install htop
# yum install net-tools
