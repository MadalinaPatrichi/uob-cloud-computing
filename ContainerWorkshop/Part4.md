# Part 4: Running a Container

Let's get started by running a small container locally and then we'll explain the architecture of how Docker works behind the scenes.

There are no specific skills needed for this tutorial beyond a basic comfort with the command line and using a text editor. Prior experience in developing web applications will be helpful but is not required. Follow the instructions below and execute the code blocks in your terminal.

## Prerequsities:

TODO:
- access to the VM (which has docker installed on it already)
- or have docker installed locally on their laptop
- have a dockerhub account
- open up a terminal


TODO:
* Follow this hello world example with busybox image (https://docker-curriculum.com/)

Once you are done installing Docker, test your Docker installation by running the following:

```bash
$ docker run hello-world

Hello from Docker.
This message shows that your installation appears to be working correctly.
...
```

## Wokrshop 1: Playing with BusyBox

Now that we have everything setup, it's time to get our hands dirty. In this section, we are going to run a Busybox container on our system and get a taste of the `docker run` command. 

A Busybox docker image is useful if one is building a container for which busybox can fulfill its dependency chain without needing a full Linux distribution package.

To get started, let's run the following in our terminal:

```bash
$ docker pull busybox
```

> _Note: Depending on how you've installed docker on your system, you might see a permission denied error after running the above command. If you're on a Mac, make sure the Docker engine is running. If you're on Linux, then prefix your docker commands with sudo._

The `pull` command fetches the busybox image from the Docker registry and saves it to our system. You can use the `docker images` command to see a list of all images on your system.

```bash
$ docker images
REPOSITORY              TAG                 IMAGE ID            CREATED             VIRTUAL SIZE
busybox                 latest              c51f86c28340        4 weeks ago         1.109 MB
```
#### Docker Run

Great! Let's now run a Docker container based on this image. To do that we are going to use the almighty `docker run` command.

```bash
$ docker run busybox
$
```

Wait, nothing happened! Is that a bug? Well, no. Behind the scenes, a lot of stuff happened. When you call `run`, the Docker client finds the image (busybox in this case), loads up the container and then runs a command in that container. When we run `docker run busybox`, we didn't provide a command, so the container booted up, ran an empty command and then exited. Well, yeah - kind of a bummer. Let's try something more exciting.

```bash
$ docker run busybox echo "hello from busybox"
hello from busybox
```
Nice - finally we see some output. In this case, the Docker client dutifully ran the `echo` command in our busybox container and then exited it. If you've noticed, all of that happened pretty quickly. Imagine booting up a virtual machine, running a command and then killing it. Now you know why they say containers are fast! Ok, now it's time to see the `docker ps` command. The `docker ps` command shows you all containers that are currently running.

```bash
$ docker ps
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS              PORTS               NAMES
```

Since no containers are running, we see a blank line. Let's try a more useful variant: `docker ps -a`

```bash
$ docker ps -a
CONTAINER ID        IMAGE               COMMAND             CREATED             STATUS                      PORTS               NAMES
305297d7a235        busybox             "uptime"            11 minutes ago      Exited (0) 11 minutes ago                       distracted_goldstine
ff0a5c3750b9        busybox             "sh"                12 minutes ago      Exited (0) 12 minutes ago                       elated_ramanujan
14e5bd11d164        hello-world         "/hello"            2 minutes ago       Exited (0) 2 minutes ago                        thirsty_euclid

```

So what we see above is a list of all containers that we ran. Do notice that the `STATUS` column shows that these containers exited a few minutes ago.

You're probably wondering if there is a way to run more than just one command in a container. Let's try that now:

```bash
$ docker run -it busybox sh
/ # ls
bin   dev   etc   home  proc  root  sys   tmp   usr   var
/ # uptime
 05:45:21 up  5:58,  0 users,  load average: 0.00, 0.01, 0.04
```

Running the `run` command with the `-it` flags attaches us to an interactive tty (terminal = tty = text input/output environment) in the container. Now we can run as many commands in the container as we want. Take some time to run your favorite commands.

> _Danger Zone: If you're feeling particularly adventurous you can try rm -rf bin in the container. Make sure you run this command in the container and not in your laptop/desktop. Doing this will not make any other commands like ls, echo work. Once everything stops working, you can exit the container (type exit and press Enter) and then start it up again with the docker run -it busybox sh command. Since Docker creates a new container every time, everything should start working again._

To find out more about run, use `docker run --help` to see a list of all flags it supports. As we proceed further, we'll see a few more variants of `docker run`.

Before we move ahead though, let's quickly talk about deleting containers. We saw above that we can still see remnants of the container even after we've exited by running `docker ps -a`. Throughout this tutorial, you'll run docker run multiple times and leaving stray containers will eat up disk space. Hence, as a rule of thumb, I clean up containers once I'm done with them. To do that, you can run the `docker rm` command. Just copy the container IDs from above and paste them alongside the command.

```bash
$ docker rm 305297d7a235 ff0a5c3750b9
305297d7a235
ff0a5c3750b9
```
On deletion, you should see the IDs echoed back to you. If you have a bunch of containers to delete in one go, copy-pasting IDs can be tedious. In that case, you can simply run:

```bash
$ docker rm $(docker ps -a -q -f status=exited)
```
This command deletes all containers that have a status of exited. In case you're wondering, the -q flag, only returns the numeric IDs and -f filters output based on conditions provided.

Continue to [Part 5](Part5.md)



