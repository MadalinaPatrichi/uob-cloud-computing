# Deploying our application (the hard way)

We'll look at how we might deploy our application using the basic facilities available as _Infrastructure as a Service_ or _IaaS_ - to wit, full virtual machines and virtulalised networks.

The purpose of this section is threefold. Firstly, it'll talk you through setting up your application in an IaaS environment. Although we use OCI here, the principles should readily map to other providers.

Secondly, whilst we'll use the console for manual deployment - a very laborious process - the point is to familiarise you with the kinds of resources typically available. In particular, all of the following chapters use technologies which are built atop these facilities. Having a good mental model of how these higher-level services might be put together can be very helpful; nothing here is _magic_ (in fact, quite the opposite!)

Finally, the rather laborious process should encourage you to think about how you might improve upon this situation - how you might make such a deployment repeatable, how it might be automated - and motivate the use of tools that enable IaaS operations and VM management to be scripted.

## The Goal

At a high level, we'd like our users to be able to talk to our application over the Internet.

To put some more flesh on the bones of this goal, we'll split our application deployment into two pieces: the web tier, which serves up the JavaScript application and responds to requests from it; and the database tier, which persists client data.

Each of these pieces will be hosted on a separate virtual machine; thus, we'll need to ensure that our components can communicate successfully. We'll look at some common tools that can be used to troubleshoot that communication.

### Hosting on a VM

Each virtual machine is a close analogue to a physical host. In particular, it comes with a whole operating stack: a kernel, support libraries and packages, all of which potentially provide an attack surface to a hostile third party. Thus, a full VM comes with a management problem.

One of the successes of IaaS is that it enables us to rapidly provision hundreds - or thousands - of hosts just by running a script (you'll hear this called _infrastructure as software_). However, this multiplicity increases the maintenance problem: each of those virtual hosts may require patching!

As you progress through this chapter, you should ask yourself how you might act were a vulnerability in a critical library to be announced: what could you do to mitigate the risk? What could you do to update any deployed systems?

### Remote management

For the purposes of this chapter, we'll be managing our hosts remotely through a command-line session, using _ssh_ to interact with them directly.

_ssh_ can use a variety of different authentication mechanisms. The one we'll use here is a common one - a public/private key pair for authentication. As we boot a host, we specify the _public_ half of a keypair - which is injected into the host early in the boot process (we'll see how later). Providing that you possess the _private_ key that goes with that, you can authenticate yourself to the host.

#### ssh key generation

(This assumes you're working from a Linux or Mac desktop. If you're using a Windows host, you should look for the _putty_ tool or an equivalent and follow the instructions for key generation that are associated with it.)

    % mkdir ~/.ssh
    % ssh-keygen -t rsa -b 2048
    Generating public/private rsa key pair.
    Enter file in which to save the key (/Users/jan/.ssh/id_rsa): 
    Enter passphrase (empty for no passphrase): 
    Enter same passphrase again: 
    Your identification has been saved in /Users/jan/.ssh/id_rsa.
    Your public key has been saved in /Users/jan/.ssh/id_rsa.pub.
    The key fingerprint is:
    SHA256:FcVhmf24Lahrpcf56YT65djtKodK4kxTWOFFOo1uyIc jan@jan-Mac
    The key's randomart image is:
    +---[RSA 2048]----+
    |          o+*=   |
    |         . O+ .  |
    |          B .  o |
    |       . B .  . .|
    |        E =  . o |
    |         +  o.o .|
    |        + .=.oo. |
    |       + ++.**.o |
    |        o.+=o=Boo|
    +----[SHA256]-----+
    % cat ~/.ssh/id_rsa.pub
    ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDM+UIk50jMoHOEPReFcO+hTEPe3mfq8ow1ObCF4CM29OjixwWH5UJr08+CbkSZgs11LgYPu5QiK17sETSaWW4ZXQC88j5KzsxrgApRb84a+q9gPgGE0nmLAb2ZjGP13dX5Pu41b6vsapglci5/lALFq/by5G6fzqQtrh0m3d0mr3hRu1aE1vY1K6igy3Mj8/tyZxcN4OJkFbV4wzavmdpPPgh0LXT41bWfQDzQRlSs/nLPGIuUOlNpSInfSSvNvSz8ZtsWPQZtt1zuVMIhCwUdzF01urWw4ATkghk9GKNtze9ocGIrIcbNhSSQQiqkYnS8UdHUdfzr+MejiuefsMI1 jan@jan-Mac

