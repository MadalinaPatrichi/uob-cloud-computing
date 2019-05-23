

### Kubernetes architecture

Now that we have a couple of basic concepts in mind, I want to go over a little bit over its internal architecture to
understand how does Kubernetes make everything work. I am going to brush over a lot of detailed concepts, but I want to
give you a rough idea of what is it and what does it actually do in the background and what is it responsible for.

There are a couple of different architectural components, two of which I am going to talk about, the first of which is 
the Kubernetes cluster services, which sit on the master node. As I previously mentioned, the fundamental premise behind
Kubernetes is that we can enforce what’s called “Desired state management”. And really  what that means, is that I am
going to feed the cluster services a specific configuration and it will be up to the cluster services to go out and run
that configuration in my infrastructure.

One of the main components that I do want to talk about is this API that sits in front of all this in the API services.
This is one building block of the system.

The second building block of the system is this thing called a worker node. What is a worker? Well, a worker is really
just a container host. The one thing unique about a worker in the Kubernetes environment is that it does have this
Kubelet process that runs, which is responsible with, yes, you guessed it, the Kubernetes Cluster Services.

So, the master node, the worker nodes and all their components, this is what actually makes up this Kubernetes cluster.

To make it more clear, let’s talk about a specific use case here. What we want to do is to feed to the Kubernetes
cluster a configuration, so the desired state exists here, in this deployment yaml file. Inside this, there could be a
whole bunch of configuration information which I am going to bypass for the moment. But, what I am going to talk about
is two main components.

One if this is a pod configuration; in order to run this pod, I need to specify some sort of container image, maybe I
want to have two container images.

The other additional thing is the number of replicas, so maybe there are three, four pod number one.
I can also list additional pods. So, what happens is that I am going to take this configuration file, I am going to feed it to the API and it will be up to the cluster services to figure out how to schedule these pods in the environment and make sure that I have the right number of pods running.
