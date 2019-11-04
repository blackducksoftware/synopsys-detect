# About

${solution_name} consolidates the functionality of ${blackduck_product_name}(TM) and Coverity™ on Polaris™ to support
Software Composition Analysis (SCA: open source software detection) and Static Application Security Testing (SAST: code analysis).
${solution_name} makes it easier to set up and scan code bases for a variety of languages and package managers.
${solution_name} leverages multi-factor discovery techniques to scan software projects and directories.
${solution_name} runs on Windows, Linux, and MacOS. It is available through GitHub, under a permissive
Apache license and does not require pre-installation or configuration.
For more information, refer to [Requirements](requirements.md).

${solution_name} can be used either for ${blackduck_product_name} SCA (open source software detection), or for Coverity on Polaris SAST (static code) analysis, or both simultaneously.
Although ${solution_name} can support large projects, care should be taken to ensure that scans are performed to optimize platform performance and produce manageable and meaningful results.
For applications containing multiple sub-projects, it may be advantageous to scan sub-projects separately and combine results as a project of projects, or other techniques.

## ${solution_name} processing

${solution_name} processing is divided into three phases:

* Initialization
* Run
* Cleanup

### Initialization phase

During the initialization phase, ${solution_name} performs verification checks on the user-provided configration, checks to see if it
can connect to any external systems needed for the run, and creates any directories that it needs.

### Run phase

During the run phase, ${solution_name} processes and ordered list of [tools](/components/tools), invoking any/all that apply.
Tool applicability depends on how ${solution_name} is configured (what property values you set).

One of those tools is the Detector tool, which runs by default and will invoke any/all [Detectors](/components/detectors) that apply.
Detector applicability depends on what ${solution_name} finds in your project. For example, if ${solution_name}
finds a pom.xml file, it will run the Maven detector.

At the end of the run phase, ${solution_name} will upload results to the appropriate external
system(s) (${blackduck_product_name} and/or ${blackduck_product_name}), and optionally perform post actions, such as generating
a risk report or checking for policy violations.

### Cleanup phase

During the cleanup phase, ${solution_name} removes temporary files and directories before exiting.

## Modifying ${solution_name} processing

For information on modifying ${solution_name} processing, see [Including/excluding tools/detectors](running/tbd).
