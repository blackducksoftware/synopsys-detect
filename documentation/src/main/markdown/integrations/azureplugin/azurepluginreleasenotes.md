# Release Notes for Azure DevOps Plugin

## Version 8.1.0
**New features**

* (DETECTADO-95) Plugin is now able to inherit the Azure agent's proxy configuration.
	* Refer to [Configuring a Build Agent with a proxy](configuringbuildagent.md) for more information.

## Version 8.0.0
**New features**

* Updated the plugin to use [solution_name] 8.

## Version 7.0.0
**New features**

* Updated the plugin to use [solution_name]7.
* Added the ability to run [solution_name] in AirGap mode.

## Version 6.0.0
**Resolved issues**

* (DETECTADO-68) Improved error messaging when invalid proxy details are used.
* (DETECTADO-70) Resolved issue wherein passing properties on new lines would cause Detect ADO to fail.
* (DETECTADO-71) Resolved issue with TLS errors being thrown on Windows hosted agents.

**Changed features**

* The plugin versioning was changed to match the major version of [solution_name] that it is designed to work with, for example Detect ADO 6.0.0 works with [solution_name] major version 6.

## Version 3.0.0
**New features**

* Added the capability for the script to use the tool directory in the ADO agent to store the [solution_name] JAR. It will continue to use this JAR as long as the JAR version matches the version specified in the task configuration.
* Added support for using Linux and Mac agents.

**Changed features**

* Removed support for Polaris.

## Version 2.0.0
**New features**

* Added support for Polaris.

**Changed features**

* Product renamed to [solution_name] for Azure DevOps.

## Version 1.1.0
**Changed features**

* The service endpoint configuration is now optional.
* Added support for using an API token for user authentication.

## Version 1.0.4
**Changed features**

* Improved proxy support and handling of supplied proxy arguments.

**Resolved issues**

* Resolved an issue that could result in an *Access denied* error.

## Version 1.0.3
**Resolved issues**
* Resolved an issue involving the SSL issue casting protocol.

## Version 1.0.0
* First release of product
