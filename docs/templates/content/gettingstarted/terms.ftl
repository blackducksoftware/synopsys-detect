# Key Concepts and Terms

This page describes the key concepts and terms that are used with ${solution_name}.
Understanding the basic terms and ${solution_name} components helps you to explore how to use it to scan and analyze your code more efficiently.

## Software Composition Analysis (SCA) 

Open source software detection to provide users visibility into their open source inventory. 

## ${solution_name} run

Typically, it consists of the ${solution_name} detector using the project's package manager to derive the hierarchy of dependencies in a software project,
running the ${blackduck_signature_scanner_name}, and uploading the results to ${blackduck_product_name} for analysis.

## ${solution_name} script

The primary function of the ${solution_name} scripts is to download and execute the ${solution_name} JAR file, which enables the scan.

You download and run the latest version of ${solution_name} using the following commands, and add properties to refine the instruction.

Windows:
````
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect7.ps1?$(Get-Random) | iex; detect"
````

Linux/MacOs:
````
bash <(curl -s https://detect.synopsys.com/detect7.sh)
````

## ${solution_name} JAR

By using a specific ${solution_name} JAR, you have direct control over the ${solution_name} version that you use, rather than using the script, which automatically runs the latest version.

## ${solution_name} tools

${solution_name} tools are run to enable the scanning of your code.

The default tools that are run are:

* Detector (--detect.tools=DETECTOR).
The detector tool runs the appropriate detectors that are used to find and extract dependencies by using package manager inspection.
* ${blackduck_signature_scanner_name} (--detect.tools=SIGNATURE_SCAN).
The ${blackduck_signature_scanner_name} tool runs by default when ${blackduck_product_name} connection details are provided. A file/folder (Signature) scan is performed on the built project to examine all project files for open-source software.

Other ${solution_name} tools such as Docker Inspector or ${blackduck_binary_scan_capability} are not run by default in most scenarios but you can add them by using properties on the command line.

## Detectors

${solution_name} uses detectors to find and extract dependencies from all supported package managers. For example, the Maven detector, which is run by default, executes an mvn dependency:tree command against a Maven project and derives dependency information, which can be sent to ${blackduck_product_name}

By default, all detectors are eligible to run. The set of detectors that actually run depends on the files that exist in your project directory. 

## Properties

A property to which you assign a value is like a flag or a parameter on the command line or in a script that provides instructions for the ${solution_name} scan task.

When setting a property value, the property name is prefixed with two hyphens (--). 

````
bash <(curl -s -L https://detect.synopsys.com/detect7.sh) <--property=value>
````

Example using properties to specify project name and ${blackduck_product_name} URL:

````
bash <(curl -s -L https://detect.synopsys.com/detect7.sh) --detect.project.name=MyProject --blackduck.url=https://blackduck.yourdomain.com
````

## Inspectors

Inspectors are used by detectors when the package manager requires an integration or embedded plugin to work. For example, Gradle uses an inspector as a plugin that executes a custom task. Most detectors do not require an inspector.

## Scans and projects

${solution_name} scans are mapped to one project. A project version can have more than one scan mapped to it, which enables the mapping of multiple separate folders and their scan results of their into one aggregated project version.

## BDIO 

${solution_name} produces dependency information for ${blackduck_product_name} in ${blackduck_product_name} Input Output (BDIO) format files. 

## Vulnerability Impact Analysis

When the *detect.impact.analysis.enabled* property in ${solution_name} to set to true, ${solution_name} creates a call graph (a list of calls made by your code)
to understand the public methods your code is using in your application.
The call graph shows the fully qualified public method names as well as the line number where the function was called.
The data is packaged into a single file and ${solution_name} sends the file over HTTPS to the ${blackduck_product_name} server, enabling vulnerability impact analysis in ${blackduck_product_name}
