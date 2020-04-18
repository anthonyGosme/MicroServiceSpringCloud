# init gradle project
## build 
./gradlew build
./gradlew test
./gradlew  :microservices:product-service:test
./gradlew  :microservices:product-service:clean  :microservices:product-service:build
docker-compose kill product-service & docker-compose up product-service


#spring init


spring init \
--boot-version=2.2.5.RELEASE \
--build=gradle \
--java-version=11 \
--packaging=jar \
--name= \
--package-name=com.agosme.microservices.cloud.auth-server \
--groupId=com.agosme.microservices.cloud.auth-server \
--dependencies=actuator \
--version=1.0.0-SNAPSHOT \
auth-server

spring init \
--boot-version=2.2.5.RELEASE \
--build=gradle \
--java-version=11 \
--packaging=jar \
--name= \
--package-name=com.agosme.microservices.cloud.gateway \
--groupId=com.agosme.microservices.cloud.gateway \
--dependencies=actuator \
--version=1.0.0-SNAPSHOT \
gateway


spring init \
--boot-version=2.2.5.RELEASE \
--build=gradle \
--java-version=11 \
--packaging=jar \
--name= \
--package-name=com.agosme.microservices.cloud.eureka-server \
--groupId=com.agosme.microservices.cloud.eureka-server \
--dependencies=actuator \
--version=1.0.0-SNAPSHOT \
eureka-server

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



## test
curl http://localhost:7000/product-composite/13 -i
 curl http://localhost:7000/product-composite/113 -s | jq
 curl http://localhost:7000/product-composite/213 -s | jq
 
# set java intellij 
export  JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.7.jdk/Contents/Home

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
docker ps --format {{.Names}}
docker images -f dangling=true
docker-compose down --remove-orphans
#ip check
docker-compose exec product-composite getent hosts review
docker-compose exec --index=1 review cat /etc/hosts
docker-compose exec --index=2 review cat /etc/hosts
# test container service ip used 
docker-compose up -d --scale product=0
docker-compose up -d --scale product=1
curl localhost:8080/product-composite/2 -s |  jq -r .serviceAddresses.rev
docker-compose up -d --scale gateway=0 --scale gateway=1
# continous integration
date && ./gradlew clean build && docker-compose build && ./test-em-all.bash start stop && date

#persitance test
##  all
./gradlew clean test  --info 

./gradlew test  --info 

./gradlew microservices:product-service:test  ProductServiceApplicationTests info

./gradlew :microservices:product-composite-service:bootRun --debug-jvm


#enter inside DB via docker-compose
docker-compose exec mongodb mongo --quiet
docker-compose exec mysql mysql -uuser -p review-db




#swaggee openapi
http://localhost:7000/swagger-ui.html

#Rabbit MQ
http://localhost:15672/#/queues guest gest

#kafka
docker-compose -f docker-compose-kafka.yaml exec kafka /opt/kafka/bin/kafka-topics.sh --zookeeper zookeeper --list
docker-compose -f docker-compose-kafka.yaml exec kafka /opt/kafka/bin/kafka-topics.sh --describe --zookeeper zookeeper --topic products

#eureka service discovery

http://localhost:8761/  -> eureka IHM
http://localhost:8761/eureka/apps
http://localhost:8761/actuator

http://localhost:8761/actuator/health


# spring gateway : edge server
docker-compose ps 
http://localhost:8080/actuator/gateway/routes
docker-compose logs -f  --tail==0 gateway
http://localhost:8080/product-composite/2
http://localhost:8080/eureka/api/apps
http://localhost:8080/eureka/web
curl http://localhost:8080/headerrouting -H "Host:i.feel.lucky:8080"
curl http://localhost:8080/headerrouting -H "Host:im.a.teapot:8080"
http://localhost:8080/headerrouting

#Https
create a sef signed cetificate pass: password
keytool -genkeypair -alias localhost -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore edge.p12 -validity 3650

curl -H "accept:application/json" https://u:p@localhost:8443/eureka/api/apps -kv | jq 
browser https://localhost:8443/eureka/web/