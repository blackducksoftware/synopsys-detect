# Running with [blackduck_product_name]

[solution_name] can be used with [blackduck_product_name] to perform Software Composition Analysis (SCA).

## Overview

When [blackduck_product_name] connection details are provided, [solution_name] executes
the following by default:

* The [detector tool](../components/detectors.md), which runs the appropriate package manager-specific detector; the Maven detector
for Maven projects, the Gradle detector for Gradle projects, and so forth.
* The [Black Duck Signature Scanner](../properties/configuration/signature-scanner.md), which performs a [blackduck_signature_scan_act] on the
project directory.

[solution_name] can be configured to perform additional tasks, including the following:

* Enable any of the supported snippet matching modes in the [Black Duck Signature Scanner](../properties/configuration/signature-scanner.md).
* Enable the [Vulnerability Impact Analysis Tool](../properties/configuration/impact-analysis.md#vulnerability-impact-analysis-enabled) on any Java project.
* Run [Black Duck - Binary Analysis](../properties/configuration/binary-scanner.md) on a given binary files.
* Run the [dockerinspector_name] on a given [Docker image](../packagemgrs/docker/intro.md).
* Generate a [report](../properties/configuration/report.md).
* Fail on [policy violation](../properties/configuration/project.md#fail-on-policy-violation-severities-advanced).

Refer to [Black Duck Server properties](../properties/configuration/blackduck-server.md)
and [Black Duck Signature Scanner properties](../properties/configuration/signature-scanner.md) for details.

## Offline mode

If you do not have a [blackduck_product_name] instance, or if your network is down, you can still run [solution_name] in offline mode.
In offline mode, [solution_name] creates the BDIO content and the dry run [blackduck_signature_scan_act] output files without attempting to upload them to [blackduck_product_name].
You can run [solution_name] in offline mode using the [offline mode property](../properties/configuration/blackduck-server.md#offline-mode).

## BDIO format

[solution_name] produces dependency information for [blackduck_product_name] in Black Duck Input Output (BDIO) format files.
[solution_name] can produce BDIO files in two formats: BDIO version 1, or BDIO version 2.
Versions of [blackduck_product_name] prior to 2018.12.4 accept only BDIO 1.
[blackduck_product_name] versions 2018.12.4 and higher accept either BDIO 1 or BDIO 2.
By default, [solution_name] produces BDIO 2 files.

Use the [BDIO2 enabled property](../properties/configuration/paths.md#bdio-2-enabled-deprecated) to select BDIO 1 format
(by disabling BDIO 2 format).

