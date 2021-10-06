# Docker image support

On Linux, Mac, and Windows 10 Enterprise, ${solution_name} can invoke Docker Inspector to inspect Linux Docker images to discover packages installed by the Linux package manager.
For simple use cases, add ```--detect.docker.image={repo}:{tag}```, ```--detect.docker.tar={path to an image archive}```,
```--detect.docker.image.id={image id}```,
to the ${solution_name} command line.

The documentation for Docker Inspector is available [here](https://synopsys.atlassian.net/wiki/spaces/INTDOCS/pages/187596884/Black+Duck+Docker+Inspector).

When passed a value for *detect.docker.image*, *detect.docker.image.id*, or *detect.docker.tar*,
${solution_name} runs Docker Inspector on the given image (the "target image"),
creating one code location.

## Supported image formats

Images passed to ${solution_name} via the *detect.docker.image* property must either be pullable using the machine's docker engine (via the equivalent of a "docker pull" command) or already exist in the local docker cache. ${solution_name} will save these to a file using the equivalent of a "docker save" command.

Images passed to ${solution_name} via the *detect.docker.image.id* property must already exist in the local docker cache. ${solution_name} will save these to a file using the equivalent of a "docker save" command.

Image files passed to ${solution_name} via the *detect.docker.tar* property must be .tar files, and the contents must conform to either of the following image format specifications: 1. [Docker Image Specification v1.2.0](https://github.com/moby/moby/blob/master/image/spec/v1.2.md) (the format produced by the "docker save" command), or 2. [Open Container Initiative Image (OCI) Format Specification](https://github.com/opencontainers/image-spec/blob/main/spec.md).

## ${solution_name} workflow

When running ${solution_name} on a Docker image, you'll probably want to
set *detect.target.type* to *IMAGE*.

When a Docker image is provided and property *detect.target.type* is set to IMAGE, ${solution_name} will run:

1. Docker Inspector (on the image)
1. ${blackduck_signature_scanner_name} (on the image)
1. ${blackduck_binary_scan_capability} (on the image)

When a Docker Image is provided and property *detect.target.type* is set to *SOURCE* (the default), ${solution_name} will run:

1. Docker Inspector (on the image)
1. Any applicable detectors (on the source directory)
1. ${blackduck_signature_scanner_name} (on the image)
1. ${blackduck_binary_scan_capability} (on the image)

${solution_name} by default runs
the ${blackduck_signature_scanner_name} on an image built from the "container file system".
This image is referred to as
the squashed image (because it has only one layer, to eliminate the chance of false positives from lower layers).
This scan creates another code location.

${solution_name} by default
runs ${blackduck_binary_scan_capability} on the container file system.
Refer to [${solution_name}'s scan target](#scantarget) for more details.
This also creates a code location.

## File permissions

When using Docker Inspector, ${solution_name} must be run in an environment configured so that files created
by Docker Inspector are readable by all. On Linux, this means an appropriate umask value
(for example, 002 or 022 will work). On Windows, this means that the ${solution_name}
output directory must be readable by all.

Docker image tarfiles passed to ${solution_name} via the *detect.docker.tar* property must be readable by all.

## Passing Docker Inspector property values to Docker Inspector from ${solution_name}

For more complex use cases, you may need to pass Docker Inspector property values to Docker Inspector using ${solution_name}. To do this, construct the ${solution_name} property name by prefixing the Docker Inspector property name with ```detect.docker.passthrough.```.

For example, suppose you need to set Docker Inspector's service.timeout value (the length of time Docker Inspector waits for a response from the Image Inspector services that it uses) to 480000 milliseconds. You add the prefix to the Docker Inspector property name to derive the ${solution_name} property name ```detect.docker.passthrough.service.timeout```. Therefore, add ```--detect.docker.passthrough.service.timeout=480000``` to the ${solution_name} command line.

For example:
```
./detect7.sh --detect.docker.image=ubuntu:latest --detect.docker.passthrough.service.timeout=480000
```

You can set any Docker Inspector property using this method.
However, you usually should not override the values of the following Docker Inspector properties (which ${solution_name} sets)
because changing their values is likely to interfere with ${solution_name}'s ability to work with Docker Inspector:

* output.path
* output.include.squashedimage
* output.include.containerfilesystem
* upload.bdio

<a name="scantarget"></a>
## ${solution_name}'s scan target

When a Docker image is run; for example, using a `docker run` command, a container is created. That container has a file system; in other words, the container file system. The container file system at the instant the container is created; in other words, the initial container file system, can be determined in advance from the image without running the image. Because the target image is not yet trusted, Docker Inspector does not run the image; that is, it does not create a container from the image, but it does construct the initial container file system, which is the file system a container has at the instant it is created.

When ${solution_name} invokes both Docker Inspector because either detect.docker.image or detect.docker.tar is set, and the ${blackduck_signature_scanner_name}, as it does by default, the target of that ${blackduck_signature_scan_act} is the initial container file system constructed by Docker Inspector, packaged in a way to optimize results from ${blackduck_product_name}'s matching algorithms. Rather than directly running the ${blackduck_signature_scanner_name} on the initial container file system, ${solution_name} runs the ${blackduck_signature_scanner_name} on a new image; in other words, the squashed image, constructed using the initial container file system built by Docker Inspector. Packaging the initial container file system in a Docker image triggers matching algorithms within ${blackduck_product_name} that optimize match results for Linux file systems.

In earlier versions of ${solution_name} / Docker Inspector, ${solution_name} ran the ${blackduck_signature_scanner_name} directly on the target image. This approach had the disadvantage of potentially producing false positives under certain circumstances. For example, suppose your target image consists of multiple layers. If version 1 of a package is installed in layer 0, and then replaced with a newer version of that package in layer 1, both versions exist in the image, even though the initial container file system only includes version 2. A ${blackduck_signature_scan_act} of the target image shows both versions, even though version 1 has been effectively replaced with version 2. The current ${solution_name} / Docker Inspector functionality avoids this potential for false positives.

By default, ${solution_name} also runs ${blackduck_binary_scan_capability} on the initial container file system.
If your ${blackduck_product_name} server does not have ${blackduck_binary_scan_capability} enabled, you
should disable ${blackduck_binary_scan_capability}. For example, you might set: *--detect.tools.excluded=BINARY_SCAN*.

## Isolating application components

If you are interested in components from the application layers of your image, but not interested in components from the underlying platform layers, you can exclude components from platform layers from the results.

For example, if you build your application on ubuntu:latest (your Dockerfile starts with FROM ubuntu:latest), you can exclude components from the Ubuntu layer(s) so that the components generated by ${solution_name} using Docker Inspector and the ${blackduck_signature_scanner_name} contain only components from your application layers.

First, find the layer ID of the platform's top layer. To do this task:

Run the docker inspect command on the base image; in our example this is ubuntu:latest.
Find the last element in the RootFS.Layers array. This is the platform top layer ID. In the following example, this is sha256:b079b3fa8d1b4b30a71a6e81763ed3da1327abaf0680ed3ed9f00ad1d5de5e7c.
Set the value of the Docker Inspector property docker.platform.top.layer.id to the platform top layer ID. For example:

./detect7.sh ... --detect.docker.image={your application image} --detect.docker.platform.top.layer.id=sha256:b079b3fa8d1b4b30a71a6e81763ed3da1327abaf0680ed3ed9f00ad1d5de5e7c

In this mode, there may be some loss in match accuracy from the ${blackduck_signature_scanner_name} because, in this scenario, the ${blackduck_signature_scanner_name} may be deprived of some contextual information, such as the operating system files that enable it to determine the Linux distribution, and that that may negatively affect its ability to accurately identify components.

## Inspecting Windows Docker images

Given a Windows Image, Docker Inspector, since it can only discover packages using
a Linux package manager will not contribute any components to the BOM, but will
return the container filesystem (in the form of a squashed image),
which ${solution_name} will scan using the ${blackduck_signature_scanner_name}.

## Inspecting Docker images on Windows

This section contains considerations that apply when running on Windows.

### Docker usually needs to be configured for Linux containers

By default, Docker Inspector pulls (using Docker) and uses container-based image inspector services.
These services run on Linux operating systems.
On Windows, Docker must be configured for Linux containers in order for these Linux-based images
to be pulled and started by Docker Inspector.

Docker Inspector also supports more advanced deployment options that avoid this requirement by using
image inspector services running elsewhere in the network;
for more information, refer to the Docker Inspector documentation.

### Docker must be able to mount the shared directory as a volume

Docker Inspector uses Docker to mount a shared directory (a directory inside ${solution_name}'s output directory,
which is controlled via property detect.output.path)
so that it can be accessed (read from and written to) by the image inspector container.
Consequently Docker must have permissions sufficient to mount read/write directories
within ${solution_name}'s output directory.

### The user must have permission to create symbolic links

Docker Inspector constructs the initial file system that a container would have if you ran
the target image. All component discovery (including package manager discovery
and signature scanning) is based on this file system. In order to construct the file
system accurately, the ${solution_name} user must have permission to create symbolic links.

### A Docker bug may affect ${solution_name} during clean up

For important information on a Docker for Windows bug that might affect ${solution_name}, refer to the
[troubleshooting page](../../troubleshooting/solutions/#on-windows-error-trying-cleanup).
