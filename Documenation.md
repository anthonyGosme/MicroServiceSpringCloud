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
./gradlew test

#test
curl http://localhost:7000/product-composite/13 -i
 curl http://localhost:7000/product-composite/113 -s | jq
 curl http://localhost:7000/product-composite/213 -s | jq
 
#DEBUG ITH INTELLIJ
start all services
add in composition project build.gradle conf -> test.doFirst {
	jvmArgs '-Xdebug', '-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5555'
}
.. preferences -> build -> gradle -> build & run & test using intellij
gradle panel -> tasks-> verification -> test
remote debug  127.0.0.1:5555