The public part of your ssh keypair does not need to be kept secret.

## Booting the first VM

We begin by booting the initial virtual machine, creating a network for it to connect to as we go.

Locate the `Menu / Compute > Instances` screen in the web console. Ensure that you've selected your own _compartment_. (You can create a new compartment using the `Menu / Identity > Compartments` link, if required.)

### A note on compartments

A major challenge in dealing with large deployments is that of _namespace management_. As a simple example: for our purposes, we might want to deploy a version of our application, separately from our live (production) system.

Ideally, the smaller the differences between the test and production deployments, the greater the fidelity of our testing environment. In this case, we'd like our VMs to have the same _hostnames_, for instance, and not have to worry too much that a test web service might accidentally alter production data.

The _compartment_ is an IaaS approach to part of this problem: it acts as a bag in which a number of related resources can be collected. It's possible to apply security policies to particular compartments that restrict which users are capable of modifying which resources; in that way, you can put a barrier in the way of a developer accidentally updating a production application.

### The first VM

We'll boot a VM called `web1`. As we do this, we'll use the instance creation dialog to provision a new set of network resources to home the VM.

![create instance](01-02-oci-console.png "Empty instances view")

Hitting `Create Instance` will pop up a fairly extensive dialog. We'll pick the `launch in compartment` option. Let's look at the fields for the top part of the dialog:

- we pick our own compartment to host the instance
- we give it the name `web1`
- we'll use the first _availability domain_ (selected by default)
- we'll boot from an OCI-supplied image
- the image we'll use is `Oracle Linux 7.5`. (There are a couple of technical reasons for that; in particular, the instructions for package and firewall management that follow are specific to rpm-based distributions like this one.)
- we'll use a fairly small shape: `VM.Standard2.1`
- use the latest image version
- for these first machines, use the default boot volume size.

![create instance part one](02-00-create-instance-web-with-network.png "Launch instance, part one")

The second part of the dialog involves selecting the credentials that will be embedded into the running instance. Select `Choose SSH key files` and locate your `~/.ssh/id_rsa.pub` file.

![create instance part two](02-01-create-instance-web-with-network.png "Launch instance, ssh information")

Finally, we want to create a new set of network resources to use with this VM.

- select the same compartment to host the _virtual cloud network_, or _VCN_.
- call it `net1`
- ensure that public IP addresses are assigned for hosts on that VCN
- then create the instance

![create instance part three](02-02-create-instance-web-with-network.png "Launch instance, networking")

You should see a detail panel once the instance is booted. It'll look like this:

![create instance result](02-03-create-instance-web-result.png "Launch instance, result")

Notice that there are two IP addresses listed for the host - a private IP address and a public IP address. It's the latter that we'll use to connect to this VM over the internet.

### A note on tags

Resources can be _tagged_ with arbitrary labels. For a small deployment, this may seem unnecessary; however, for larger deployments, it can be useful to identify the various categories that a VM (or network) belongs to. Example divisions might be: _environment_ (staging, production, ...); _cost centre_; or perhaps the particular application that a resource is associated with.

## Booting the second VM

This process is very similar; we'll create the second instance (using `Oracle Linux 7.5` as the image operating system) on another small VM in the same compartment. Call it `db1`:

![create second_instance](03-00-create-instance-db.png "Launch instance, result")

Locate the ssh public key file as before:

![create second_instance](03-01-create-instance-db.png "Launch instance, ssh key")

This time, we'll use the pre-existing network resources that were created in our compartment:

![create second_instance](03-02-create-instance-db.png "Launch instance, networking")

The detail page looks much like before. Take a note of the public IP address - we'll be using this to `ssh` into the instance.

![create second_instance](03-03-create-instance-db-result.png "Launch instance, result")

## A diagram of the result

![](03-04-resulting-layout.png "")

### Aside: the anatomy of a TCP connection

