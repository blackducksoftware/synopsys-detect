# Tools

Each [solution_name] run consists of running any applicable [solution_name] tools.

The available [solution_name] tools in order of potential execution, with the corresponding [detect tools property](../properties/configuration/paths.md#detect-tools-included)
value specified in parentheses are:

* [Docker Inspector](../packagemgrs/docker/intro.md) (--detect.tools=DOCKER)
* [Bazel](../packagemgrs/bazel.md) (--detect.tools=BAZEL)
* [Detector](detectors.md) (--detect.tools=DETECTOR)
* [Black Duck Signature Scanner](../runningdetect/basics/runningwithblackduck.md) (--detect.tools=SIGNATURE_SCAN)
* [Black Duck - Binary Analysis](../runningdetect/basics/runningwithblackduck.md) (--detect.tools=BINARY_SCAN)
* [Vulnerability Impact Analysis Tool](../runningdetect/basics/runningwithblackduck.md) (--detect.tools=IMPACT_ANALYSIS)
* [IaC Scanner](../runningdetect/basics/runningwithblackduck.md) (--detect.tools=IAC_SCAN)
* [Container Scanning](../runningdetect/containerscanning.md) (--detect.container.scan.file.path=<Path to local or URL for remote container>)

The detector tool runs any applicable [detectors](detectors.md).
