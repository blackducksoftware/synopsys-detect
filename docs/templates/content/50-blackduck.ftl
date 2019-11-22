# Software Composition Analysis (SCA) with ${blackduck_product_name}

## Overview

When ${blackduck_product_name} connection details are provided, ${solution_name} will execute
the following by default:

* The [detector tool](components/detectors.md), which runs the appropriate package manager-specific detector (the Maven detector
for Maven projects, the Gradle detector for Gradle projects, etc.).
* The [${blackduck_signature_scanner_name}](properties/Configuration/signature scanner.md), which does a ${blackduck_signature_scan_act} on the
project directory.

${solution_name} can be configured to perform additional tasks, including the following:

* Enable any of the supported snippet matching modes in the [${blackduck_signature_scanner_name}](properties/Configuration/signature scanner.md).
* Run the [${blackduck_binary_scanner_name}](properties/Configuration/signature scanner.md) on a given binary files.
* Run the ${dockerinspector_name} on a given [Docker image](advanced/language-and-package-managers/docker-images).
* Generate a [report](properties/Configuration/report.md).
* Fail on [policy violation](properties/Configuration/project.md#fail-on-policy-violation-severities.md).

See [${blackduck_product_name} Server properties](properties/Configuration/blackduck server.md)
and [${blackduck_signature_scanner_name} properties](properties/Configuration/signature scanner.md) for details.

## Offline mode

If you have no ${blackduck_product_name} instance, or if your network is down, you can still run ${solution_name} in offline mode.
In offline mode, ${solution_name} creates the BDIO content and the dry run ${blackduck_signature_scan_act} output files without attempting to upload them to ${blackduck_product_name}.
You can run ${solution_name} in offline mode using the [offline mode property](properties/Configuration/blackduck server.md#offline-mode).
