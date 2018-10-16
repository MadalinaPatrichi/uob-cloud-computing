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

![create instance part one](02-00-create-instance-web-with-network.png "Launch compartment, part one")

The second part of the dialog involves selecting the credentials that will be embedded into the running instance. Select `Choose SSH key files` and locate your `~/.ssh/id_rsa.pub` file.

![create instance part two](02-01-create-instance-web-with-network.png "Launch compartment, ssh information")

Finally, we want to create a new set of network resources to use with this VM.

- select the same compartment to host the _virtual cloud network_, or _VCN_.
- call it `net1`
- ensure that public IP addresses are assigned for hosts on that VCN
- then create the instance

![create instance part three](02-02-create-instance-web-with-network.png "Launch compartment, networking")

You should see a detail panel once the instance is booted. It'll look like this:

![create instance result](02-03-create-instance-web-result.png "Launch compartment, result")

Notice that there are two IP addresses listed for the host - a private IP address and a public IP address. It's the latter that we'll use to connect to this VM over the internet.

## Booting the second VM

This process is very similar

![create second_instance](03-00-create-instance-db.png "Launch compartment, result")
![create second_instance](03-00-create-instance-db.png "Launch compartment, result")

03-00-create-instance-db.png
03-01-creat-instance-db.png
03-02-create-instance-db.png
03-03-create-instance-db-result.png

### A note on tags

Resources can be _tagged_ with arbitrary labels. For a small deployment, this may seem unnecessary; however, for larger deployments, it can be useful to identify the various categories that a VM (or network) belongs to. Example divisions might be: _environment_ (staging, production, ...); _cost centre_; or perhaps the particular application that a resource is associated with.