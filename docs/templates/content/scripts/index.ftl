# Shell script configuration

The ${solution_name} Bash and PowerShell scripts are configured by setting the environment variables.

In Bash, an environment variable is set as follows:

    export ENV_VAR_NAME=value

In Command or Batch, an environment variable is set as follows:
    set ENV_VAR_NAME=value

In PowerShell, an environment variable is set as follows:
    $Env:ENV_VAR_NAME = value
Note it is generally recommended to run the ${solution_name} PowerShell script from a Command line, not from within PowerShell itself.

| Variable             | Purpose                                             | Value           | Notes                                      |
| -------------------- | --------------------------------------------------- | --------------- | ------------------------------------------ |
| DETECT_SOURCE        | Download the ${solution_name} .jar from a given URL |  The URL of the ${solution_name} .jar file to download and run |                                            |
| DETECT_LATEST_RELEASE_VERSION | Run a given ${solution_name} version |  A ${solution_name} version (example: 5.6.2) | If you would like to run a ${solution_name} version other than the latest, set DETECT_LATEST_RELEASE_VERSION to the ${solution_name} version you would like to run (for example: 5.6.2). DETECT_SOURCE has precedence over DETECT_LATEST_RELEASE_VERSION. You can see the available ${solution_name} versions in [Artifactory](${binary_repo_url_base}/webapp/#/artifacts/browse/tree/General/${binary_repo_repo}/${binary_repo_pkg_path}/${project_name}). |
| DETECT_VERSION_KEY | Continue running an earlier major version of ${solution_name} | DETECT_LATEST (default), DETECT_LATEST_5, DETECT_LATEST_4 | If neither DETECT_SOURCE nor DETECT_LATEST_RELEASE_VERSION is specified, the script will use the version key to query Artifactory for the correct version to download. By default it will look for DETECT_LATEST, however the ${solution_name} artifactory also includes keys for (some of) the major versions of ${solution_name} such as DETECT_LATEST_4. You can view the available values for DETECT_VERSION_KEY in [Artifactory](${binary_repo_url_base}/webapp/#/artifacts/browse/tree/Properties/${binary_repo_repo}/${binary_repo_pkg_path}/${project_name}). |
| DETECT_JAR_PATH | Change the ${solution_name} .jar download dir | The path to the .jar file download directory | If DETECT_JAR_PATH is provided, the script will use this location when downloading and running detect. The location of the jar will be DETECT_JAR_PATH/${project_name}-{version}.jar. The Bash script will default to '/tmp' if no option is specified. |
| TMP (PowerShell only) | Provides the user's temporary directory | The path to a directory for temporary files | If DETECT_JAR_PATH is not provided, the script will use the environment 'TMP' variable as the folder for the ${solution_name} .jar path. |
| HOME (PowerShell only) | Provides the user's home directory | The path to the user's home directory | If DETECT_JAR_PATH is not provided and no 'TMP' variable can be found, the '$HOME/tmp' folder will be used for the ${solution_name} jar path. |
| DETECT_JAVA_PATH | The path to the java executable to use to execute ${solution_name} | The path to the java executable file | |
| JAVA_HOME | The path to the Java installation directory. | The path to the Java home directory. | If DETECT_JAVA_PATH is not set, and JAVA_HOME is set, the script will execute $JAVA_HOME/bin/java. |
| PATH | The executable program path. | The list of directories in which the system looks for the executable file for each command executed (the syntax is operating system-specific). | If neither  DETECT_JAVA_PATH nor JAVA_HOME are set, the script assumes the directory containing the java executable file is on the path. |
| DETECT_EXIT_CODE_PASSTHRU (PowerShell only) | Prevent the shell from exiting on completion | 1 | Setting this variable to '1' will cause the script to simply return the exit code but not exit. By default, the ${solution_name} PowerShell script will exit with the exit code of ${solution_name}. This is desirable because many CI's such as TFS will look at the scripts exit code to decide build status. However it may be undesirable to exit the script in some situations, such as when debugging in a terminal. |
| DETECT_SKIP_JAVA_TEST (PowerShell only) | Skip the test for the presence of the java command |  1 | Setting this variable to '1' causes the script not to ensure that java is on the path. By default the script will attempt to execute "java -version" to ensure that java is available and executable. |
| DETECT_CURL_OPTS (Bash only) | Add the given options to any curl commands executed | curl command options (a string) | Use this variable to add options to the curl command used to download files such as the ${solution_name} .jar file. For example, you can use this variable to set proxy settings for curl. The PowerShell script does not support this as it does not use curl. To supply proxy information to the PowerShell you can simply set the ${solution_name} proxy settings as environment variables. |
| DETECT_JAVA_OPTS (Bash only) | Add the given options to the java command | java command options (a string) | Use this variable to add options to the java command used to execute ${solution_name}. The PowerShell script does not currently support this setting. |
| DETECT_DOWNLOAD_ONLY (Bash only) | Download the ${solution_name} .jar file, but do not run it | 1 | Set this variable to 1 to download, but not run, the ${solution_name} .jar file. The PowerShell script does not currently support this setting. |
| BLACKDUCK_PROXY_HOST (PowerShell only) | Proxy host to use to download detect | | When set, the PowerShell script will use the configured proxy information to download detect. Supports both environment variable styles (see below). |
| BLACKDUCK_PROXY_PORT (PowerShell only) | Proxy port to use to download detect | | When set, the PowerShell script will use the configured proxy information to download detect. Supports both environment variable styles (see below). |
| BLACKDUCK_PROXY_USERNAME (PowerShell only) | Proxy username to use to download detect | | When set, the PowerShell script will use the configured proxy information to download detect. Supports both environment variable styles (see below). |
| BLACKDUCK_PROXY_PASSWORD (PowerShell only) | Proxy password to use to download detect | | When set, the PowerShell script will use the configured proxy information to download detect. Supports both environment variable styles (see below). |

Note that proxy environment variables can be provided to the PowerShell script in both 'Bash' and 'PowerShell' formats. For example a given property name can be specified as "PROPERTY_NAME" or "property.name".
Note that the proxy can only be provided to the PowerShell script as environment variables.
