kubectl delete namespace hands-on
kubectl create namespace hands-on
kubectl config set-context $(kubectl config current-context) --namespace=hands-on