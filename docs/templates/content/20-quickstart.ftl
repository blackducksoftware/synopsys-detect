# Quickstart guide

The following is a simple example to help you get started using ${solution_name}.

## Step 1: Locate or acquire a source code project on which you will run ${solution_name}.

To run ${solution_name} on junit4, which is an open source project written in Java and built with Maven, you could acquire
it by doing the following:
```
git clone https://github.com/junit-team/junit4.git
cd junit4
```

To understand what ${solution_name} does, it can be helpful to think about what you would do if you wanted to discover this
project's dependencies without using ${solution_name}. You might do the following:

1. Look in the project directory (junit4) for hints about how dependencies are managed. In this case, the *mvnw* and *pom.xml* files are hints that dependencies are managed using Maven.
1. Since it's a Maven project, you would likely run `./mvnw dependency:tree` to reveal the project's dependencies; both direct and transitive.

This is exactly what ${solution_name} does on this project. In addition, ${solution_name} runs the
${blackduck_signature_scanner_name} on the directory, which discovers additional dependencies
added to the project by any means other than the package manager.

## Step 2: Run ${solution_name} in offline mode.

Running ${solution_name} in offline mode eliminates the need to provide
connection details to ${blackduck_product_name} or ${polaris_product_name},
while giving you an opportunity to see what ${solution_name} does on a project.

At the top level of the project directory (junit4), run ${solution_name} in offline mode:

    bash <(curl -s -L https://detect.synopsys.com/detect.sh) --blackduck.offline.mode=true

The operations performed by ${solution_name} depends on what it finds in your source directory.
By default, ${solution_name} considers the current working directory to be your source directory.

In the junit4 case, ${solution_name} will:

1. Run the Maven detector, creating one BDIO (Black Duck Input Output) (.jsonld) file that contains the dependencies discovered using Maven.
2. Run the ${blackduck_signature_scanner_name}, creating a .json file that contains the dependencies discovered by the ${blackduck_signature_scanner_name}.

In offline mode, neither of these are uploaded to ${blackduck_product_name}.

To locate these files, look in the log for the message "Run directory: ...". These files are located inside
the specified run directory.

## Step 3: Run ${solution_name} connected to ${blackduck_product_name}.

If you have access to a ${blackduck_product_name} server, you can re-run ${solution_name},
this time connecting and uploading results to ${blackduck_product_name}.

To connect ${solution_name} to ${blackduck_product_name}, replace the `--blackduck.offline.mode=true` command line argument
with the following three arguments that provide login details for your ${blackduck_product_name} server:

* `--blackduck.url={your Black Duck server URL}`
* `--blackduck.username={your Black Duck username}`
* `--blackduck.password={your Black Duck password}`

The command you run looks like this:

    bash <(curl -s -L https://detect.synopsys.com/detect.sh) --blackduck.url={your Black Duck server URL} --blackduck.username={your Black Duck username} --blackduck.password={your Black Duck password}

In this way, ${solution_name} performs the same steps it did in the offline run, plus
the following:

* Uploads the discovered dependencies to ${blackduck_product_name}.
* Provides in the log a "Black Duck Project BOM URL that you can use to view the results in ${blackduck_product_name}.

Point your browser to the Black Duck Project BOM URL to see the Bill Of Materials for junit4.

## Next steps

Because ${solution_name} can be used on a variety of project types in a variety of ways,
its behavior is highly configurable. More detailed information on how to configure ${solution_name}
for your needs is provided in the following sections.


