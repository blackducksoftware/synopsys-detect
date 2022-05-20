# Deploying [docker_inspector_name]

[docker_inspector_name] can be run in either of the following modes:

1. Host mode on a server or virtual machine (VM) with Docker. In this mode, [docker_inspector_name] starts and stops the image inspector services it uses. The deployment approach for host mode is referred to below as "utility;" you simply execute a command, and deployment is automatic.
2. Container mode utilizing either Docker or a container running on an orchestration platform such as Kubernetes, OpenShift, among others. In this mode, you start the image inspector services, and [docker_inspector_name] just sends them requests, which complete much faster. The deployment approach for container mode is referred to below as "toolkit;" you take components provided by Docker Inspector (one utility, three containerized services) and deploy them yourself.

Most, but not all, of the following deployment examples use the toolkit approach.

## Important notes regarding deployment sample code

The deployment samples provided are intended to illustrate possible approaches to the challenges
involved in deploying [docker_inspector_name]. They are not intended to be used as-is in production.
You should understand the code before you use it. They do not represent the only way to deploy in each environment.
At a minimum, before using a sample, you should update it to the latest versions of the software it uses.

Your deployment approach is the same whether you are invoking [docker_inspector_name] directly, or invoking it using [solution_name].
Most of the sample deployments use [solution_name] simply because that is the most common use case.

## Using host mode on a server or VM with Docker

In this scenario, [docker_inspector_name] is a command line utility that automatically pulls/runs and uses container-based services,  
and cleans them up when it's done. The Docker command, if installed on the machine, can be very useful for troubleshooting, but is not actually
required or used by [docker_inspector_name].

In this mode, [docker_inspector_name] requires access to a Docker Engine, similar to the way the Docker client requires
access to a Docker Engine, so it can pull and run Docker images. It uses the
[docker-java](https://github.com/docker-java/docker-java)
library to perform Docker operations using the Docker Engine.

In this mode, [docker_inspector_name] automatically pulls, runs, stops, and removes the container-based image inspector services
on which it depends. It accesses the services they provide through HTTP GET operations.

This is the default mode, and the simplest to use.

## Using container mode with Docker or a container orchestration platform such as Kubernetes, OpenShift, and others

In this scenario, [docker_inspector_name] is a toolkit consisting of a command line utility that you run in one container, plus
three container-based services which you must start. These four containers must:
(a) Share a mounted volume, either persistent or temporary, used to pass large files between containers, and:
(b) Be able to reach each other through HTTP GET operations using base URLs that you provide.

Because in this mode you (not [docker_inspector_name]) are deploying the image inspector services,
you must ensure that you deploy the correct version of the image inspector images for the
version of [docker_inspector_name] that you run. This is easier if you explicitly control the version of
[docker_inspector_name], rather than letting [solution_name] auto-update [docker_inspector_name].
See the [solution_name] for details.
 
## Image Inspector Services

[docker_inspector_name] consists of a command line utility provided in a Java .jar, but sometimes invoked using a bash script,
and three image inspector services.

The required Docker operations, if any, are performed by the command line utility, while the image inspector services
perform the work of unpacking the target Docker image, extracting the Linux package manager database,
and running the Linux package manager against that database to extract installed packages
and translate them to components which are actually externalIDs for Black Duck. If the image inspector service
finds in the target image a package manager database that is incompatible with its own package manager utility; for example, 
when you run [docker_inspector_name] on an Alpine image, but the request goes to the
Ubuntu image inspector service, the image inspector service redirects the request to the appropriate
image inspector service. You can change the default image inspector service to reduce the likelihood
of redirects, resulting in shorter execution times. For example, if most of your target images are Alpine,
you can set *imageinspector.service.distro.default* to *alpine*.

The image inspector service containers are downloaded from Docker Hub ([image_repo_organization]/[imageinspector_image_name_base]-*).

## Deployment sample for Docker using persistent image inspector services

Approach: Toolkit

Deployment notes: 
[docker_inspector_name] runs on a host that has Docker.
Each image inspector service runs in a container.
The shared volume is a directory on the host, mounted into each container.
The containers communicate through service URLs.

Download: curl -O [source_raw_content_url_base]/[source_repo_organization]/[docker_inspector_project_name]/master/deployment/docker/runDetectAgainstDockerServices/setup.sh

## Deployment sample for Kubernetes

Approach: Toolkit

Deployment notes: Each image inspector service runs in a separate pod.
The shared volume is a hostPath volume. The containers communicate through service URLs.

Download: curl -O [source_raw_content_url_base]/[source_repo_organization]/[docker_inspector_project_name]/master/deployment/kubernetes/setup.txt

## Deployment sample for OpenShift

Approach: Toolkit

Deployment notes: All image inspector services run in a single pod. The shared volume is an *emptyDir* volume.
The containers communicate through localhost URLs.

Download: curl -O [source_raw_content_url_base]/[source_repo_organization]/[docker_inspector_project_name]/master/deployment/openshift/setup.txt

## Deployment sample for Travis CI

Approach: Toolkit

Deployment notes: Uses the Travis CI Docker service.
The containers communicate through localhost URLs.

Download: curl -O [source_raw_content_url_base]/[source_repo_organization]/[docker_inspector_project_name]/master/deployment/travisci/travis.yml

## Deployment sample for GitLab CI

Approach: Toolkit

Deployment notes: Uses the GitLab CI shell executor.
The containers communicate through localhost URLs.

Download: curl -O [source_raw_content_url_base]/[source_repo_organization]/[docker_inspector_project_name]/master/deployment/gitlabci/setup.sh

## Deployment sample for Circle CI

Approach: Utility

Deployment notes: 

Download: curl -O [source_raw_content_url_base]/[source_repo_organization]/[docker_inspector_project_name]/master/deployment/circleci/config.yml

## Deployment sample for Docker with [solution_name] running in a container

Approach: Toolkit

Deployment notes: The containers communicate through localhost URLs.

Download: curl -O [source_raw_content_url_base]/[source_repo_organization]/[docker_inspector_project_name]/master/deployment/docker/runDetectInContainer/setup.sh			

## Configuring [docker_inspector_name] for your Docker registry

If you invoke [docker_inspector_name] with an image reference versus an image that is saved to a .tar file,
it uses the
[docker-java](https://github.com/docker-java/docker-java)
library
to access the Docker registry to pull the image. 

If *docker pull {targetimage]* works from the command line, then [docker_inspector_name] is also able
to pull that image, because docker-java can be configured the same way as the Docker command line utility. 

There are other ways to configure docker-java. For more information on configuring docker-java
and [docker_inspector_name] for your Docker registry, refer to the configuration information in
[docker-java documentation](https://github.com/docker-java/docker-java/blob/master/docs/getting_started.md).

[docker_inspector_name] does not override any of the configuration settings in the code,
so any of the other methods (properties, system properties, system environment) work.

If you choose to use environment variables, and you are calling [docker_inspector_name] from [solution_name],
you must prefix the environment variable names with *DETECT_DOCKER_PASSTHROUGH_* to
instruct [solution_name] to pass them on to [docker_inspector_name].
In that scenario, instead of *export SOMENAME=value*, use *export DETECT_DOCKER_PASSTHROUGH_SOMENAME=value*.

If you choose to use system properties which are normally set using *java -D*,
and you are calling [docker_inspector_name] from [solution_name], you must
put the properties in a file; for example, *mydockerproperties.properties*, and use 
```
--detect.docker.passthrough.system.properties.path=mydockerproperties.properties
```
to point [docker_inspector_name] to those property settings.
