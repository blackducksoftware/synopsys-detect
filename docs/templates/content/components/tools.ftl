# Tools

Each ${solution_name} run consists of running any applicable ${solution_name} tools.

The available ${solution_name} tools (in order of execution, with the corresponding --detect.tools value specified in parentheses) are:

* [Polaris](https://synopsys.atlassian.net/wiki/spaces/INTDOCS/pages/62423113/Synopsys+Detect#SynopsysDetect-#get-start-coverityGettingStartedwithSynopsysDetectforSASTusingCoverity(onPolaris)) (--detect.tools=POLARIS)
* [Docker Inspector](https://synopsys.atlassian.net/wiki/spaces/INTDOCS/pages/62423113/Synopsys+Detect#SynopsysDetect-config-docker-scan) (--detect.tools=DOCKER)
* [Bazel](https://synopsys.atlassian.net/wiki/spaces/INTDOCS/pages/108331118/Synopsys+Detect+Additional+Features#SynopsysDetectAdditionalFeatures-Bazelsupport) (--detect.tools=BAZEL)
* Detector (--detect.tools=DETECTOR): See [Detectors](detectors.md).
* [Black Duck signature scanner](https://synopsys.atlassian.net/wiki/spaces/INTDOCS/pages/62423113/Synopsys+Detect#SynopsysDetect-SignaturescannerinvocationforSCA) (--detect.tools=SIGNATURE_SCAN)
* [Black Duck binary scanner](https://synopsys.atlassian.net/wiki/spaces/INTDOCS/pages/62423113/Synopsys+Detect#SynopsysDetect-get-start-BAGettingStartedwithSynopsysDetectforSCAofbinaryfilesusingBlackDuckBinaryAnalysis) (--detect.tools=BINARY_SCAN)

The detector tool runs any applicable [detectors](detectors.md)
