# [solution_name] tools

[solution_name] tools are run to enable the scanning of your code.

The default tools that are run are:

* Detector (--detect.tools=DETECTOR).
The detector tool runs the appropriate detectors that are used to find and extract dependencies by using package manager inspection.
* [blackduck_signature_scanner_name] (--detect.tools=SIGNATURE_SCAN).
The [blackduck_signature_scanner_name] tool runs by default when [blackduck_product_name] connection details are provided. A file/folder (Signature) scan is performed on the built project to examine all project files for open-source software.

Other [solution_name] tools such as Docker Inspector or [blackduck_binary_scan_capability] are not run by default in most scenarios but you can add them by using properties on the command line.
