# Supported image formats

Images passed to [solution_name] via the *detect.docker.image* property must either be pullable using the machine's docker engine (via the equivalent of a "docker pull" command) or already exist in the local docker cache. [solution_name] will save these to a file using the equivalent of a "docker save" command.

Images passed to [solution_name] via the *detect.docker.image.id* property must already exist in the local docker cache. [solution_name] will save these to a file using the equivalent of a "docker save" command.

Image files passed to [solution_name] via the *detect.docker.tar* property must be .tar files, and the contents must conform to either of the following image format specifications: 1. [Docker Image Specification v1.2.0](https://github.com/moby/moby/blob/master/image/spec/v1.2.md) (the format produced by the "docker save" command), or 2. [Open Container Initiative Image (OCI) Format Specification](https://github.com/opencontainers/image-spec/blob/main/spec.md).

