# Running with [blackduck_product_name]

[solution_name] can be used with multiple Synopsys platforms and [blackduck_product_name] to perform Software Composition Analysis (SCA).

## Overview

Running with [blackduck_product_name] and connection details are provided, [solution_name] executes
the following by default:

* The [detector tool](../../components/detectors.md), which runs the appropriate package manager-specific detector; the Maven detector
for Maven projects, the Gradle detector for Gradle projects, and so forth.
* The [Black Duck Signature Scanner](../../properties/configuration/signature-scanner.md), which performs a [blackduck_signature_scan_act] on the
project directory.
* Run [Black Duck - Binary Analysis](../../properties/configuration/binary-scanner.md) on given binary files.

[solution_name] can be configured to perform additional tasks, including the following:

* Enable any of the supported snippet matching modes in the [Black Duck Signature Scanner](../../properties/configuration/signature-scanner.md).
* Enable the [Vulnerability Impact Analysis Tool](../../properties/configuration/impact-analysis.md#vulnerability-impact-analysis-enabled) on any Java project.
* Run the [dockerinspector_name] on a given [Docker image](../../packagemgrs/docker/intro.md).
* Generate a [report](../../properties/configuration/report.md).
* Fail on [policy violation](../../properties/configuration/project.md#fail-on-policy-violation-severities-advanced).
* Run [IaC Scan](../iacscan.md) on provided targets. Note: Iac Scan capabilities require [blackduck_product_name] 2022.7.0 or later.

Refer to [Black Duck Server properties](../../properties/configuration/blackduck-server.md), [Black Duck Signature Scanner properties](../../properties/configuration/signature-scanner.md), and [IaC Scan](../iacscan.md) for details.

## Offline mode

If you do not have a [blackduck_product_name] instance, or if your network is down, you can still run [solution_name] in offline mode.
In offline mode, [solution_name] writes output files (.bdio files and, when Vulnerability Impact Analysis runs, .bdmu files) to subdirectories
within the run directory without attempting to upload them to [blackduck_product_name]. You can find the value of the run directory in the [solution_name] log.
You can run [solution_name] in offline mode using the [offline mode property](../../properties/configuration/blackduck-server.md#offline-mode).

## BDIO format

[solution_name] produces dependency information for [blackduck_product_name] in Black Duck Input Output (BDIO) format files.
[solution_name] now only supports generating BDIO version 2 documents.


