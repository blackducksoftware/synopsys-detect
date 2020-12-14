# Requirements

## General requirements

Requirements for ${solution_name}

* Normally, access to the internet is required to download and run ${solution_name} and components from GitHub and other locations. For running without internet access, refer to [Air gap and offline modes](../advanced/air-gap/).
* Minimum 8GB RAM.
* Java: OpenJDK versions 8 or 11.
* curl versions 7.34.0 or later.
* Bash.
* If using ${powershell_script_name}: PowerShell versions 4.0 or higher.
* The tools required to build your project source code.

## ${blackduck_product_name} requirements

For connecting to ${blackduck_product_name}:

* Licensed installation of the current version of ${blackduck_product_name} with access credentials.  Visit [this page](${blackduck_release_page}) to determine the current version of Black Duck.
* The ${blackduck_product_name} notifications module must be enabled.
* A ${blackduck_product_name} user with the [required roles](../advanced/BD-user-role-requirements/).
* On Alpine Linux you will also need to override the Java installation used by the ${blackduck_signature_scanner_name} as described [here](../advanced/troubleshooting/solutions/#solution_8).

## ${polaris_product_name} requirements

A licensed installation of Polaris with access credentials.

## Project type-specific requirements

In general, the detectors require:

* All dependencies must be resolvable. This generally means that each dependency has been installed using the package manager's cache, virtual environment, and others.
* The package manager / build tool must be installed and in the path.

Refer to *Language and package managers* for information on specific detectors.

## Risk report requirements

The risk report requires that the following fonts are installed:

* Helvetica
* Helvetica bold

