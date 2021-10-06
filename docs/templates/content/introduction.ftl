# Introduction

${solution_name} is ${blackduck_product_name}'s intelligent scan client that scans code bases in your projects and folders to perform compositional analysis. ${solution_name} sends scan results to ${blackduck_product_name}, which generates risk analysis when identifying open source components, licenses, and security vulnerabilities.

${solution_name} also has the following characteristics:

* ${solution_name} integrates with development tools used throughout the SDLC (software development life cycle) and automatically detects resources to optimize its scan methodology.
* ${solution_name} provides scanning capabilities to ${blackduck_product_name} to help identify open source components, licenses, and security vulnerabilities. This is achieved through a variety of detection methods such as package manager inspection, file-system based signature scanning of source directories and files, Docker image inspection, and Binary analysis. 
* ${solution_name} provides the source of information for ${blackduck_product_name} to analyze open source components and find vulnerabilities in open source components and containers. Using this type of analysis, you can minimize security, compliance, and code quality risks; you can monitor for new vulnerabilities throughout your development cycle, and you can set and enforce open source use and security policies.
* Runs on Windows, Linux, and macOS. It is available through GitHub, under a permissive Apache license and does not require pre-installation or configuration.
* Supports scanning Docker images by identifying open source within the images, using both signature scanning and the package manager analysis techniques.

${solution_name} consolidates the functionality of the ${blackduck_product_name}, package managers, and continuous integration plugin tools into a single tool, which does the following:

* Discovers open source components in your code
* Maps components to known vulnerabilities
* Identifies license compliance and component quality risks
* Sets and enforces open source policies
* Integrates open source management into your DevOps environment
* Monitors and alerts when new threats are reported
* Calculates risk in your code.
* Produces reports of its findings

## ${solution_name} at work

By default, ${solution_name} examines your source directory to be scanned and discovers the package managers in your code, and uses the project package managers to derive the hierarchy of dependencies known to those package managers.

* ${solution_name} extracts package manager dependencies in your project by using selected ${solution_name} detectors to extract the dependencies.
* The ${blackduck_product_name} signature scanner runs and extracts more dependencies that might not be known to a package manager if there is a connection to ${blackduck_product_name}.
* All dependencies are uploaded to ${blackduck_product_name} for analysis; a project is created, and a Bill of Materials (BOM) is generated.
* You can view the output and analysis in ${blackduck_product_name}.
