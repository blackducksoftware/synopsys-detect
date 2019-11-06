# Software Composition Analysis (SCA) with ${blackduck_product_name}

## Overview

When ${blackduck_product_name} connection details are provided, the ${solution_name} will execute
the following by default:

* The detector tool, which runs the appropriate package manager-specific detector (the Maven detector
for Maven projects, the Gradle detector for Gradle projects, etc.).
* The ${blackduck_product_name} signature scan tool, which runs the signature scanner on the
project directory.

${solution_name} can be configured to perform additional tasks, including the following:

* Enable any of the supported snippet matching modes in the signature scanner.
* Run the ${blackduck_product_name} binary scanner on a given binary files.
* Run the ${dockerinspector_name} on a given Docker image.

See [Properties](/properties/all-properties) for details.
