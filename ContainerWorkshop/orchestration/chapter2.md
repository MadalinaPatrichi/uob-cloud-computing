
### Kubernetes primitives

The basic building blocks of Kubernetes are objects, or so called Kubernetes primitives. In order to understand
more easily the architecture of Kubernetes and the demo, we're now going to talk about a few of these primitives
and demonstrate running a single node cluster using minikube.


## Pods

Pods, as in a pod of whales or a pea pod, are collections of containers with shared storage/network.

![dockerandwhale](./.png)

How is that helpful you ask? Docker recommends that each each container should only have one concern. Decoupling
applications into multiple containers makes it easier to scale horizontally and reuse containers. For instance,
a web application stack might consist of three separate containers, each with its own unique image, to manage the web
application, database, and an in-memory cache in a decoupled manner.

The cool thing about Kubernetes is that you can put your app in one container and you can also have a second containerised
application, such as a database, which sits next to the other one in what is called a pod. Therefore, a pod is a group of containers;
and those containers see each other as localhost. You can have your app running by itself, nginx running by itself and 
they will know about each other as if they were the same entity.

Applications within a Pod also have access to shared volumes, which are defined as part of a Pod and are made available to be mounted into each application’s filesystem.
In terms of Docker constructs, a Pod is modelled as a group of Docker containers with shared namespaces and shared filesystem volumes.

Like individual application containers, Pods are considered to be relatively ephemeral (rather than durable) entities. Pods
are created, assigned a unique ID (UID), and scheduled to nodes where they remain until termination (according to restart policy)
or deletion. If a Node dies, the Pods scheduled to that node are scheduled for deletion, after a timeout period.
A given Pod (as defined by a UID) is not “rescheduled” to a new node; instead, it can be replaced by an identical Pod, 
with even the same name if desired, but with a new UID.
                                                                                                 

![podsoverview](./.png)

## Nodes

A Pod always runs on a Node. A Node may be either a virtual or physical machine, for example VMs, physical servers, or
your local machine. Each Node can have multiple pods, and the Kubernetes master automatically handles scheduling
the pods across the Nodes in the cluster. Think of a node as machine with a specific set of resources you have assigned
to it.

The Kubernetes master is responsible for maintaining the desired state for your cluster. When you interact with Kubernetes,
such as by using kubectl command-line interface, you're communicating with your cluster's Kubernetes master.

Every Kubernetes Node runs at least:
- Kubelet, a process responsible for communication between the Kubernetes Master and the Node; it manages the Pods and
the containers running on a machine.
- A container runtime (like Docker) responsible for pulling the container image from a registry, unpacking the container,
and running the application.

![nodeoverview](./.png)

A cluster is a collection of nodes. When you deploy applications on Kubernetes, you tell the master to start the
application containers. The master schedules the containers to run on the cluster's nodes. A single node is not very useful,
the purpose of Kubernetes is to pool together multiple nodes to form a more powerful machine. 

## Deployments

A deployment is similar to a recipe. As soon as you have your applications in containers, that is your ingredients, you
may now think of how to set them up. 


The following is an example of a Deployment. It creates a ReplicaSet to bring up three nginx Pods:
```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: nginx-deployment
  labels:
    app: nginx
spec:
  replicas: 3
  selector:
    matchLabels:
      app: nginx
  template:
    metadata:
      labels:
        app: nginx
    spec:
      containers:
      - name: nginx
        image: nginx:1.7.9
        ports:
        - containerPort: 80
```

A ReplicaSet’s purpose is to maintain a stable set of replica Pods running at any given time. As such, it is often used 
to guarantee the availability of a specified number of identical Pods. A deployment is a higher-level concept that manages 
ReplicaSets and provides declarative updates to Pods.

-- TODO provide an example related to out app
If you look at the deployment example here, it’s very bare-bones, it has the number of replicas, a couple of labels, it exposes some ports. 
You're telling Kubernetes how you want it to setup everything, and Kube will make sure this is always true.

In this config we have the replicas set to two, if there are ever not two replicas, because, for instance
a pod fails, or someone accidentally deletes one, Kubernetes steps in and bring it back up.

A deployment will always update the container if there is any difference.

## Service

You’ve deployed your app, the pods are running on Kubernetes, but how do you expose them now?

The way we expose them classically is through a proxy, or a load-balancer, and that usually points
to your servers and you’re good to go. In Kubernetes, the way that that’s done is through something called a service.
A service is basically the analog of a load-balancer.

Kubernetes pods can be scaled in and out, and can die at any time if a node restarts or dies. This leads to a problem: 
if some set of Pods (let’s call them backends) provides functionality to other Pods (let’s call them frontends) inside 
the Kubernetes cluster, how do those frontends find out and keep track of which backends are in that set?

Kubernetes services are an abstraction which defines a logical set of Pods and a policy by which to access them - 
sometimes called a micro-service. As an example, consider an image-processing backend which is running with 3 replicas. 
Those replicas are fungible - frontends do not care which backend they use. While the actual Pods that compose the backend
set may change, the frontend clients should not need to be aware of that or keep track of the list of backends themselves.
The Service abstraction enables this decoupling.

A Service in Kubernetes is a REST object, similar to a Pod. Like all of the REST objects, a Service definition can be 
POSTed to the apiserver to create a new instance. For example, suppose you have a set of Pods that each expose port 9376 and carry a label "app=MyApp".

```yaml
apiVersion: v1
kind: Service
metadata:
  name: my-service
spec:
  selector:
    app: MyApp
  ports:
  - protocol: TCP
    port: 80
    targetPort: 9376
```

This specification will create a new Service object named “my-service” which targets TCP port 9376 on any Pod with the "app=MyApp" label.
This Service will also be assigned an IP address (sometimes called the “cluster IP”).