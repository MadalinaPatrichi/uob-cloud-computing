## Demo


First of all ensure you have minikube installed:

```
minikube version
```

To get a cluster running in Kubernetes type:
```
minikube start
```

You can interact with the cluster you need to use the kubectl CLI.

To find information about the cluster type:

```
kubectl cluster info
```

You can also get information about the nodes in the cluster by running:
```
kubectl get nodes
```

In our case there will only be one node running. The status of the node should be set to Active.

We can deploy a container to the Kubernetes cluster now.

-- TODO use our own container
```
kubectl run first-deployment --image=katacoda/docker-http-server --port=80
```

Get the status of the deployment:

```
kubectl get pods
```

Once the container is running it can be exposed via different networking options, depending on requirements. 
One possible solution is NodePort, that provides a dynamic port to a container.

```
kubectl expose deployment first-deployment --port=80 --type=NodePort
```

You can follow more tutorials here: https://www.katacoda.com/courses/kubernetes
