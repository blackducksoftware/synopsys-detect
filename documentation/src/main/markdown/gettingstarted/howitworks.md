# How [company_name] [solution_name] Works

This page provides an overview of how [company_name] [solution_name] works.

## How [company_name] [solution_name] does its work

[company_name] [solution_name] performs the following basic steps when scanning open source software, assuming you are connected to a [blackduck_product_name] instance.

1. [company_name] [solution_name] uses the project's package manager to derive the hierarchy of dependencies known to that package manager. For example, on a Maven project, [company_name] [solution_name] executes an mvn dependency:tree command, and derives dependency information from the output.
1. Runs the [blackduck_signature_scanner_name] on the project. This might identify additional dependencies not known to the package manager such as a .jar file copied into the project directory.
1. Uploads both sets of results (dependency details) to [blackduck_product_name] creating the project/version if it does not already exist. [blackduck_product_name] uses the uploaded dependency information to build the Bill Of Materials (BOM) for the project/version.

In this case, the user has provided [blackduck_product_name] connection details through property settings to [company_name] [solution_name], specifying that results (project dependency details) are to be uploaded to [blackduck_product_name]
By combining all these techniques, [company_name] [solution_name] is capable of scanning a wide range of software projects
utilizing a variety of package managers and programming languages for open source components.