### Aside: the runtime configuration of the VM
#### DHCP
#### Other host metadata
## Review of the deployment plan
### Log in
### Deploy and configure the database
#### Aside: what's a _package_?
### Database installation
### Database configuration: change the _root_ password
### Examine the existing schemas
### Create application credentials
### Create a blank database schema for the application
### Summary
- We’ve installed a database server
- We’ve added credentials for a new user to it (and the password isn’t just “secret” :-) )
- We’ve created a database
- We’ve confirmed that it works
### Communication between hosts
- ping fails
- ... but DNS lookup works
### Network security
### Security rules: a firewall external to the VMs

![](04-00-network-view.png "")

![](04-01-security-list-ingress.png "")

![](04-02-security-list-egress.png "")

![](04-03-edit-security-rules.png "")

![](04-04-allow-interhost-pings.png "")

![](04-05-allow-interhost-3306.png "")

### Ping working
### Use of _netcat_ to test low-level communication
### Network security part two: host-based firewalls
### VM _db1_: enabling access to the _mysql_ server
### Testing from the VM _web1_ with the _mysql_ command-line client
### Summary
- We’ve permitted connectivity from the VM web1 to the VM db1.
- We’ve demonstrated that we can talk to the database service from the host where we’ll be running our application.  (So, our application should be able to talk to it also.)

### Deployment: install the Java application
### Getting the `.jar` file
### install a _JRE_ on _web1_
### Running the Java application directly from the command-line
### Testing locally with _curl_
### Another look at the database
### Configuring the Java application to run as a daemon
### Permit inbound traffic to port 8080
#### The host-based firewall configuration
#### An additional security rule

![](port-8080-ingress-rule.png "")

### Troubleshooting checklist

## Locating the application on the web

### A typical DNS request - local names
### A typical DNS request - global names
### Configuring your own domain
#### Purchase the domain
#### Set up one or more A records
### Examining DNS using _dig_
### Summary
- We’ve installed the application using two VMs
- We can connect to it from the outside world
- We know how to plumb it into DNS if required.

## Extenstions: Scalability
### Adding a load-balancer
![](loadbalancer-schematic.png "")

### Adding a load-balancer through the console

![](07-00-load-balancers.png "")

![](07-01-load-balancer-create-a.png "")

![](07-02-load-balancer-create-b.png "")

![](07-03-load-balancer-created.png "")

![](07-04-backend-sets.png "")

![](07-05-backend-set-create-a.png "")

![](07-06-backend-create-b-health-check.png "")

![](07-07-backend-set-result.png "")

![](07-08-backends.png "")

![](07-09-grab-instance-ocid.png "")

![](07-10-instance-ocid-is-long.png "")

![](07-11-edit-backends.png "")

![](07-12-security-rules-for-backend-a.png "")

![](07-12-security-rules-for-backend-b.png "")

![](07-13-backends-result.png "")

![](07-14-listeners.png "")

![](07-15-create-tcp-listener.png "")

![](07-16-lb-public-ip.png "")

![](07-17-lb-ingress-rules.png "")

![](07-18-edit-8080-offsite-to-80.png "")

### Checking: via IP address
### Optional: checking via DNS name

## Extensions: scaling the persistence layer

## Extensions: service continuity
- Backups! What happens if one of our VMs is destroyed?
- There are various mysql tools that can dump the state of the database to a file.
- We might upload that file to object storage
- A backup plan is not complete without a recovery plan
- A recovery plan doesn’t work unless you’ve tested it
but…

Focus on the question of service availability
- What does my data represent?
- How important is it that it’s fresh (consistent, versus available)?
- How long an outage can I tolerate?
- Of what fraction of data?
- How secure are copies?
- Do I need a transactional history?

## Extensions: regional scalability
Regional Scalability! Does all traffic need to cross the Atlantic?
Approaches like GeoDNS give different results to client depending on where in the world they are
Different A records means that I might be directed to a data centre in London rather than the US.
What are the implications on my application/data architecture?
Can I rely on asynchronous updates?

## Extensions: monitoring
### From within a cluster
### Offsite monitoring
## Extensions: security
### "Let's Encrypt"

# Automation

# Updates
Updates: probably the most important aspect.
- “What about this critical OS update?”
  - Are you going to patch, or blow away and redeploy?
- “How do I change my application?”
- What about database schemas?
  - Hibernate offers support for database migrations
- Do I need to take everything down to bring up a new version?
- What about the REST API? Is it versioned? Will old clients continue to work? Does that matter?
- How can I manage multi-region updates?

Thinking about this needs to be done early in a design.

## Continuous Deployment in our architecture
