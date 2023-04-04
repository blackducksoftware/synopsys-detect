# Quickstart guide

The following is a simple example to help you get started using [solution_name].

## Step 1: Locate or acquire a source code project on which you will run [solution_name].

To run [solution_name] on junit4, which is an open source project written in Java and built with Maven, you could acquire
it by doing the following:
```
git clone https://github.com/junit-team/junit4.git
cd junit4
```

To understand what [solution_name] does, it can be helpful to think about what you would do if you wanted to discover this
project's dependencies without using [solution_name]. You might do the following:

1. Look in the project directory (junit4) for hints about how dependencies are managed. In this case, the *mvnw* and *pom.xml* files are hints that dependencies are managed using Maven.
1. Since it's a Maven project, you would likely run `./mvnw dependency:tree` to reveal the project's dependencies; both direct and transitive.

This is basically what [solution_name] does on this project. In addition, [solution_name] runs the
[blackduck_signature_scanner_name] on the directory, which may discover additional dependencies
added to the project by means other than the package manager.

## Step 2: Run [solution_name] connected to [blackduck_product_name].

To run [solution_name], you will need to provide login credentials for your [blackduck_product_name]
server. One way to do that is to add the following arguments to the command line:

* `--blackduck.url={your Black Duck server URL}`
* `--blackduck.api.token={your Black Duck access token}`

The command you run looks like this:

On Linux or Mac:

````
bash <(curl -s -L https://detect.synopsys.com/detect8.sh) --blackduck.url={your Black Duck server URL} --blackduck.api.token={your Black Duck access token}
````

On Windows:

````
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect8.ps1?$(Get-Random) | iex; detect" --blackduck.url={your Black Duck server URL} --blackduck.api.token={your Black Duck access token}
````

The operations performed by [solution_name] depends on what it finds in your source directory.
By default, [solution_name] considers the current working directory to be your source directory.

In the junit4 case, [solution_name] will:

1. Run the Maven detector, which uses Maven to discover dependencies.
2. Run the [blackduck_signature_scanner_name] which scans the files in the source directory to discover dependencies.
3. Upload the discovered dependencies to [blackduck_product_name].
4. Provide in the log a Black Duck Project BOM URL that you can use to view the results in [blackduck_product_name].

Point your browser to the Black Duck Project BOM URL to see the Bill Of Materials for junit4.

## Next steps

[solution_name] can be used on a variety of project types, and in a variety of ways, requiring its behavior to be highly configurable.
For more detailed information on how to configure [solution_name] for your needs, see [Configuring Synopsys Detect](../configuring/overview.md).

