# Air gap mode

To run ${solution_name} on an air-gapped computer or network, you must first download and install
files that ${solution_name} normally downloads as it runs. These include inspectors
for Docker, Gradle, and NuGet.

Air gap archives can be downloaded from the
[${division_name} ${binary_repo_type} server](${binary_repo_ui_url_base}/${binary_repo_repo}/${binary_repo_pkg_path}/${project_name}).

As an alternative, you can create an air gap archive by running ${solution_name} with the -z or --zip command line option.
The archive created contains the ${solution_name} .jar and the inspectors.

Before running ${solution_name} in air gap mode, unzip the air gap archive to create the air gap directory.

To run ${solution_name} in air gap mode, invoke the ${solution_name} .jar file at the top level of
the air gap directory. Refer to
[Running the ${solution_name} .jar](../2-running.md#running-the-synopsys-detect-jar) for more information.

To run ${solution_name} using the Gradle inspector, set the
[Gradle Inspector Air Gap Path](../properties/Detectors/gradle.md#gradle-inspector-airgap-path-advanced)

To run ${solution_name} using the NuGet inspector, set the
[NuGet Inspector Air Gap Path](../properties/Detectors/nuget.md#nuget-inspector-airgap-path-advanced)

To run ${solution_name} using the Docker inspector, set the
[Docker Inspector Air Gap Path](../properties/Detectors/docker.md#docker-inspector-airgap-path-advanced)


