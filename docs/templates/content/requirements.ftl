# Requirements

## General requirements

Requirements for Synopsys Detect are:

* Normally, access to the internet is required to download and run Synopsys Detect and components from GitHub and other locations. For running without internet access, see [Air gap and offline modes](air-gap.md).
* Minimum 8GB RAM.
* OpenJDK 8 or OpenJDK 11.
* curl version 7.34.0 or later.
* Bash.
* The source code for your project must be buildable.

## ${blackduck_product_name} requirements

For connecting to ${blackduck_product_name}:

* Licensed installation of the current version of ${blackduck_product_name} with access credentials.  Visit [this page](${blackduck_release_page}) to determine the current version of Black Duck.
* The ${blackduck_product_name} notifications module must be enabled.
* A ${blackduck_product_name} user with project creator and global code scanner roles.

## ${polaris_product_name} requirements

A licensed installation of Polaris with access credentials.

## Project type-specific requirements

In general, the detectors require:

* All dependencies must be resolvable. This generally means that each dependency has been installed using the package manager's cache, virtual environment, etc.
* The package manager / build tool must be installed and in the path.

See [Language and package manager support details](advanced/language-and-package-managers/overview.md) for information on specific detectors.

## Risk report requirements

The risk report requires that the following fonts are installed:

* Helvetica
* Helvetica bold

