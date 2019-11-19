# Static application security testing (SAST) with ${polaris_product_name}

## Overview

When ${polaris_product_name} connection details are provided, ${solution_name} will execute
the following by default:

* The detector tool, which runs the appropriate package manager-specific detector (the Maven detector
for Maven projects, the Gradle detector for Gradle projects, etc.).
* The ${polaris_product_name} signature scan tool, which runs the signature scanner on the
project directory.

See [Properties](/properties/Configuration/polaris) for details.