# Quickstart guide

To help you get started using ${solution_name}, here's a simple example.

## Step 1: Locate or acquire a source code project on which you will run ${solution_name}

Since ${solution_name} normally runs as a post-build step, you should be able to build the project
you choose to run ${solution_name} on.

For example, to run ${solution_name} on log4j (a Java/Maven/Git project), you could do the following:
```
git clone https://github.com/junit-team/junit4.git
cd junit4
```

If you wanted to discover components on this project without using ${solution_name}, you might do the following:

1. Look in the project directory in an attempt to determine how dependencies are managed in this project. Seeing the mvnw and pom.xml files would indicate that dependencies are managed using Maven.
1. Since it's a Maven project, you might run `./mvnw dependency:tree` to reveal the project's dependencies.

This is exactly what ${solution_name} will do. In addition, ${solution_name} will run the
${blackduck_signature_scanner_name} on the directory, which can discover additional dependencies
added to the project by any means other than the package manager.

## Step 2: Run ${solution_name} in offline mode

Running ${solution_name} in offline mode eliminates the need to provide
connection details to ${blackduck_product_name} or ${polaris_product_name},
so provides a relatively simple way to get started.

At the top level of the project directory, run ${solution_name} in offline mode:

    bash <(curl -s -L https://detect.synopsys.com/detect.sh) --blackduck.offline.mode=true

The operations performed by ${solution_name} will depend on what it finds in your source directory.
(By default, ${solution_name} considers the current working directory to be your source directory.

In the log4j case, ${solution_name} will:

1. Run the Maven detector, creating one BDIO (Black Duck Input Output) (.jsonld) file that contains the dependencies discovered using Maven.
2. Run the ${blackduck_signature_scanner_name}, creating a .json file that contains the dependencies discovered by the ${blackduck_signature_scanner_name}.

In offline mode, neither of these will be uploaded to ${blackduck_product_name}.

To locate these files, look in the log for the message "Run directory: ...". These files are located inside
the specified run directory.

## Step 3: Run ${solution_name} connected to ${blackduck_product_name}

If you have access to a ${blackduck_product_name}, you can re-run ${solution_name},
this time connecting (and uploading results) to ${blackduck_product_name}.

In this mode, you will not include the `--blackduck.offline.mode=true` command line option.
Instead, you will provide connection details for your ${blackduck_product_name} server
using the following command line arguments:

* `--blackduck.url={your Black Duck server URL}`
* `--blackduck.username={your Black Duck username}`
* `--blackduck.password={your Black Duck password}`

At the top level of the project directory, run ${solution_name} with ${blackduck_product_name}
connection details:

    bash <(curl -s -L https://detect.synopsys.com/detect.sh) --blackduck.url={your Black Duck server URL} --blackduck.username={your Black Duck username} --blackduck.password={your Black Duck password}

Run this way, ${solution_name} will perform the same steps as in the previous run, and also upload
the discovered dependencies to ${blackduck_product_name}, and provide a "Black Duck Project BOM" URL
that you can use to view the results in ${blackduck_product_name}.

## Next steps

Because ${solution_name} can be used on a variety of project types for a variety of purposes,
its behavior is highly configurable. More detailed information on how to configure ${solution_name}
for your needs is provided in the sections that follow.


