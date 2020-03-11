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

## build 
./gradlew build
./gradlew test

## test
curl http://localhost:7000/product-composite/13 -i
 curl http://localhost:7000/product-composite/113 -s | jq
 curl http://localhost:7000/product-composite/213 -s | jq
 
## DEBUG ITH INTELLIJ
start all services
add in composition project build.gradle conf -> test.doFirst {
	jvmArgs '-Xdebug', '-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5555'
}
.. preferences -> build -> gradle -> build & run & test using intellij
gradle panel -> tasks-> verification -> test
remote debug  127.0.0.1:5555

# JSHELL
echo 'Runtime.getRuntime().availableProcessors()' | jshell -q
java -XX:+PrintFlagsFinal -version | grep MaxHeapSize
java -Xmx2000m -XX:+PrintFlagsFinal -version | grep MaxHeapSize
echo 'Runtime.getRuntime().availableProcessors()' | docker run --rm -i --cpu-shares 2048 openjdk:12.0.2 jshell -q
docker run --rm -it openjdk:12.0.2 java -XX:+PrintFlagsFinal -version | grep MaxHeapSize
## OutOfMemeory (java allocate 25% of system memory by default)
echo 'new byte[500_000_000]' | docker run --rm -i -m=1024M openjdk:12.0.2 jshell -q
echo 'new byte[500_000_000]' | docker run --rm -i -m=1024M openjdk:9-jdk jshell -q

./gra
'product-service'product-service

# docker
docker-compose logs product
docker system prune -f --volumes

# restart 
docker-compose up -d --scale product=0
docker-compose up -d --scale product=1

# continous integration
date && ./gradlew clean build && docker-compose build && ./test-em-all.bash start stop && date