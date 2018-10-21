This directory contains scripts related to setting up and administering a shared OKE (Oracle Kubernetes Engine) cluster split between multiple student users in order to demonstrate various cloud native concepts.

Unless otherwise stated, scripts should be executed as the admin Kubernetes user or as a user with the `cluster-admin` Kubernetes role.

## 1. Set up an OKE cluster

- See [this page](https://www.oracle.com/webfolder/technetwork/tutorials/obe/oci/oke-full/index.html) for detailed instructions.

## 2. Deploy Nginx ingress

To help other users expose their services to the internet, we'll use a layer 7 routing solution known as the Nginx Ingress Controller. There are a few different ingress controllers, but the Nginx one is the easiest for this use case.

1. Install the mandatory nginx ingress components

    ```
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/master/deploy/mandatory.yaml
    ```

2. Install a nodeport service to expose it

    We're only going to support plain HTTP here, but if you did the properly you'd most likely want to have a proper HTTPS configuration with certificates.

    ```
    kubectl apply -f - << EOF
    apiVersion: v1
    kind: Service
    metadata:
      name: ingress-nginx
      namespace: ingress-nginx
      labels:
        app.kubernetes.io/name: ingress-nginx
        app.kubernetes.io/part-of: ingress-nginx
    spec:
      type: NodePort
      ports:
      - name: http
        port: 80
        targetPort: 80
        nodePort: 30080
        protocol: TCP
      selector:
        app.kubernetes.io/name: ingress-nginx
        app.kubernetes.io/part-of: ingress-nginx
    EOF
    ```

3. Set up an OCI load balancer for the HTTP nodeport

    Now in the OCI console, set up a load balancer to send traffic from port 80 to port 30080 on all the worker nodes. The backend set should include the public IPs of all the worker nodes. 

    You should let the users of the cluster know the public IP of the Load Balancer. They should their own DNS A-records to point to the Load Balancer. This will let traffic for the DNS name flow to the Load Balancer, into the nginx ingress controller, and finally into the required pods.

## 3. Set the pod security policy

The pod security policy [security.yaml](security.yaml) is intended to lock down the pods and prevent too much 
privilege escalation between users.

```
kubectl apply -f security.yaml
```

## 4. Set up central prometheus operator

We'll install a central prometheus operator to serve all the namespaces and accounts:

```
git clone https://github.com/coreos/prometheus-operator
kubectl apply -f bundle.yaml
```

## 5. Set up accounts for each user

This command creates a namespace and service account for each user. Edit the headers at the top of the file to specify the total amount of resources available in the cluster and the expected number of users.

```
./ensure-account.py my-user my-user.kubeconfig
```

## 6. Optionally, remove an account

```
./remove-account.py my-user
```
