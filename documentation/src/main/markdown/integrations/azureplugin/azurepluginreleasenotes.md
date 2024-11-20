# Release Notes for Azure DevOps Plugin

## Version 10.0.0
**Notice**

The [company_name] Software Integrity Group is now [var_company_name]    
* As part of this activity, sig-repo.synopsys.com and detect.synopsys.com are being deprecated and will be decomissioned on Feb. 14th 2025. Please make use of repo.blackduck.com and detect.blackduck.com respectively.    
* Refer to the [Black Duck Domain Change FAQ](https://community.blackduck.com/s/article/Black-Duck-Domain-Change-FAQ#SDInstru).
<note type="note">It is recommended that customers continue to maintain `sig-repo.synopsys.com`, and `repo.blackduck.com` on their allow list until February 2025 when `sig-repo.synopsys.com` will be fully replaced by `repo.blackduck.com`.</note>

### Upgrading from [company_name] [detect_product_short] plugin to [detect_product_long] plugin
Prior to upgrading to the [detect_product_short] plugin, you must manually uninstall the [company_name] [detect_product_short] plugin. 
	* Installing the [detect_product_long] plugin will ensure you receive future plugin updates.   

After uninstalling a previous [company_name] [detect_product_short] plugin or if you are a new user, you may proceed with installing the [detect_product_long] plugin available at the following [Marketplace location](https://marketplace.visualstudio.com/items?itemName=blackduck.blackduck-detect).    

**New features**

* Plugin updated to support [detect_product_long] 10.
	* [detect_product_long] [Release Notes](../../currentreleasenotes.md)

## Version 9.0.1

<note type="attention">To continuing using the deprecated [company_name] [detect_product_short] plugin, it is essential to update to version 9.0.1, available at the [Previous Marketplace location](https://marketplace.visualstudio.com/items?itemName=synopsys-detect.synopsys-detect).</note>

**Changed features**

* Adds logic to pull the [detect_product_short] jar file. If this is not accessible, the plugin will fall back to the synopsys-detect jar file.

<note type="important">It is essential to update to 9.0.1 before `detect.synopsys.com` is decommissioned on Feb. 14th 2025.</note>

## Version 9.0.0
**New features**

* Updated the plugin to use [company_name] [solution_name] 9.   
	* [company_name] [solution_name] [Release Notes](../../currentreleasenotes.md)

**Resolved issues**

* (DETECTADO-92) Pipeline will now fail as expected when invalid proxy details are provided for Linux and Mac Agents.

## Version 8.1.0
**New features**

* (DETECTADO-95) Plugin is now able to inherit the Azure agent's proxy configuration.
	* Refer to [Configuring a Build Agent with a proxy](configuringbuildagent.md) for more information.

## Version 8.0.0
**New features**

* Updated the plugin to use [company_name] [solution_name] 8.

## Version 7.0.0
**New features** 

* Updated the plugin to use [company_name] [solution_name] 7.
* Added the ability to run [company_name] [solution_name] in AirGap mode.

## Version 6.0.0
**Resolved issues**

* (DETECTADO-68) Improved error messaging when invalid proxy details are used.
* (DETECTADO-70) Resolved issue wherein passing properties on new lines would cause Detect ADO to fail.
* (DETECTADO-71) Resolved issue with TLS errors being thrown on Windows hosted agents.

**Changed features**

* The plugin versioning was changed to match the major version of [company_name] [solution_name] that it is designed to work with, for example Detect ADO 6.0.0 works with [company_name] [solution_name] major version 6.

## Version 3.0.0
**New features**

* Added the capability for the script to use the tool directory in the ADO agent to store the [company_name] [solution_name] JAR. It will continue to use this JAR as long as the JAR version matches the version specified in the task configuration.
* Added support for using Linux and Mac agents.

**Changed features**

* Removed support for Polaris.

## Version 2.0.0
**New features**

* Added support for Polaris.

**Changed features**

* Product renamed to [company_name] [solution_name] for Azure DevOps.

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
