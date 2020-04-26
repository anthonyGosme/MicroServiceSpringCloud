# init gradle project
## build 
./gradlew build
./gradlew test
./gradlew  :microservices:product-service:test
./gradlew  :microservices:product-service:clean  :microservices:product-service:build
docker-compose kill product-service & docker-compose up product-service
|Û
# add alias 
alias k=kubectl
alias kg='kubectl get'
alias kd='kubectl describe'
alias kl='kubectl logs -f'
alias ke='kubectl exec -it'
eval $(minikube -p minikube docker-env)

#port troubleshooting

 lsof -nPi | grep 30 

#spring init


spring init \
--boot-version=2.2.6.RELEASE \
--build=gradle \
--java-version=11 \
--packaging=jar \
--name= \
--package-name=com.agosme.microservices.cloud.config-server \
--groupId=com.agosme.microservices.cloud.config-server \
--dependencies=actuator \
--version=1.0.0-SNAPSHOT \
config-server

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

httprocalhost:8761/actuator/health


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

curl -H "accept:application/json" https://u:p@localhost:8444/eureka/api/apps -kv | jq 
browser https://localhost:8444/eureka/web/


# Authorization server workflows tests
## password grant flow
curl -kv  https://writer:secret@localhost:8444/oauth/token -d grant_type=password -d username=anthony -d password=password | jq .
curl -kv  https://reader:secret@localhost:8444/oauth/token -d grant_type=password -d username=anthony -d password=password | jq .

## implicit grant flow
browser https://localhost:8444/oauth/authorize?response_type=token&client_id=reader&redirect_uri=http://my.redirect.uri&scope=product:read&state=48532

## code grant flow (most secure get code exhnge it with access token) 
call auth server from user browser 
 https://localhost:8444/oauth/authorize?response_type=code&client_id=reader&redirect_uri=http://my.redirect.uri&scope=product:read&state=4853
https://localhost:8444/oauth/authorize?response_type=code&client_id=reader&redirect_uri=http://my.redirect.uri&scope=product:read&state=4853

retrieve the code 
http://my.redirect.uri/?code=8tRbxG&state=4853
act as the client server app (reader )calling back the auth server in backend mode -> 
curl -kv  https://reader:secret@localhost:8444/oauth/token -d grant_type=authorization_code -d client_id=reader -d redirect_uri=http://my.redirect.uri -d code=8tRbxG | jq .

#use acces token to acces the api
ACCESS_TOKEN1=invalidAccessToken
ACCESS_TOKEN2=eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhbnRob255IiwiZXhwIjoyMTg3MzA3NzQwLCJhdXRob3JpdGllcyI6WyJST0xFX1VTRVIiXSwianRpIjoiMDI1MGE3MWUtNjQwOC00ZDQ3LTk4MTEtZDQ2YWQ0NTc2NGRmIiwiY2xpZW50X2lkIjoicmVhZGVyIiwic2NvcGUiOlsicHJvZHVjdDpyZWFkIl19.bzlFycmVrM8VZ1vkHMmnT5IwyJ6PEN3nWeYHEo44Oa_h3yrrzIKKTwE8AD9_opw_cSw1gfv_fZyo23hGwQ-l1FjHYGpjA7UXnk1-VBYfWulqzgVMQl7gjooIX-SWO2bF0fstd1kPGQizDJfSuOxv4Zt35s4lJk19muW7vzh-fdWpwgnktlD98oIBYuok5S9WM_akfxIPhJo3HzX2qsKcEoxkfPwqpXLpun4bEVYqHdV2wRiP5evF2P599w-ELkdI3e0oODaffuOQhF6bc6b-n6GWfUXJ49G_OTeXAV-Id3jvrAja6zIlYhPqSMLycXZFBCv5TwJ4h8ekFd0Jzh5mDA
curl https://localhost:8444/product-composite/2 -k -H "Authorization: Bearer $ACCESS_TOKEN2" -i
curl https://localhost:8444/product-composite/2 -k -H "Authorization: Bearer $ACCESS_TOKEN2" -i -X DELETE


#auth0.com
anthonygosme.eu.auth0.com
rD9ymywibx4qC5x4smBlhvF783wtpo3N
weVDJA2yrR60EzKpjI3-1S8KrNYw3_7xVr-Zq11FQrtBy-MUoEDUtAcUvku4vRPC
..ou7_

#config server
http://localhost:8888/auth-server/default
http://localhost:8888/review/default
curl https://dev-usr:dev-pwd@localhost:8443/config/product/docker -ks | jq .
curl https://dev-usr:dev-pwd@localhost:8443/config/encrypt -k --data-urlencode "hello world" 
curl https://dev-usr:dev-pwd@localhost:8443/config/decrypt -dc7d289e6cc613eedc709b4223a5e1fde6053048c2694d2dc1cb800a3e668866d%        

