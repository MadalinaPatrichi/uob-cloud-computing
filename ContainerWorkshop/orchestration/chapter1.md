## What is orchestration and what is this thing called Kubernetes?

“Kubernetes is an open-source system for automating deployment, scaling and management of containerised applications.”

![kubelogo](./kube.png)


The previous definition is vague and might initially be quite confusing. We are going to talk about Kubernetes in
terms of what it does and why it came to be in the first place. We will then use it and see how it can be helpful
for ourselves. At the end of the orchestration section we will return to the definition to see if we can understand it further.


### What kinds of problems does Kubernetes try to solve?

Users expect your application to be always available. This can be really challenging in situations such as deploying apps 
or rolling out updates. In the past maintenance, upgrades, and security patches would cut into the uptime of the application.

Developers expect to be able to deploy updates to their code multiple times per day. That is perfectly normal, it’s 
our job. You wouldn’t want to implement a bunch of features and then not be able to deploy or test your code until an 
update patch.

Containerization packages software to help serve these goals, enabling application to be released and updated in an easy
and fast way without downtime. Kubernetes helps you make sure those containerized application run where and when you want,
and helps them find the resources and tools they need to work. 

### How did Kubernetes come to be?

Let’s explore the trends that have given rise to containers, the need for container orchestration and how it’s created
the space for Kubernetes for dominance and growth.

The growth of technology into every aspect of our lives and days has created an immense demand on software, the companies
and organisations that sell and deliver products based on software, or just use software in their businesses. This 
is nearly any business and anyone who doesn’t live entirely off the grid. This pressure has spurred innovation 
and standardisation. And the needs and stakes are so high, it has fundamentally changed how software is developed and 
deployed into production.

But this evolution isn’t without precedent. Imagine the world economy relying on methods like this for the
trade of goods? There would be no way you would get your new phone in time. Now, we take for granted that shipping
containers efficiently move between different modes of transportation, different shipowners and shipping companies.
And they are standardized, ubiquitous and available anywhere in the world. Almost always, no matter what’s in, if it
fits, it ships!

-- TODO
![containerpicture](./.png)

Modern software has become quite similar. As software systems become more complicated, this complication has driven
software to be divided into smaller pieces such as microservices. Whether they are called microservices or not, these 
smaller pieces of software each need to be packaged, built, deployed and accessed by other pieces of software around 
them to function as a total system. These small pieces of software can be deployed into containers that allow them to 
run on the same machine, virtual or real, but making it appear to the software that it is the only process running.

Here is where we depart from the shipping container metaphor for a bit. A shipping container sole job is to keep
things contained within itself. However, usually, interesting and useful software depends on other software around it to do its
job. Here is where the need for container orchestration is born. Kubernetes allows us to run and coordinate
applications across a cluster of machines.

The microservices architecture promotes separating an application into a collection of services. There are definite benefits in keeping logically
distinct software separate from each other. You can develop, deploy, scale and maintain each small piece without too
much fuss between the other parts of the system, when it’s not all on the same module. However, the different
parts of the application likely need to communicate with each other to do something interesting. For example, an API may
need to communicate with business logic which in turn needs to access information from a database. Containers themselves
keep logically distinct pieces of software separate, so they can be built, deployed, maintained, managed and scaled on
their own, without affecting other parts of the system. As a Kubernetes user, you can decide how the containers run and
interact with other applications.

### Kubernetes Adoption

Since container orchestration is a much newer space, only growing since the adoption of containers, the technologies are
newer and still evolving. However, Kubernetes is quite advanced, and with AWS finally joining its competitors in
embracing Kubernetes, it now has the backing of nearly every major player in cloud computing through the Cloud Native
Computing Foundation, also known as the CNCF. CNCF is the organisation that now governs Kubernetes. 

CNCF is an organisation dedicated to making cloud native computing universal and sustainable. It focuses on defining what
the industry standards are. When it comes to Kubernetes it mediates the interaction between cloud platforms and Kubernetes itself.

Containerisation changed how software was developed and packaged; container orchestration is changing how containers are
deployed into bigger useful systems. So, how did Kubernetes come about? Google learned a lot of lessons in their decade 
of experience with containers. They were running applications and containers far before anyone else and they were able 
to manage it. Kubernetes was born as a project of Google’s internal need for container orchestration.

What made the project so popular is the fact that its founders have taken all the proprietary knowledge, preserved it and
grew it through the CNCF and involved the public community for its maintenance and expansion. This allowed Kubernetes
to grow unlike proprietary technologies. Kubernetes has gained the trust and commitment of its users and the open-source
community in large part by avoiding vendor lock-in, but allowing access to the kind of support and assured quality large
vendors can offer through their own distributions. 

## Use Cases
-- TODO:
Write about some use cases:
Users of Kubernetes are usually companies who want to be able to scale up and down and the number of machines
they are using based on the consumption at the time. You can read the use cases of various companies here: https://kubernetes.io/case-studies/