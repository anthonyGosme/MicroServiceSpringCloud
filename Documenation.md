# init gradle project

spring init \
--boot-version=2.2.5.RELEASE \
--build=gradle \
--java-version=11 \
--packaging=jar \
--name=product-service \
--package-name=com.agosme.microservices.core.product \
--groupId=com.agosme.microservices.core.product \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
product-service

spring init \
--boot-version=2.2.5.RELEASE \
--build=gradle \
--java-version=11 \
--packaging=jar \
--name=product-service \
--package-name=com.agosme.microservices.core.product \
--groupId=com.agosme.microservices.core.product \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
review-service

spring init \
--boot-version=2.2.5.RELEASE \
--build=gradle \
--java-version=11 \
--packaging=jar \
--name=product-service \
--package-name=com.agosme.microservices.core.product \
--groupId=com.agosme.microservices.core.product \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
product-composite-service

spring init \
--boot-version=2.2.5.RELEASE \
--build=gradle \
--java-version=11 \
--packaging=jar \
--name=product-service \
--package-name=com.agosme.microservices.core.product \
--groupId=com.agosme.microservices.core.product \
--dependencies=actuator,webflux \
--version=1.0.0-SNAPSHOT \
recommendation-service

# 
#build 
./gradlew build


#test
curl http://localhost:7000/product-composite/13 -i
 curl http://localhost:7000/product-composite/113 -s | jq
 curl http://localhost:7000/product-composite/213 -s | jq