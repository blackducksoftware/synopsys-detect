# Inspecting Docker images on Windows

This section contains considerations that apply when running on Windows.

## Docker usually needs to be configured for Linux containers

By default, Docker Inspector pulls (using Docker) and uses container-based image inspector services.
These services run on Linux operating systems.
On Windows, Docker must be configured for Linux containers in order for these Linux-based images
to be pulled and started by Docker Inspector.

Docker Inspector also supports more advanced deployment options that avoid this requirement by using
image inspector services running elsewhere in the network;
for more information, refer to the Docker Inspector documentation.

## Docker must be able to mount the shared directory as a volume

Docker Inspector uses Docker to mount a shared directory (a directory inside [solution_name]'s output directory,
which is controlled via property detect.output.path)
so that it can be accessed (read from and written to) by the image inspector container.
Consequently Docker must have permissions sufficient to mount read/write directories
within [solution_name]'s output directory.

## The user must have permission to create symbolic links

Docker Inspector constructs the initial file system that a container would have if you ran
the target image. All component discovery (including package manager discovery
and signature scanning) is based on this file system. In order to construct the file
system accurately, the [solution_name] user must have permission to create symbolic links.

## A Docker bug may affect [solution_name] during clean up

For important information on a Docker for Windows bug that might affect [solution_name], refer to the
[troubleshooting page](../../troubleshooting/solutions.md#on-windows-error-trying-cleanup).
