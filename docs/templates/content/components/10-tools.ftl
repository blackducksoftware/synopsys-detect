# Tools

Each ${solution_name} run consists of running any applicable ${solution_name} tools.

The available ${solution_name} tools in order of execution, with the corresponding [detect tools property](../../properties/configuration/paths/#detect-tools-included)
value specified in parentheses are:

* [Docker Inspector](../packagemgrs/docker-images.md) (--detect.tools=DOCKER)
* [Bazel](../packagemgrs/bazel.md) (--detect.tools=BAZEL)
* [Detector](detectors.md) (--detect.tools=DETECTOR)
* [${blackduck_signature_scanner_name}](../downloadingandrunning/index.md#running-with-black-duck) (--detect.tools=SIGNATURE_SCAN)
* [${blackduck_binary_scan_capability}](../downloadingandrunning/index.md#running-with-black-duck) (--detect.tools=BINARY_SCAN)
* [${impact_analysis_name}](../downloadingandrunning/index.md#running-with-black-duck) (--detect.tools=IMPACT_ANALYSIS)

The detector tool runs any applicable [detectors](detectors.md).
