# Conan support

${solution_name} has two detectors for Conan:

* Conan Lockfile detector
* Conan CLI detector

## Conan detector requirements

${solution_name} will run a Conan Detector if either of the following is true:

* ${solution_name} finds or is provided (via the *detect.conan.lockfile.path* property) a Conan lockfile. (If no lockfile is provided, ${solution_name} looks for a file named conan.lock.) In this case, the Conan Lockfile detector runs and discovers dependency details using the contents of the Conan lockfile.
* ${solution_name} finds a file named *conanfile.txt* or *conanfile.py*. In this case, the Conan CLI detector runs and discovers dependency details by running the *conan info* command on the Conan project and parsing the output.

In order for ${solution_name} to generate dependency details that will reliably match components
in the ${blackduck_kb}, the Conan revisions feature must be enabled on the Conan project.
The Conan command *conan config get general.revisions_enabled* must produce a value of "True"
*and* this value must not be overridden by the environment variable CONAN_REVISIONS_ENABLED.

## Conan detector usage

When using the Conan CLI detector, be sure to use the *detect.conan.arguments* property to provide any additional arguments (profile settings, etc.) that the *conan info* command needs to produce accurate results.

## Package revision matching (future enhancement)

By default (property *detect.conan.attempt.package.revision.match* is set to false), the Conan detectors use the following dependency details to match components in the ${blackduck_kb}:

* name
* version
* user (defaults to "_")
* channel (defaults to "_")
* recipe revision

This is the default mode, and currently (as of February 2021) produces the best results.

Enhancements to the ${blackduck_kb} are planned that, for Conan projects with lockfiles
and the Conan revisions feature enabled,
will enable more accurate matching that also considers package ID and package revision.
(Package revision is provided by Conan lockfiles when the Conan revisions feature is enabled,
but it is never provided by the *conan info* command, so this only affects the Conan Lockfile detector.)
When this ${blackduck_kb} enhancement becomes available,
you will be able to improve match accuracy for projects with lockfiles by setting
property *detect.conan.attempt.package.revision.match* to true.

## Conan Detector Precedence

If a Conan lockfile (conan.lock) is found or provided, the Conan Lockfile detector will run.

If no Conan lockfile is found or provided, but a conanfile.txt or conanfile.py is found, the
Conan CLI detector will run.
