# GoLang support

Detect has four detectors for GoLang:

* [GoDepLock Detector](#godeplockdetector)
* [GoDepCli Detector](#godepclidetector)
* [GoVendor Detector](#govendordetector)
* [GoVndr Detector](#govndrdetector)

<a name="godeplockdetector"></a>
# GoDepLock Detector

The GoDepLock Detector can discover dependencies of GoLang projects.

The GoDepLock Detector will attempt to run on your project if a Gopkg.lock file is found in your source directory.

The GoDepLock Detector does not rely on external executables (go, dep, etc.).

The GoDepCli Detector parses Gopkg.lock for dependencies.

<a name="godepclidetector"></a>
# The GoDepCli Detector

The GoDepCli Detector can discover dependencies of go language (GoLang) projects.

The GoDepCli Detector will attempt to run on your project if files are found in your source directory with extension "go" ("*.go").

The GoDepCli Detector requires "go" and "dep" executables:
* Detect looks for go on $PATH.
* Detect looks for dep in your source directory.

The GoDepCli Detector looks in your source directory for a Gopkg.lock file. If it does not exist, it runs dep command(s) to create it. Then the GoDepCli Detector parses Gopkg.lock for dependencies.

<a name="govendordetector"></a>
# The GoVendor Detector

The GoVendor Detector can discover dependencies of go language (GoLang) projects.

The GoVendor Detector will attempt to run on your project if the file vendor/vendor.json is found in your source directory.

The GoVendor Detector does not rely on external executables (go, dep, etc.).

The GoVendor Detector parses vendor/vendor.json for dependencies.



<a name="govndrdetector"></a>
# The GoVndr Detector

The GoVndr Detector can discover dependencies of go language (GoLang) projects.

The GoVndr Detector will attempt to run on your project if the file vendor.conf is found in your source directory.

The GoVndr Detector does not rely on external executables (go, dep, etc.).

The GoVndr Detector parses vendor.conf for dependencies.

