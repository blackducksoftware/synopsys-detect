# GoLang support

${solution_name} has four detectors for GoLang:

* [GoDepLock detector](#godeplockdetector)
* [GoDepCli detector](#godepclidetector)
* [GoVendor detector](#govendordetector)
* [GoVndr detector](#govndrdetector)

<a name="godeplockdetector"></a>
# GoDepLock detector

The GoDepLock detector discovers dependencies of GoLang projects.

The GoDepLock detector attempts to run on your project if a Gopkg.lock file is found in your source directory.

The GoDepLock detector does not rely on external executables; for example, go, dep, and others.

The GoDepCli detector parses Gopkg.lock for dependencies.

<a name="godepclidetector"></a>
# The GoDepCli detector

The GoDepCli detector discovers dependencies of go language (GoLang) projects.

The GoDepCli detector attempts to run on your project if files are found in your source directory with extension "go" ("*.go").

The GoDepCli detector requires *go* and *dep* executables:
* ${solution_name} looks for go on $PATH.
* ${solution_name} looks for dep in your source directory.

The GoDepCli detector looks in your source directory for a Gopkg.lock file. If it does not exist, it runs dep commands to create it. Then the GoDepCli detector parses Gopkg.lock for dependencies.

<a name="govendordetector"></a>
# The GoVendor detector

The GoVendor detector discovers dependencies of go language (GoLang) projects.

The GoVendor detector attempts to run on your project if the file vendor/vendor.json is found in your source directory.

The GoVendor detector does not rely on external executables; for example, go, dep, and others.

The GoVendor detector parses vendor/vendor.json for dependencies.



<a name="govndrdetector"></a>
# The GoVndr detector

The GoVndr detector discovers dependencies of go language (GoLang) projects.

The GoVndr detector attempts to run on your project if the file vendor.conf is found in your source directory.

The GoVndr detector does not rely on external executables; for example, go, dep, and others.

The GoVndr detector parses vendor.conf for dependencies.

