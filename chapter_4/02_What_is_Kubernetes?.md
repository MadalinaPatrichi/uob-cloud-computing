What is Kubernetes?

“Kubernetes is an open-source system for automating deployment, scaling and management of containerised applications.”

I think this is a very nice summary of what it does, but I think we usually like to think of it in terms of what problems does Kubernetes solve for you? Let’s make it relatable…

What kinds of problems does Kubernetes try to solve?

So, your users interacting with your application, whether it is via a front-end or via an API, they expect it to always be available, which is really, really challenging, right? Especially if there are any DevOps people here in the room know that achieving 5 9’s of availability is really, really hard, especially when doing app deployments, rolling updates and things like that. In the past this has been very challenging and doing instance maintenance and upgrades and security patches, etc. will always cut into that uptime.

Developers expect to be able to deploy updates to their code multiple times per day. And that is perfectly normal, it’s our job. You wouldn’t want to be held up from doing your work, implement a bunch of features, know that it works and then not be able to deploy it or test your code until after a specific time, or not today because there’s maintenance, of for whatever reason. So you want to be able to do your work and not be impeded.

Moreover, with the advent of cloud, we have the ability to have unlimited resources at our fingertips, right? But how do we utilize that? Well, it’s pretty challenging, I would say if you have an application that doesn’t use the full resources of a super powerful instance and you want to scale that across multiple instances. So, Kubernetes is going to help us solve that problem, which we’ll talk about a little bit.

And then, of course fault tolerance and self-healing, which goes back to what we were talking about in the first bullet point, which is, your ops engineers, your managers, your CEO does not want things to fail and customers to experience down time. So, for example if one of the cloud regions of cloud providers goes down, Kubernetes will help you save yourself from those types of outages.

Lastly, there’s scalability, which is huge in cloud.

But first, let’s explore the trends that have given rise to containers, the need for container orchestration and how it’s created the space for Kubernetes for dominance and growth. The growth of technology into every aspect of our lives and days has created an immense demand on software, the companies and organisations that sell and deliver products based on software, or just use software in their businesses. This really is nearly any business and anyone who doesn’t live entirely off the grid. This pressure has spurred innovation and standardisation. And the needs and stakes are so high, it has fundamentally changed how software is developed and deployed into production.

But this evolution isn’t without precedent. Could you imagine the world economy relying on methods like this for the trade of goods? There would be no way you would get your new iPhone in time. Now, we take for granted that shipping containers efficiently move between different modes of transportation, different shipowners and shipping companies. And they are standardized, ubiquitous and available anywhere in the world. Almost always, no matter what’s in, if it fits, it ships!

Modern software is becoming quite similar. As software systems become more complicated, this complication has driven software to be divided into smaller pieces such as microservices. Whether they are called microservices or not, these smaller pieces of software each need to be packaged, built, deployed and accessed by other pieces of software around them to function as a total system. These small pieces of software can be deployed into containers that allow them to run on the same machine, virtual or real, but making it appear to the software that it is the only process running.

Here is where we depart from the shipping container metaphor for a bit. While a shipping container sole job is to keep things contained within itself, typically, interesting and useful software depends on other software around it to do its job. Here is where the need for container orchestration is born. There are definite benefits in keeping logically distinct software separate from each other. You can develop, deploy, scale and maintain each small piece without too much fuss between the other parts of the system, when it’s not all on the same module. However, at some point, different parts of the application likely need to communicate with each other to do something interesting. For example, an API may need to communicate with business logic, it needs, in turn to access information in the database. Containers themselves keep logically distinct pieces of software separate, so they can be built, deployed, maintained, managed and scaled on their own, without unduly affecting other parts of the system.

Container orchestration on the other hand defines how these containers interact as a system, the needs between each other and how they come together to your performant, manageable, reliable and scalable system.

It is currently safe to say that Docker is the dominant leader in both technology and an adoption in containers. While others exist in the space, their dominance is so great, when you mention containers, it is almost synonymous with Docker. Other players exist in niche application and some movement around standardisation is occurring, although it is only at the beginning, therefore the Docker file standard is essentially today’s standard. And Docker repository has defined a dominant way of hosting versions of a container between developers and systems that need to use them. An example of a system that uses the Docker repo is Kubernetes itself.

Since container orchestration is a much newer space, only growing since the adoption of containers, the technologies are newer and still evolving. However, Kubernetes is quite advanced, and with AWS finally joining its competitors in embracing Kubernetes, it now has the backing of nearly every major player in cloud computing through the Cloud Native Computing Foundation, also known as the CNCF. CNCF is the organisation that now governs Kubernetes. It is basically an organisation that focuses on defining what the industry standards are when it comes to Kubernetes and mediates the interaction between cloud platforms and Kubernetes itself. It has essentially become the de facto standard, especially now that its competing technology, Docker Swarm (Docker’s solution to container orchestration) has now announced support for Kubernetes style configuration and processing. It is clear that Kubernetes is now the de facto standard, if not the acknowledged standard in the industry in container orchestration.

(Review and using kubernetes orchestrating containers)

Containerisation changed how software was developed and packaged; container orchestration is changing how containers are deployed into bigger useful systems.

Now we’ll focus on the growth, lineage of and why the market has chosen Kubernetes.
