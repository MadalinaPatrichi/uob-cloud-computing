This directory contains scripts related to administering a shared Kubernetes cluster split between multiple student users.

This is intended to create separate namespaces and service accounts for a number of users.

It should be executed as the admin user.

**Warning**: there may still be issues or bugs in this setup!

## Set the pod security policy

The pod security policy [security.yaml](security.yaml) is intended to lock down the pods and prevent too much 
privilege escalation between users.

```
kubectl apply -f security.yaml
```

## Set up accounts for each user

This command creates a namespace and service account for each user.

```
./ensure-account.py my-user my-user.kubeconfig
```
