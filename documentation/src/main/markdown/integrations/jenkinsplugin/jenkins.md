# Jenkins Plugin

The [solution_name] for Jenkins plugin enables you to install and run [solution_name] in your Jenkins instance. 

[solution_name] scans code bases in your projects and folders to perform compositional analysis and functions as a [blackduck_product_name] intelligent scan client. [solution_name] sends scan results to [blackduck_product_name], which generates risk analysis when identifying open source components, licenses, and security vulnerabilities.

[solution_name] is designed to run in the native build environment of the project that you want to scan. It uses the same global configuration as your Jenkins instance and provides a pass-through for [solution_name]. You can run as a post-build action in a Jenkins Freestyle job or run as a Pipeline step using a Pipeline script in a PipeLine job.
After running a [solution_name] scan following the Jenkins build, you can view the scan results in your [blackduck_product_name] instance.

Refer to [How it Works](../../gettingstarted/howitworks.md) to learn more about [solution_name].

## Basic workflow
1.      Make sure you satisfy system and other requirements.
*   Install the [solution_name] plugin in Jenkins.
*   Configure [blackduck_product_name] connection and plugin.

2.      Run a Jenkins build on your project.

3.      [solution_name] scans the project, for example, the scan might be a step in a Jenkins Pipeline job or post-build action in a Freestyle job.

4.      [solution_name] sends the scan results to [blackduck_product_name] for analysis.

After running a [solution_name] scan following the Jenkins build, you can view the scan results in your [blackduck_product_name] instance.
