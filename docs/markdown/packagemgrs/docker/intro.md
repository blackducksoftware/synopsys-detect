# Docker image support

On Linux, Mac, and Windows 10 Enterprise, [solution_name] can invoke Docker Inspector to inspect Linux Docker images to discover packages installed by the Linux package manager.
For simple use cases, add ```--detect.docker.image={repo}:{tag}```, ```--detect.docker.tar={path to an image archive}```,
```--detect.docker.image.id={image id}```,
to the [solution_name] command line.

The documentation for Docker Inspector is available [here](https://synopsys.atlassian.net/wiki/spaces/INTDOCS/pages/187596884/Black+Duck+Docker+Inspector).

When passed a value for *detect.docker.image*, *detect.docker.image.id*, or *detect.docker.tar*,
[solution_name] runs Docker Inspector on the given image (the "target image"),
creating one code location.
