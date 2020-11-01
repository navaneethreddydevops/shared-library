```
kubectl get nodes
kubectl describe node
kubectl run jenkins --image=navaneethreddydevops/jenkins --port=8080
kubectl run kubia --image=luksa/kubia --port=8080
kubectl expose kubia --type=LoadBalancer --name kubia-http
```
