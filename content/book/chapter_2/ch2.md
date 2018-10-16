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

Before we look at configuring the individual VMs that we 've brought up, let's have a look at an overall picture.

Using the console, each of the resources created during the above process can be examined; but it can be helpful to have a visual interpretation of the result of these steps, to see how the pieces fit together.

![](03-04-resulting-layout.png "")

In this instance, we've booted two VMs. Those have individual network connections (called _VNICs_) onto the same _subnet_. Each VNIC has its own unique private IP address.

Each subnet has a set of _security rules_ associated with it. These can be thought of as a firewall that sits in front of each VNIC. The _security rules_ are comprised of _ingress_ and _egress_ rules. Ingress rules apply to all traffic that flows from the subnet onto the host; egress rules apply to all traffic flowing from a host's VNIC onto the subnet. (That is, the terms _ingress_ and _egress_ are relative to the host.)

Attached to the subnet is a _router_; this directs traffic (IP packets) between subnets. The router also has an IP address on that subnet; this is often referred to as a _gateway address_, since hosts on that subnet will direct traffic destined for elsewhere to that address as a 'first hop'.

The router forwards traffic by consulting a _route table_. The route table contains a list of next-hop _routes_ (there are implicit routes inserted into this table for each subnet that the router is attached to).

The default entry in the route table targets an _internet gateway_. It is this component which conceptually permits traffic to flow between the (privately addressed) subnets and the internet at large.

Each VM is configured with its internal, _private IP address_; however, those addresses are not routable over the internet. The _public IP address_ associated with a VNIC is not configured into the host's operating system. In order to understand how traffic gets to and from the internet, we need to understand how a TCP connection operates.

### Aside: the anatomy of a TCP connection

Each host involved in a TCP connection has (at least) one IP address. (Hosts that are connected to more than one subnet are said to be _multi-homed_; a router is simply a multi-homed device that can forward packets from one subnet to another.)

![](tcp-01.png "")

A _server_ may have several _services_ running on it. Each _listens_ on a unique _port_. Typically, well-known services have port numbers assigned to them. A port number lies in the range 1-65,535. Well-known ports are typically at the low end of that range.

Here, we see the server listening on port 22, for the ssh service, and on port 80 (the unencrypted HTTP port).

![](tcp-02.png "")

A client might want to make several outgoing calls to the same service at the same time. To distinguish them, the client also allocates an _ephemeral port_ (typically from higher in the available range).

![](tcp-03.png "")

A connection is identified by a 4-tuple: (_local address, local port, remote address, remote port_).  The local and remote ends of a TCP connection appear in a table maintained by the operating system of each host.

All TCP packets carry these addresses: both source, and destination.

![](tcp-04.png "")

Traffic from the client carries the source and destination information one way around...

![](tcp-05.png "")

... and the return traffic carries it the other way around.

![](tcp-06.png "")

#### The Interget Gateway: Network Address Translation, or _NAT_.

When packets traverse the internet gateway, it rewrites the corresponding destination address (or source address, for outbound packets), replacing the public IP address with its corresponding private one.

![](tcp-07.png "")

![](tcp-08.png "")

Outbound traffic gets the dual treatment.

![](tcp-09.png "")

![](tcp-10.png "")

#### Inspecting the state of a VM's connection table

Armed with the above information, we can inspect these connection tables using the command `netstat`.

In the following except, we can see two server processes waiting for incoming client connections, together with an established outgoing connection to the ssh port on a remote server.

    % netstat -an46
    Active Internet connections (servers and established)
    Proto Recv-Q Send-Q Local Address           Foreign Address         State      
    tcp        0      0 127.0.0.1:3306          0.0.0.0:*               LISTEN     
    tcp        0      0 0.0.0.0:22              0.0.0.0:*               LISTEN     
    tcp        0      0 192.168.99.4:44112      192.168.99.2:22         ESTABLISHED


There are other commands that’ll give you this information, but this is a common one, although its command-line parameters vary depending on the operating system. On a Mac: use  `netstat -anf inet`.

### Aside: the runtime configuration of the VM

A VM is booted from a copy of an _operating system image_. That image has fixed contents (filesystem layout, boot-time daemons, etc.) However, each VM instance is differentiated from the next with a small number of run-time parameters that are typically injected during the boot process. In particular, each VM has its own IP address; this must be configured into the operating system somehow. Additionally, the public half of the ssh keypair that was supplied during instance creation needs to be installed in the filesystem in order to be effective.

#### DHCP

There is a two-stage process that takes care of this. The first part of the process takes care of network configuration. (This is the same mechanism used by a laptop to connect to your favourite cafe's fre wifi.) The protocol used is called _DHCP_.

Initially, the host does not know its IP address. It sends a broadcast packet asking for configuration information. That packet is received by the DHCP server. This is a _layer-2 broadcast_, which typically means that the DHCP server must be homed on the same subnet to receive the packet.

![](dhcp-01.png "")

In response, the DHCP server can look up (or dynamically allocate) an IP address for the host. The response is sent directly to the host (that is, it doesn't use the typical IP mechanism for locating the host on a local subnet). This response packet contains other _DHCP options_ that tell the host how to configure itself: information about the subnet, the subnet's _maximum transmission unit_ (that is, the size of the largest packet that the subnet will accept), information about DNS (that is, the location of a local name-server), and so on.

This information comes with a _lease time_. The client host must renew its lease within that period if it wishes to retain the same IP address.

![](dhcp-02.png "")

On receipt, the host configures its low-level network information.

![](dhcp-03.png "")

At this stage, the second part of the boot-time configuration can proceed.

#### Other host metadata

Here OS images for instances in the cloud differ from the contents of your laptop. Each image is designed to be booted in a cloud environment - possibly by an automated process - and must therefore be able to continue the configuration process automatically.

There are a small number of equivalent approaches to doing this that are typified by _cloud-init_. Here, the cloud provider and the OS image follow a conventional contract: if the VM makes an HTTP request to a given address, the cloud provider will serve up metadata in response that details the VM's specific configuration.

![](cloud-init-01.png "")

![](cloud-init-02.png "")

That metadata is interpreted by the boot-time process; it can cause additional configuration files to be written into the VM's filesystem, additional packages to be installed, additional user accounts to be created, and so on. One typical piece of configuration information is an _ssh key_ that is associated with the _default user_ baked into the OS image.

You'll be able to see your own ssh public key in your instance, in the `~/.ssh/authorized_keys` file, after you log into it.

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
