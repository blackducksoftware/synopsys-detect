# Carthage support

## Overview

[company_name] [solution_name] runs the Carthage detector if it finds either of the following files in your project:

* Cartfile
* Cartfile.resolved

The Carthage detector parses the Cartfile.resolved file for information on your project's dependencies. If the detector discovers a Cartfile file but not a Cartfile.resolved file, it will prompt the user to generate a Cartfile.resolved file by running `carthage update` and then run [company_name] [solution_name] again.

## Supported Dependency Origins

[company_name] [solution_name] only reports dependencies declared in a Cartfile.resolved file that have a 'github' [origin](https://github.com/Carthage/Carthage/blob/master/Documentation/Artifacts.md#origin).  This limited support is a result of the de-centralized nature of the Carthage ecosystem.  Many commonly-used frameworks used in Carthage projects are not open-source, and thus not tracked by [blackduck_product_name].

* Note: Even for dependencies from Github that are declared with the 'github' origin, it is possible that some may not be matched by [blackduck_product_name], as [blackduck_product_name] does not track all repositories hosted on GitHub.
