# Tools

Each [company_name] [solution_name] run consists of running any applicable [company_name] [solution_name] tools.

The available [company_name] [solution_name] tools in order of potential execution, with the corresponding [detect tools property](../properties/configuration/paths.md#detect-tools-included)
value specified in parentheses are:

* [Docker Inspector](../packagemgrs/docker/intro.md) (--detect.tools=DOCKER)
* [Bazel](../packagemgrs/bazel.md) (--detect.tools=BAZEL)
* [Detector](../components/detectors.dita) (--detect.tools=DETECTOR)
* [Black Duck Signature Scanner](../runningdetect/basics/runningwithblackduck.md) (--detect.tools=SIGNATURE_SCAN)
* [Black Duck - Binary Analysis](../runningdetect/basics/runningwithblackduck.md) (--detect.tools=BINARY_SCAN)
* [Vulnerability Impact Analysis Tool](../runningdetect/basics/runningwithblackduck.md) (--detect.tools=IMPACT_ANALYSIS)
* [IaC Scanner](../runningdetect/basics/runningwithblackduck.md) (--detect.tools=IAC_SCAN)
* [Container Scan](../runningdetect/containerscanning.md) (--detect.tools=CONTAINER_SCAN)
* [ReversingLabs Scan](../runningdetect/threatintelscan.md) (--detect.threatintel.scan.file.path)

The detector tool runs any applicable [detectors](../components/detectors.dita).
