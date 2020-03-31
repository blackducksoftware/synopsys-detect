# Tools

Each ${solution_name} run consists of running any applicable ${solution_name} tools.

The available ${solution_name} tools in order of execution, with the corresponding [detect tools property](../properties/configuration/paths/#detect-tools-included)
value specified in parentheses are:

* [${polaris_product_name}](../30-running/#running-with-polaris) (--detect.tools=POLARIS)
* [Docker Inspector](../advanced/language-and-package-managers/docker-images/) (--detect.tools=DOCKER)
* [Bazel](../advanced/language-and-package-managers/bazel/) (--detect.tools=BAZEL)
* [Detector](../detectors/) (--detect.tools=DETECTOR)
* [${blackduck_signature_scanner_name}](../30-running/#running-with-black-duck) (--detect.tools=SIGNATURE_SCAN)
* [${blackduck_binary_scanner_name}](../30-running/#running-with-black-duck) (--detect.tools=BINARY_SCAN)

The detector tool runs any applicable [detectors](../detectors/).
