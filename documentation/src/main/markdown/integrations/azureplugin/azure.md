# Azure DevOps (ADO) Plugin

The [company_name] [solution_name] for Azure DevOps plugin is architected to seamlessly integrate [company_name] [solution_name] with Azure DevOps build and release pipelines. [company_name] [solution_name] makes it easier to set up and scan code bases using a variety of languages and package managers.

The [company_name] [solution_name] plugin for Azure DevOps supports native scanning in your Azure DevOps environment to run Software Composition Analysis (SCA) on your code.

As a Synopsys and Azure DevOps user, [company_name] [solution_name] Extension for Azure DevOps enables you to:

- Run a component scan in an Azure DevOps job and create projects and releases in Black Duck through the Azure DevOps job.
- After a scan is complete, the results are available on the [blackduck_product_name] server (for SCA).

Using the [company_name] [solution_name] Extension for Azure DevOps together with [blackduck_product_name] enables you to use Azure DevOps to automatically create [blackduck_product_name] projects from your Azure DevOps projects.

**Note:** The Azure plugin currently supports [company_name] [solution_name] 9.X.

## Invoking [company_name] [solution_name]
Synopsys recommends invoking [company_name] [solution_name] from the CI (build) pipeline.  Scanning during CI enables [company_name] [solution_name] to break your application build, which is effective for enforcing policies like preventing the use of disallowed or vulnerable components.

   <figure>
    <img src="../azureplugin/images/introscreen.png"
         alt="Intro">
    <figcaption>Intro screen</figcaption>
</figure>

## Basic workflow

Using [company_name] [solution_name] to analyze your code in Azure involves the following basic steps:

1. Make sure you satisfy system and other requirements.
1. Download and configure the [company_name] [solution_name] extension in Azure.
1. Configure build agent and pipeline.
1. Configure [blackduck_product_name] connection.
1. Configure [company_name] [solution_name] arguments.
1. Run pipeline and invoke scan.
1. Examine the analysis results.
