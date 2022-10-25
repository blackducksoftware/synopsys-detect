# Requirements for Azure DevOps

The following is a list of requirements for the [solution_name] in Azure DevOps integration.

* [blackduck_product_name] server.
  For the supported versions of [blackduck_product_name], refer to [Black Duck Release Compatibility](https://synopsys.atlassian.net/wiki/spaces/INTDOCS/pages/177799187/Black+Duck+Release+Compatibility).
* [blackduck_product_name] API token to use with Azure.
* Azure DevOps Services or Azure DevOps Server 17 or later
* Java.
  OpenJDK versions 8 and 11 are supported. Other Java development kits may be compatible, but only OpenJDK is officially supported for [blackduck_product_name].
* Access to the internet is required to download components from GitHub and other locations.

The [solution_name] plugin for Azure DevOps is supported on the same operating systems and browsers as [blackduck_product_name].

For scanning NuGet projects, verify that you have the NuGet tool installer set up in the build job definition.  You can download it at [https://docs.microsoft.com/en-us/Azure DevOps/build-release/tasks/tool/nuget?view=Azure DevOps](https://docs.microsoft.com/en-us/vsts/build-release/tasks/tool/nuget?view=vsts).

You can get the [solution_name] for Azure DevOps plugin at [VisualStudio Marketplace](https://marketplace.visualstudio.com/items?itemName=synopsys-detect.synopsys-detect).