#resilience4J

ACCESS_TOKEN=$(curl -k  https://writer:secret@localhost:8443/oauth/token -d grant_type=password -d username=anthony -d password=password | jq -r .access_token)
echo $ACCESS_TOKEN

curl 'http://localhost:7000/product-composite/2?faultPercent=25'  -H "Authorization: Bearer $ACCESS_TOKEN" 

voir le temps
time curl 'http://localhost:7000/product-composite/2?faultPercent=25' -w "%{http_code}\n" -o /dev/null -s  -H "Authorization: Bearer $ACCESS_TOKEN"
--->  0.00s user 0.00s system 27% cpu 0.032 total  -> ok pas de retry
--> 0.00s user 0.00s system 0% cpu 1.039 total -> ko un retry

#zipkin & cloud sleuth 
ACCESS_TOKEN=$(curl -k  https://writer:secret@localhost:8443/oauth/token -d grant_type=password -d username=anthony -d password=password | jq -r .access_token)
echo $ACCESS_TOKEN
curl 'https://localhost:8443/product-composite/2' -k  -w "%{http_code}\n" -o /dev/null -s -H "Authorization: Bearer $ACCESS_TOKEN" 
browser http://localhost:9411/zipkin
curl 'https://localhost:8443/product-composite/12345' -k  -w "%{http_code}\n" -o /dev/null -s -H "Authorization: Bearer $ACCESS_TOKEN" 
to see asynchronous flow, (delete is idmenpotan and retuen 200 status) :
curl -X DELETE 'https://localhost:8443/product-composite/12345' -k  -w "%{http_code}\n" -o /dev/null -s -H "Authorization: Bearer $ACCESS_TOKEN" 
view rabbitmq queues :
http://localhost:15672/#/queues/%2F/zipkin

#zipkin with kafka
docker-compose -f docker-compose-kafka.yaml up -d
ACCESS_TOKEN=$(curl -k  https://writer:secret@localhost:8443/oauth/token -d grant_type=password -d username=anthony -d password=password | jq -r .access_token)
echo $ACCESS_TOKEN
curl 'https://localhost:8443/product-composite/2' -k  -w "%{http_code}\n" -o /dev/null -s -H "Authorization: Bearer $ACCESS_TOKEN" 
curl 'https://localhost:8443/product-composite/12345' -k  -w "%{http_code}\n" -o /dev/null -s -H "Authorization: Bearer $ACCESS_TOKEN" 
curl -X DELETE 'https://localhost:8443/product-composite/12345' -k  -w "%{http_code}\n" -o /dev/null -s -H "Authorization: Bearer $ACCESS_TOKEN" 

#kubernetes create minikube






# check minikube start --memory=10240 --cpus=6 --disk-size=30g --vm-driver=virtualbox
 minikube start --memory=10240 --cpus=6 --disk-size=30g --vm-driver=virtualbox


brew link --overwrite kubernetes-cli
kubectl version
cat ~/.kube/config
minikube addons enable ingress 
minikube addons enable metrics-server
kubectl get nodes
kubectl get pods --namespace=kube-system



# poc namespace test
kubectl create namespace poc
kubectl config set-context $(kubectl config current-context) --namespace=poc
kubectl config get-contexts 
kubectl apply -f nginx-deployment.yaml
kubectl get all   
kubectl delete pod -selector app=nginx-app
kubectl get all   
kubectl apply -f nginx-service.yaml
minikube ip
http://192.168.99.100:30080/
kg svc
test the port & IP from inside the cluster : create a dummy pod
kubectl run  -i --rm --restart=Never curl-client --image=tutum/curl:alpine --command -- curl -s 'http://nginx-service:80'
kubectl run  -i --rm --restart=Never curl-client --image=tutum/curl:alpine --command -- curl -s 'http://10.105.40.172:80'
kubectl delete namespace  poc

# manage memory (hibernate & resume)
minikube stop
minikube start
kubectl config set-context $(kubectl config current-context) --namespace=pockubectl crea

# use minikube docker image
eval $(minikube -p minikube docker-env)
  export DOCKER_TLS_VERIFY="1"
  export DOCKER_HOST="tcp://192.168.99.100:2376"
  export DOCKER_CERT_PATH="/Users/toto/.minikube/certs"
  export MINIKUBE_ACTIVE_DOCKERD="minikube"

# deploy on K8S



k create namespace hands-on
k config set-context $(kubectl config current-context) --namespace=hands-on
k create configmap config-repo --from-file=config-repo/ --save-config
k create secret generic config-server-secrets \
--from-literal=ENCRYPT_KEY=my-very-secure-encrypt-key \
--from-literal=SPRING_SECURITY_USER_NAME=dev-usr \
--from-literal=SPRING_SECURITY_USER_PASSWORD=dev-pwd cd se\
--save-config

k create secret generic config-client-credentials \
--from-literal=CONFIG_SERVER_USR=dev-usr \
--from-literal=CONFIG_SERVER_PWD=dev-pwd \
--save-config

k apply -k kubernetes/services/overlays/dev
k wait --timeout=600s --for=condition=ready pod --all
k get pods -o jso**n | jq.items[].spec.containers[].image

 http://172.17.0.6:8888/actuator/health: dial tcp 172.17.0.6:8888: connect: connection refused

kubectl delete all --all

# test if  working 
HOST=192.168.99.101 PORT=31443 ./test-em-all.bash
https://192.168.99.101:31443/actuator/health

my.redirect.uri
https://192.168.99.101:31443/oauth/authorize?response_type=token&client_id=reader&redirect_uri=http://my.redirect.uri&scope=product:read&state=48532
https://192.168.99.101:31443/oauth/authorize?response_type=token&client_id=reader&redirect_uri=https://192.168.99.101:31443/product-composite/2&scope=product:read&state=48532
https://192.168.99.101:31443/oauth/authorize?response_type=token&client_id=reader&redirect_uri=https://my.redirect.uri:31443/product-composite/2&scope=product:read&state=48532


# run prod
eval $(minikube docker-env)
docker-compose up -d mongodb mysql rabbitmq

docker tag hands-on/config-server  hands-on/config-server:v1     
docker tag hands-on/review-service hands-on/review-service:v1 
docker tag hands-on/auth-server  hands-on/auth-server:v1                                           
docker tag hands-on/gateway hands-on/gateway:v1                                                 
docker tag hands-on/product-service  hands-on/product-service:v1                                        
docker tag hands-on/recommendation-service hands-on/recommendation-service:v1                               
docker tag hands-on/product-composite-service  hands-on/product-composite-service:v1    
 
k create namespace hands-on
k config set-context $(kubectl config current-context) --namespace=hands-on
k create secret generic config-client-credentials \
--from-literal=CONFIG_SERVER_USR=dev-usr \
--from-literal=CONFIG_SERVER_PWD=dev-pwd \
--save-config
history -c; history -w
k apply -k
k apply -k kubernetes/services/overlays/prod/
k create configmap config-repo --from-file=config-repo/ --save-config

# rooling update
kg po -l app=product -o 
kg po -l app=product -o jsonpath='{.items[*].spec.containers[*].image} '
docker tag hands-on/product-service:v1  hands-on/product-service:v2
siege https://$(minikube ip):31443/actuator/health -c1 -d1
for i in `seq 100O0`; do curl -o /dev/null --silent --head --write-out '%{http_code}\n'  https://$(minikube ip):31443/actuator/health -k ; done
kubectl set image deployment/product pro=hands-on/product-service:v2
kg po -l app=product -w

# roll back
kg po -l app=product -w
kubectl set image deployment/product pro=hands-on/product-service:v4
k rollout history deployment product
k rollout history deployment product --revision=2

HOST=192.168.99.101 PORT=31443 ./test-em-all.bash
# ngrok

ngrok authtoken 1b2...


#ingress & config with K8s
./kubernetes/scripts/createNamespace.bash
./kubernetes/scripts/deploy-dev-env.bash   
k apply -k kubernetes/services/overlays/dev
HOST=minikube.me PORT=443 ./test-em-all.bash

#isue a  certifiate to the cert manager
0.8.1 -> 0.9.1
kubectl delete -f https://github.com/jetstack/cert-manager/releases/download/v0.9.1/cert-manager.yaml
k delete namespace cert-manager

#get status of namesapce :
kubectl get namespace cert-manager -o json
kubectl create namespace cert-manager
#install 
kubectl apply --validate=false -f https://github.com/jetstack/cert-manager/releases/download/v0.9.1/cert-manager.yaml
kubectl get pods --namespace cert-manager


# issuer saveing config
k apply -f kubernetes/services/base/letsencrypt-issuer-staging.yaml
k apply -f kubernetes/services/base/letsencrypt-issuer-prod.yaml

# run the cert issuing 
ngrok http https://minkub.me:443

watch k get po -n cert-manager
# edit and apply the ngrol server 4b5354dc
kubectl apply -f kubernetes/services/base/ingress-edge-server-ngrok.yml
NGROK_HOST=4144987b.ngrok.io 
keytool -printcert -sslserver $NGROK_HOST:443 | grep -E "Owner:|Issuer:"
HOST=4144987b.ngrok.io  PORT=443 ./test-em-all.bash

#restart nginx
kg deployment -A  | grep ngi
kubectl scale --replicas=0 deployment nginx-ingress-controller -n kube-system

kubectl scale --replicas=0 deployment nginx-deploy