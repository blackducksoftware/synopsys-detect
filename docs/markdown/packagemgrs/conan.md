# Conan support

## Related properties

[Detector properties](../properties/detectors/conan.md)

## Overview

[solution_name] has two detectors for Conan:

* Conan Lockfile detector
* Conan CLI detector

## Conan detector requirements

[solution_name] will run a Conan Detector if either of the following is true:

* [solution_name] finds or is provided (via the *detect.conan.lockfile.path* property) a Conan lockfile. (If no lockfile is provided, [solution_name] looks for a file named conan.lock.) In this case, the Conan Lockfile detector runs and discovers dependency details using the contents of the Conan lockfile. The Conan Lockfile detector should always be preferred due to the additional information (package revisions) that may be provided by lockfiles.
* [solution_name] finds a file named *conanfile.txt* or *conanfile.py*. In this case, the Conan CLI detector runs and discovers dependency details by running the *conan info* command on the Conan project and parsing the output.

In order for [solution_name] to generate dependency details that will reliably match components
in the [blackduck_kb], the Conan revisions feature must be enabled on the Conan project.
The Conan command *conan config get general.revisions_enabled* must produce a value of "True"
*and* this value must not be overridden by the environment variable CONAN_REVISIONS_ENABLED.

## Conan detector usage

When using the Conan CLI detector, be sure to use the *detect.conan.arguments* property to provide any additional arguments (profile settings, etc.) that the *conan info* command needs to produce accurate results.

## KB external ID generation

By default (property *detect.conan.attempt.package.revision.match* is set to false), the Conan detectors use the following dependency details to match components in the [blackduck_kb]:

* name
* version
* user (defaults to "_")
* channel (defaults to "_")
* recipe_revision

For example, here is a conan.lock file entry for a component (zlib):
```
   "2": {
    "ref": "zlib/1.2.11#1a67b713610ae745694aa4df1725451d",
    "options": "fPIC=True\nminizip=False\nshared=False",
    "package_id": "d50a0d523d98c15bb147b18fa7d203887c38be8b",
    "prev": "da65bb160c07195dba18afb91259050d",
    "context": "host"
   },
```

If you are using the Conan CLI detector instead of the Conan Lockfile detector, this data is found in the output of the `conan info` command
instead of the conan.lock file..

The format of the Conan "ref" field is: `<name>/<version>@<user>/<channel>#<recipe_revision>`

In the zlib example:

* name=zlib
* version=1.2.11
* user=_ (by default)
* channel=_ (by default)
* recipe_revision=1a67b713610ae745694aa4df1725451d

[solution_name] constructs a KB external ID for namespace "conan" using these fields as follows:
```
<name>/<version>@<user>/<channel>#<recipe_revision>
```

## Package revision matching

For Conan projects with lockfiles and the Conan revisions feature enabled
[solution_name] has an alternative mode, package revision matching, that includes
the package ID and package revision in the KB external IDs that it constructs (in addition to the fields described above).
(Package revision is provided by Conan lockfiles when the Conan revisions feature is enabled,
but it is never provided by the *conan info* command, so this only affects the Conan Lockfile detector.)
To enable package revision matching, set
property *detect.conan.attempt.package.revision.match* to true.


In this scenario, [solution_name] constructs a KB external ID for namespace "conan" as follows:
```
<name>/<version>@<user>/<channel>#<recipe_revision>:<package_id>#<package_revision>
```

For the zlib example above the two additional fields used in the KB external ID would be:

* package_id=d50a0d523d98c15bb147b18fa7d203887c38be8b
* package_revision=da65bb160c07195dba18afb91259050d

## Conan Detector Precedence

If a Conan lockfile (conan.lock) is found or provided, the Conan Lockfile detector will run.

If no Conan lockfile is found or provided, but a conanfile.txt or conanfile.py is found, the
Conan CLI detector will run.

## Customized user/channel values

Some Conan users use modified versions of Open Source packages with custom user/channel values. These modified components will not match components in the KB.
The KB requires a match on user and channel.
