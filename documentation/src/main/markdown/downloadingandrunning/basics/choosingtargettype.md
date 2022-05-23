# Choosing the target type

[solution_name] will select a workflow based in part on the target type you select via the *detect.target.type* property.

When running [solution_name] on project source code, you'll probably want to
set *detect.target.type* to *SOURCE*, or leave *detect.target.type* unset (since
*SOURCE* is the default value).

When running [solution_name] on a Docker image, you'll probably want to
set *detect.target.type* to *IMAGE*.

## Common workflows

By default (detect.target.type=SOURCE), [solution_name] will run the following on the source directory:

1. Any applicable detectors
1. [blackduck_signature_scanner_name]

When a Docker image is provided and property *detect.target.type* is set to IMAGE, [solution_name] will run the following on the image:

1. Docker Inspector
1. [blackduck_signature_scanner_name]
1. [blackduck_binary_scan_capability]
