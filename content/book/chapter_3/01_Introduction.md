Lecture outline

1. Docker overview
    1.1 The Docker platform
    1.3.Docker Engine
    1.3 What can I use Docker for?
    1.4 Docker architecture

2. Difference between containers and VMs

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

4. Managing application data
    4.1 Storage options (volumes, bind mounts and tmpfs)
    4.2 Volumes
        4.2.1 Descripton for volumes, what are they useful for?
        4.2.2 Instances where volumes should be used
        4.2.3 Create a volume and attach to the container
    4.3 Bind mounts
        4.2.1 Descripton for bind mounts, what are they useful for?
        4.2.2 Instances where bind mounts should be used
        4.2.3 Exemplify how a directory on the host machine is mounted into a container
    4.4 Tmpfs
        4.2.1 Descripton for tmpfs mounts, what is it useful for?
        4.2.2 Instances where tmpfs mounts should be used
        4.2.3 Use a tmpfs mount in a container
