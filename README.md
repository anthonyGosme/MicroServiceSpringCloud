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

# build 

cd recommendation-service; ./gradlew build; cd -;  \
cd product-service; ./gradlew build; cd -;  
cd product-composite-service; ./gradlew build; cd -;  
cd review-service; ./gradlew build; cd -;  