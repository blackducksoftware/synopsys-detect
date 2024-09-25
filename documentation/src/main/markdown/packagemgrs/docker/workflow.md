# [detect_product_long] workflow

When running [detect_product_long] on a Docker image, you'll probably want to
set *detect.target.type* to *IMAGE*.

When a Docker image is provided and property *detect.target.type* is set to IMAGE, [detect_product_short] will run:

1. Docker Inspector (on the image)
1. [blackduck_signature_scanner_name] (on the image)
1. [bdba_product_short] (on the image)

When a Docker Image is provided and property *detect.target.type* is set to *SOURCE* (the default), [detect_product_short] will run:

1. Docker Inspector (on the image)
1. Any applicable detectors (on the source directory)
1. [blackduck_signature_scanner_name] (on the image)
1. [bdba_product_short] (on the image)

[detect_product_short] by default runs
the [blackduck_signature_scanner_name] on an image built from the "container file system".
This image is referred to as
the squashed image (because it has only one layer, to eliminate the chance of false positives from lower layers).
This scan creates another code location.

[detect_product_short] by default
runs [bdba_product_short] on the container file system.
Refer to [Detect's scan target](#black-duck-detects-scan-target) for more details.
This also creates a code location.

