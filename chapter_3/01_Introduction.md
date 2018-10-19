Lecture outline

1. Containers
    1.1 What is a container?
    1.2 Difference between containers and VMs
    1.3 Why are we only talking about Docker?

2. Docker overview
    2.1 The Docker platform
    2.3.Docker Engine
    2.3 What can I use Docker for?
    2.4 Docker architecture

3. Develop with Docker
    3.1 Prepare Docker environment
        3.1.1 Install Docker
        3.1.3.Verify version
        3.1.3 Verify installation (docker run hello-world)
    3.3.Build an image from a Dockerfile
        3.3.1 What is a Dockerfile?
        3.3.3.Start with basic Dockerfile
        3.3.3 Build the app
        3.3.4 Run the app
    3.3 Publish the image
        3.3.1 Publish image to Docker
            a. Log into Docker (hub.docker.com)
            b. Tag image
            c. Publish the image
            d. Pull and run the image from the remote repo
        3.3.3.Publish image to OCI registry
            a. Overview of the OCI registry and user credentails
            b. Log in with Docker to OCI registry
            c. Tag image
            d. Publish the image
            e. Pull and run the image from the remote repo
    3.4 Intaract with a container
        3.4.1 Exec into a container
        3.4.2 Test out container connectivity with the outside world

4. Use multi-stage builds
    4.1 Why use multi-stage builds?
    4.2 Use multi-stage builds
    4.3 Cleanup: Name build stages
    4.4 Stop at a specific build stage (optional)
    4.5 Use an external image as a stage

5. Managing application data
    5.1 Storage options (volumes, bind mounts and tmpfs)
    5.2 Volumes
        5.2.1 Descripton for volumes, what are they useful for?
        5.2.2 Instances where volumes should be used
        5.2.3 Create a volume and attach to the container
    5.3 Bind mounts
        5.2.1 Descripton for bind mounts, what are they useful for?
        5.2.2 Instances where bind mounts should be used
        5.2.3 Exemplify how a directory on the host machine is mounted into a container
    5.4 Tmpfs
        5.2.1 Descripton for tmpfs mounts, what is it useful for?
        5.2.2 Instances where tmpfs mounts should be used
        5.2.3 Use a tmpfs mount in a container

6. Run application in production
    6.1 Configure the daemon (dockerrd)
    6.2 Configure the containers
        6.2.1 Start containers automatically

