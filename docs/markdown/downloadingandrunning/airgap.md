# Air Gap Mode

To run [solution_name] on an air-gapped computer or network, you must first download and install
files that [solution_name] normally downloads as it runs. These include inspectors
for Docker, Gradle, NuGet and more.

## Downloading or creating an air gap zip

Air gap archives are available for download from the
[division_name] [binary_repo_type] server: [binary_repo_ui_url_project].

As an alternative, you can create an air gap archive by running [solution_name] with the
-z or --zip command line option.
Optionally you can follow --zip with a space and an argument (for example: --zip FULL) to customize the air gap zip. Possible values: FULL (produce a full air gap zip; the default), NO_DOCKER (do not include the Docker Inspector).
The archive created contains the [solution_name] .jar and the inspectors.

## Running in air gap mode

Before running [solution_name] in air gap mode, unzip the air gap archive to create the air gap directory.

To run [solution_name] in air gap mode, invoke the [solution_name] .jar file at the top level of
the air gap directory. Refer to [Running the Synopsys Detect .jar](runningjar.md) for more information.
