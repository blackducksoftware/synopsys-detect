# Tools

[solution_name] tools are components that enable the scanning of your source code. [solution_name] uses several underlying tools to perform scanning including:   
* Detector (for inspecting package manager dependencies)   
* Signature Scanner (for inspecting the file system)   
* Docker Inspector (for inspecting Docker container content)   
* Bazel detector ( to discover dependencies in Bazel projects)   
* Binary Analysis (used to determine components within binary files)   
* Vulnerability Impact Analysis Tool (generates a Vulnerability Impact Analysis Report)   
* IaC Scanner (supports infrastructure as code scanning)   

Optional properties can be specified to explicitly enable or disable these underlying tools, but by default [solution_name] will run both the Detector and Signature Scanner on the code being analyzed.

The default tools that are run by [solution_name]:

* Detector (--detect.tools=DETECTOR).
The detector tool runs the appropriate detectors that are used to find and extract dependencies by using package manager inspection.
* [blackduck_signature_scanner_name] (--detect.tools=SIGNATURE_SCAN).
The [blackduck_signature_scanner_name] tool runs by default when [blackduck_product_name] connection details are provided. A file/folder (Signature) scan is performed on the built project to examine all project files for open-source software.

Other [solution_name] tools such as Docker Inspector or [blackduck_binary_scan_capability] are not run by default in most scenarios but can be added to a run by specifying their properties on the command line.
