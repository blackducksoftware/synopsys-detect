# [solution_name] workflow

When running [solution_name] on a Docker image, you'll probably want to
set *detect.target.type* to *IMAGE*.

When a Docker image is provided and property *detect.target.type* is set to IMAGE, [solution_name] will run:

1. Docker Inspector (on the image)
1. [blackduck_signature_scanner_name] (on the image)
1. [blackduck_binary_scan_capability] (on the image)

When a Docker Image is provided and property *detect.target.type* is set to *SOURCE* (the default), [solution_name] will run:

1. Docker Inspector (on the image)
1. Any applicable detectors (on the source directory)
1. [blackduck_signature_scanner_name] (on the image)
1. [blackduck_binary_scan_capability] (on the image)

[solution_name] by default runs
the [blackduck_signature_scanner_name] on an image built from the "container file system".
This image is referred to as
the squashed image (because it has only one layer, to eliminate the chance of false positives from lower layers).
This scan creates another code location.

[solution_name] by default
runs [blackduck_binary_scan_capability] on the container file system.
Refer to [Synopsys Detect's scan target](#synopsys-detects-scan-target) for more details.
This also creates a code location.

