# GoLang support

${solution_name} has four detectors for GoLang:

* [Go Lock (GO_DEP) detector](#godeplockdetector)
* [Go Gradle (GO_GRADLE) detector](#godepclidetector)
* [Go Mod Cli (GO_MOD) detector](#gomodclidetector)
* [Go Vendor (GO_VENDOR) detector](#govendordetector)
* [Go Vndr (GO_VNDR) detector](#govndrdetector)

<a name="godeplockdetector"></a>
## Go Lock (GO_DEP) detector

The Go Lock (GO_DEP) detector:

* discovers dependencies of GoLang projects.
* attempts to run on your project if a Gopkg.lock file is found in your source directory.
* does not rely on external executables; for example, go, dep, and others.
* parses Gopkg.lock for dependencies.

<a name="godepclidetector"></a>
## The Go Gradle (GO_GRADLE) detector

The Go Gradle (GO_GRADLE) detector:

* discovers dependencies of go language (GoLang) projects.
* attempts to run on your project if a gogradle.lock file is found in your source directory.
* does not rely on external executables; for example, go, dep, and others.
* parses gogradle.lock for dependencies.

<a name="gomodclidetector"></a>
## The Go Mod Cli (GO_MOD) detector

The Go Mod Cli (GO_MOD) detector:

* discovers dependencies of go language (GoLang) projects.
* attempts to run on your project if a go.mod file is found in your source directory.
* requires that the *go* executable is on the PATH or the executable path is set with [detect.go.path](../../../properties/detectors/go/#go-executable).
* runs *go list -m* and *go mod graph*, and parses the output of both to discover dependencies.
* runs *go mod why* to remove unused components in a build environment. Use [detect.go.mod.enable.verification=false](../../../properties/detectors/go/#go-mod-dependency-verification) to disable this step.

<a name="govendordetector"></a>
## The Go Vendor (GO_VENDOR) detector

The Go Vendor (GO_VENDOR) detector:

* discovers dependencies of go language (GoLang) projects.
* attempts to run on your project if the file vendor/vendor.json is found in your source directory.
* does not rely on external executables; for example, go, dep, and others.
* parses vendor/vendor.json for dependencies.

<a name="govndrdetector"></a>
## The Go Vndr (GO_VNDR) detector

The Go Vndr (GO_VNDR) detector:

* discovers dependencies of go language (GoLang) projects.
* attempts to run on your project if the file vendor.conf is found in your source directory.
* does not rely on external executables; for example, go, dep, and others.
* parses vendor.conf for dependencies.

