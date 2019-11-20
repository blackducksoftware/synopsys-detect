# Tools

Each ${solution_name} run consists of running any applicable ${solution_name} tools.

The available ${solution_name} tools (in order of execution, with the corresponding --detect.tools value specified in parentheses) are:

* [${polaris_product_name}](../3-polaris.md) (--detect.tools=POLARIS)
* [Docker Inspector](../advanced/language-and-package-managers/docker-images.md) (--detect.tools=DOCKER)
* [Bazel](../advanced/language-and-package-managers/bazel.md) (--detect.tools=BAZEL)
* [Detector](detectors.md) (--detect.tools=DETECTOR)
* [${blackduck_product_name} signature scanner](../3-blackduck.md) (--detect.tools=SIGNATURE_SCAN)
* [${blackduck_product_name} binary scanner](../3-blackduck.md) (--detect.tools=BINARY_SCAN)

The detector tool runs any applicable [detectors](detectors.md)
