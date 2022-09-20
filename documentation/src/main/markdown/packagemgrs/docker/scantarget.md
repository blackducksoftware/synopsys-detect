# [solution_name]'s scan target

When a Docker image is run; for example, using a docker run command, a container is created that has an initial file system. This initial container file system can be determined in advance from the image without running the actual image. Since the target image is not yet trusted, Docker Inspector does not run the image; that is, it does not create a container from the image, but it does construct the initial container file system.

When [solution_name] invokes both [blackduck_signature_scanner_name], and Docker Inspector because detect.docker.image or detect.docker.tar are set, the target of that [blackduck_signature_scan_act] is the initial container file system constructed by Docker Inspector. The intial container file system is packaged in a way to optimize results from [blackduck_product_name]'s matching algorithms. Rather than directly running the [blackduck_signature_scanner_name] on the initial container file system, [solution_name] runs the [blackduck_signature_scanner_name] on a new image; in other words, the squashed image, constructed using the initial container file system built by Docker Inspector. Packaging the initial container file system in a Docker image triggers matching algorithms within [blackduck_product_name] that optimize match results for Linux file systems.

By default, [solution_name] also runs [blackduck_binary_scan_capability] on the initial container file system.
If your [blackduck_product_name] server does not have [blackduck_binary_scan_capability] enabled, you
should disable [blackduck_binary_scan_capability]. For example, you might set: `--detect.tools.excluded=BINARY_SCAN`.

