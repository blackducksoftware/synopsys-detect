# [company_name] [solution_name]'s scan target

When a Docker image is run; for example, using a docker run command, a container is created that has an initial file system. This initial container file system can be determined in advance from the image without running the actual image. Since the target image is not yet trusted, Docker Inspector does not run the image; that is, it does not create a container from the image, but it does construct the initial container file system.

When [detect_product_short] invokes both [bd_product_short], and Docker Inspector because detect.docker.image or detect.docker.tar are set, the target of that [blackduck_signature_scan_act] is the initial container file system constructed by Docker Inspector. The intial container file system is packaged in a way to optimize results from [bd_product_short]'s matching algorithms. Rather than directly running the [blackduck_signature_scanner_name] on the initial container file system, [detect_product_short] runs the [blackduck_signature_scanner_name] on a new image; in other words, the squashed image, constructed using the initial container file system built by Docker Inspector. Packaging the initial container file system in a Docker image triggers matching algorithms within [bd_product_short] that optimize match results for Linux file systems.

By default, [detect_product_short] also runs [bdba_product_short] on the initial container file system.
If your [bd_product_short] server does not have [bdba_product_short] enabled, you
should disable [bdba_product_short]. For example, you might set: `--detect.tools.excluded=BINARY_SCAN`.

