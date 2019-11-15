# Software Composition Analysis (SCA) with ${blackduck_product_name}

## Overview

When ${blackduck_product_name} connection details are provided, ${solution_name} will execute
the following by default:

* The [detector tool](/components/detectors), which runs the appropriate package manager-specific detector (the Maven detector
for Maven projects, the Gradle detector for Gradle projects, etc.).
* The ${blackduck_product_name} [signature scan tool](/properties/Configuration/signature%20scanner/), which runs the signature scanner on the
project directory.

${solution_name} can be configured to perform additional tasks, including the following:

* Enable any of the supported snippet matching modes in the [signature scanner](/properties/Configuration/signature%20scanner).
* Run the ${blackduck_product_name} [binary scanner](/properties/Configuration/signature%20scanner) on a given binary files.
* Run the ${dockerinspector_name} on a given [Docker image](/advanced/language-and-package-managers/docker-images).
* Generate a [report](http://localhost:8000/properties/Configuration/report/).

See [Black Duck Server properties](/properties/Configuration/blackduck%20server)
and [Signature Scanner properties](/properties/Configuration/signature%20scanner/) for details.

## Offline mode

If you have no ${blackduck_product_name} instance, or if your network is down, you can still run ${solution_name} in offline mode.
In offline mode, ${solution_name} creates the BDIO content and the dry run signature scan output files without attempting to upload them to Black Duck.
You can run ${solution_name} in offline mode using the [offline mode property](/properties/Configuration/blackduck%20server/#offline-mode).