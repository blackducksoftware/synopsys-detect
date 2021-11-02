# Tools

Each [solution_name] run consists of running any applicable [solution_name] tools.

The available [solution_name] tools in order of execution, with the corresponding [detect tools property](../properties/configuration/paths.md#detect-tools-included)
value specified in parentheses are:

* [Docker Inspector](../packagemgrs/docker-images.md) (--detect.tools=DOCKER)
* [Bazel](../packagemgrs/bazel.md) (--detect.tools=BAZEL)
* [Detector](detectors.md) (--detect.tools=DETECTOR)
* [Black Duck Signature Scanner](../downloadingandrunning/overview.md#running-with-black-duck) (--detect.tools=SIGNATURE_SCAN)
* [Black Duck - Binary Analysis](../downloadingandrunning/overview.md#running-with-black-duck) (--detect.tools=BINARY_SCAN)
* [Vulnerability Impact Analysis Tool](../downloadingandrunning/overview.md#running-with-black-duck) (--detect.tools=IMPACT_ANALYSIS)

The detector tool runs any applicable [detectors](detectors.md).
