# About ${solution_name}

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

# ${solution_name} components

Depending on the property values you pass to ${solution_name}, ${solution_name} will run one or more "tools" (Detectors, signature scanner, etc.).
See [Tools](components/tools.md).

By default, the ${solution_name} Detector tool checks your project directory for hints about what package manager it uses to manage dependencies. If it detects
one of the supported package manager, it runs the corresponding Detector. See [Detectors](components/detectors.md).

Detect can upload results to either ${blackduck_product_name}, or ${polaris_product_name}, or both.

Detect optionally performs post actions:

* Generates a risk report
* Checks for policy violations

