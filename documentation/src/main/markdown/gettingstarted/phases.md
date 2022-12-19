# [solution_name] Processing

[solution_name] processing can be broken into the following phases:

## Initialization phase

In this phase, [solution_name] does verification checks on the user-provided configuration, checks connectivity to any external systems needed for the run, and creates any required directories.

## Run phase

In this phase, [solution_name] processes an ordered list of tools, invoking all that apply, which depends on how [solution_name] is configured.

[solution_name] analysis is done using an ordered set of tools that you specify using [solution_name] properties.

* By default, the build detector tool is run. This detector runs after a build and has access to both build artifacts and build tools; it produces the most accurate results.
* If [blackduck_product_name] connection details are provided, the [blackduck_product_name] signature scanner tool also runs by default.

Depending on project contents, the detector tool runs different types of detectors to find and extract dependencies from supported package managers. For example, if [solution_name] finds a pom.xml file, it runs the Maven detector. If [solution_name] finds Gradle files, it runs the Gradle detector.

At the end of the run phase, [solution_name] uploads results to [blackduck_product_name], and optionally performs tasks such as generating a risk report or checking for policy violations.

## Cleanup phase

During the cleanup phase, [solution_name] removes temporary files and directories before exiting.
