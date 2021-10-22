# Yocto (BitBake)

The BitBake detector discovers dependencies of custom Linux distribution projects built using Yocto versions 2.0 through 3.0.

The BitBake detector attempts to run on your project if all of the following are true:

1. A build environment init script (typically *oe-init-build-env*) is found in the source directory.
2. You provide at least one package name (target image name) using the BitBake package names property. ${solution_name} will not run the Bitbake detector if this property is not set.

The BitBake detector attempts to source the *oe-init-build-env* file on your behalf.

The BitBake detector also requires a bash executable, which it looks for on $PATH. You can override this by setting the bash path property.

The BitBake detector does the following for each provided recipe name: The detector sources the build environment setup script and executes
`bitbake -g {package-name}` to generate a dependency file _recipe-depends.dot_ that it parses for dependency information.
It also uses `bitbake-layers show-recipes` to gather layer information.

## Troubleshooting Tips

### Missing components for projects using the Yocto Package Revision Service

Symptom: Components are missing from the ${blackduck_product_name} BOM.

Problem: The Yocto Package Revision Service can increment a package revision to a value not present in the ${blackduck_product_name} Knowledge Base, causing
a package to fail to match.
