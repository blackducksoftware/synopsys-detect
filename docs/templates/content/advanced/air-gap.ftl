# Air gap mode

To run ${solution_name} on an air-gapped computer or network, you must first download and install
files that ${solution_name} normally downloads as it runs. These include inspectors
for Docker, Gradle, and NuGet.

Air gap archives are available for download from the
[${division_name} ${binary_repo_type} server](${binary_repo_ui_url_base}/${binary_repo_repo}/${binary_repo_pkg_path}/${project_name}).

As an alternative, you can create an air gap archive by running ${solution_name} with the
[-z or --zip command line option](../../70-quickreference/#synopsys-detect-modes).
The archive created contains the ${solution_name} .jar and the inspectors.

Before running ${solution_name} in air gap mode, unzip the air gap archive to create the air gap directory.

To run ${solution_name} in air gap mode, invoke the ${solution_name} .jar file at the top level of
the air gap directory. Refer to [Running the ${solution_name} .jar](../../30-running/#running-the-synopsys-detect-jar) for more information.
