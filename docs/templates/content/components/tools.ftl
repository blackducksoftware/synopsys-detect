# Tools

Each ${solution_name} run consists of running any applicable ${solution_name} tools.

The available ${solution_name} tools (in order of execution, with the corresponding --detect.tools value specified in parentheses) are:

* [Polaris](/polaris) (--detect.tools=POLARIS)
* [Docker Inspector](/advanced/language-and-package-managers/docker-images) (--detect.tools=DOCKER)
* [Bazel](/advanced/language-and-package-managers/bazel) (--detect.tools=BAZEL)
* [Detector](/components/detectors) (--detect.tools=DETECTOR)
* [Black Duck signature scanner](/properties/Configuration/signature%20scanner) (--detect.tools=SIGNATURE_SCAN)
* [Black Duck binary scanner](/properties/Configuration/signature%20scanner) (--detect.tools=BINARY_SCAN)

The detector tool runs any applicable [detectors](detectors.md)
