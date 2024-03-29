# Jenkins Plugin

The [company_name] [solution_name] for Jenkins plugin enables you to install and run [company_name] [solution_name] in your Jenkins instance. 

[company_name] [solution_name] scans code bases in your projects and folders to perform compositional analysis and functions as a [blackduck_product_name] intelligent scan client. [company_name] [solution_name] sends scan results to [blackduck_product_name], which generates risk analysis when identifying open source components, licenses, and security vulnerabilities.

[company_name] [solution_name] is designed to run in the native build environment of the project that you want to scan. It uses the same global configuration as your Jenkins instance and provides a pass-through for [company_name] [solution_name]. You can run as a post-build action in a Jenkins Freestyle job or run as a Pipeline step using a Pipeline script in a PipeLine job.
After running a [company_name] [solution_name] scan following the Jenkins build, you can view the scan results in your [blackduck_product_name] instance.

Refer to [How it Works](../../gettingstarted/howitworks.md) to learn more about [company_name] [solution_name].

## Basic workflow
1.      Make sure you satisfy system and other requirements.
*   Install the [company_name] [solution_name] plugin in Jenkins.
*   Configure [blackduck_product_name] connection and plugin.

2.      Run a Jenkins build on your project.

3.      [company_name] [solution_name] scans the project, for example, the scan might be a step in a Jenkins Pipeline job or post-build action in a Freestyle job.

4.      [company_name] [solution_name] sends the scan results to [blackduck_product_name] for analysis.

After running a [company_name] [solution_name] scan following the Jenkins build, you can view the scan results in your [blackduck_product_name] instance.
