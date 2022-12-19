# Introduction to Detect

[solution_name] is [blackduck_product_name]'s intelligent scan client that scans code bases in your projects and folders to perform compositional analysis. [solution_name] sends scan results to [blackduck_product_name], which generates risk analysis when identifying open-source components, licenses, and security vulnerabilities.

[solution_name] can be used in both connected and air gap modes.  

## [solution_name] has the following characteristics.     

* [solution_name] integrates with development tools used throughout the SDLC (software development life cycle) and automatically detects resources to optimize its scan methodology.

* [solution_name] provides scanning capabilities to [blackduck_product_name] to help identify open-source components, licenses, and security vulnerabilities. This is achieved through a variety of detection methods such as package manager inspection, file system based signature scanning of source directories and files, Docker image inspection, and binary analysis.

* [solution_name] provides the source of information for [blackduck_product_name] to analyze open-source components and find vulnerabilities in open-source components and containers. Using this type of analysis, you can minimize security, compliance, and code quality risks; you can monitor for new vulnerabilities throughout your development cycle, and you can set and enforce open-source use and security policies.

* Runs on Windows, Linux, and macOS. It is available through GitHub, under the permissive Apache License, Version 2.0 and does not require pre-installation or configuration.

* Supports scanning Docker images by identifying open-source libraries and code within the images, using both signature scanning and the package manager analysis techniques.  

## [solution_name] functionality consolidation.  

[solution_name] consolidates the functionality of [blackduck_product_name], package managers, and continuous integration plugin tools to perform the following tasks:  

* Discover open-source components in your code.

* Map components to known security vulnerabilities.

* Identify license compliance and component quality risks.

* Set and enforce open-source use and security policies.

* Integrate open-source management into your DevOps environment.

* Monitor and alert users when new security threats are reported.

* Calculate security vulnerability risk in your code.

* Produce reports of the open-source analysis findings.  

## How [solution_name] functions.   

When looking at vulnerabilities in open source and third-party software, [solution_name] performs the following basic steps:

* Uses the project's package manager to derive the hierarchy of dependencies known to that package manager. For example, on a Maven project, [solution_name] executes an mvn dependency:tree command and derives dependency information from the output.

* Runs the [blackduck_product_name] signature scanner on the project. This might identify additional dependencies not known to the package manager (for example, a .jar file copied into the project directory).

* Uploads both sets of results (dependency details) to [blackduck_product_name] creating the project/version if it does not already exist. [blackduck_product_name] uses the uploaded dependency information to build the Bill Of Materials (BOM) for the project/version.

* You can view the output and analysis results in [blackduck_product_name].  

