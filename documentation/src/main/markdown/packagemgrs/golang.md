# GoLang support

## Related properties

[Detector properties](../properties/detectors/go.md)

## Overview

[solution_name] has four detectors for GoLang:

* Go Mod Cli (GO_MOD) detector (recommended)
* Go Lock (GO_DEP) detector
* Go Gradle (GO_GRADLE) detector
* Go Vendor (GO_VENDOR) detector
* Go Vndr (GO_VNDR) detector

## Go Mod Cli (GO_MOD) detector

* Discovers dependencies of go language (GoLang) projects.
* Attempts to run on your project if a go.mod file is found in your source directory.
* Requires the *go* executable to be on the PATH or the executable path to be set with [detect.go.path](../properties/detectors/go.md#go-executable).
* Runs *go list -m* and *go mod graph*, and parses the output of both to discover dependencies.
* Runs *go mod why* to remove unused Go modules such as dependencies required by the build system or tests.

### Excluding Test and Build System dependencies

[solution_name] can run additional Go commands to filter out *test* and *build system* dependencies from the BOM.

Use [detect.go.mod.dependency.types.excluded=VENDORED](../properties/detectors/go.md#go-mod-dependency-types-excluded) to exclude the most dependencies. This will instruct [solution_name] to execute `go mod why -vendor` to generate a list
of modules to exclude.

Use the VENDORED option because running `go mod why` without the `-vendor` flag results in *test* and *build system* dependencies being included in the BOM from Go modules declaring a version prior to `Go 1.16`. See
the [go mod why documentation](https://go.dev/ref/mod#go-mod-why) for additional details.

#### Note on current exclusion behavior:

Now, [solution_name] runs *go mod why* by default to remove unused components from the BOM. This may result in a low number of detected dependencies. This behavior can be controlled with
the [detect.go.mod.dependency.types.excluded](../properties/detectors/go.md#go-mod-dependency-types-excluded)
property.

If the [detect.go.mod.dependency.types.excluded](../properties/detectors/go.md#go-mod-dependency-types-excluded) property is not provided, the behavior is driven by the value of this deprecated
property [detect.go.mod.dependency.types](../properties/detectors/go.md#go-mod-dependency-types).

In version 8.0.0, [solution_name] will not exclude any dependencies from the BOM by default and  [detect.go.mod.dependency.types](../properties/detectors/go.md#go-mod-dependency-types) will be removed.

## Go Lock (GO_DEP) detector

* Discovers dependencies of GoLang projects.
* Attempts to run on your project if a Gopkg.lock file is found in your source directory.
* Does not rely on external executables; for example, go, dep, and others.
* Parses Gopkg.lock for dependencies.

## Go Gradle (GO_GRADLE) detector

* Discovers dependencies of go language (GoLang) projects.
* Attempts to run on your project if a gogradle.lock file is found in your source directory.
* Does not rely on external executables; for example, go, dep, and others.
* Parses gogradle.lock for dependencies.

## Go Vendor (GO_VENDOR) detector

* Discovers dependencies of go language (GoLang) projects.
* Attempts to run on your project if the file vendor/vendor.json is found in your source directory.
* Does not rely on external executables; for example, go, dep, and others.
* Parses vendor/vendor.json for dependencies.

## Go Vndr (GO_VNDR) detector

* Discovers dependencies of go language (GoLang) projects.
* Attempts to run on your project if the file vendor.conf is found in your source directory.
* Does not rely on external executables; for example, go, dep, and others.
* Parses vendor.conf for dependencies.

