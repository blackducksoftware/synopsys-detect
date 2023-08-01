# Running with [blackduck_product_name]

[solution_name] can be used with multiple Synopsys platforms and [blackduck_product_name] to perform Software Composition Analysis (SCA).

## Overview

When running with [blackduck_product_name] and connection details are provided, [solution_name] executes
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
<note type="note">Offline mode is not the same as Air Gap mode. Air Gap mode requires the airgap.jar available to execute as it contains local copies of scanning libraries to support full offline execution.</note>
In offline mode, [solution_name] writes output files (.bdio files and, when Vulnerability Impact Analysis runs, .bdmu files) to subdirectories
within the run directory without attempting to upload them to [blackduck_product_name]. You can find the value of the run directory in the [solution_name] log.
You can run [solution_name] in offline mode using the [offline mode property](../../properties/configuration/blackduck-server.md#offline-mode).

### Running in offline mode

Download the latest [solution_name] version:
 [See download locations](https://sig-product-docs.synopsys.com/bundle/integrations-detect/page/downloadingandinstalling/downloadlocations.html)   
 
*Choose one of the following to download.*

**Version 8.7 and later**   
`synopsys-detect-X.X.X-air-gap-no-docker.zip` includes scanning for Gradle and Nuget no Docker
`synopsys-detect-X.X.X-air-gap.zip` includes scanning for Gradle, Nuget, or Docker

Download the Signature Scanner from your [blackduck_product_name] server:
https://{blackduckserver}/download/scan.cli.zip
https://{blackduckserver}/download/scan.cli-windows.zip
https://{blackduckserver}/download/scan.cli-macosx.zip

Scanning [solution_name] Properties to specify:
* --blackduck.offline.mode=true
* --detect.scan.output.path= output of the Signature Scanner
* --detect.output.path= output directory to store files that [solution_name] downloads or creates
* --detect.blackduck.signature.scanner.local.path= location to the signature scanner scan.cli-202x.xx.x
If using air-gap zip archive files, [see air gap mode](https://sig-product-docs.synopsys.com/bundle/integrations-detect/page/downloadingandrunning/airgap.html)

[solution_name] Scan Command example:
```
java -jar  synopsys-detect-x.x.x.jar --blackduck.url= --blackduck.api.token= --detect.project.name= --detect.project.version.name= --blackduck.offline.mode=true --detect.scan.output.path= --detect.output.path= --detect.blackduck.signature.scanner.local.path=
```
      
Upload Scan results via the [blackduck_product_name] UI:

The scan files to upload to [blackduck_product_name] are found in the output [blackduck_product_name] directory. There will be a scan file for the Signature Scanner and Dependency Scanner. Look at console output to check if both scanners ran. It is possible one scanner ran, but the other did not.

The following are the locations of the scan files if the following [solution_name] properties were used:

* scan.output.path (ends in .json): {the path provided}\BlackDuckScanOutput\{date and time of scan}\data
* output.path (ends in *.bdio): {the path provided}\runs\{date and time of scan}\bdio

[blackduck_product_name] UI upload

- Go to **Scans** in the left navigation bar
- Click **+Upload** Scans
- Add scan files

## BDIO format

[solution_name] produces dependency information for [blackduck_product_name], and other Synopsys products and platforms, in [blackduck_product_name] Input Output (BDIO) format files.
[solution_name] supports generating BDIO version 2 documents.
