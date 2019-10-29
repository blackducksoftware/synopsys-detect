# Shell script configuration

The ${solution_name} Bash and PowerShell scripts use the following (optional) environment variables.

### DETECT_SOURCE

The URL of the ${solution_name} .jar file to download and run.

### DETECT_LATEST_RELEASE_VERSION

If you would like to run a ${solution_name} version other than the latest, set DETECT_LATEST_RELEASE_VERSION to the ${solution_name} version you would like to run (for example: 5.6.2). DETECT_SOURCE has precedence over DETECT_LATEST_RELEASE_VERSION.

### DETECT_VERSION_KEY

If neither DETECT_SOURCE nor DETECT_LATEST_RELEASE_VERSION is specified, the script will use the version key to query Artifactory for the correct version to download. By default it will look for DETECT_LATEST, however the detect artifactory also includes keys for (some of) the major versions of ${solution_name} such as DETECT_LATEST_4.

### DETECT_JAR_PATH

If DETECT_JAR_PATH is provided, the script will use this location when downloading and running detect. The location of the jar will be DETECT_JAR_PATH/${project_name}-{version}.jar. The Bash script will default to '/tmp' if no option is specified.

### TMP (PowerShell Only)

If DETECT_JAR_PATH is not provided, the script will use the environment 'TMP' variable as the folder for the ${solution_name} jar path.

### $HOME (PowerShell Only)

If DETECT_JAR_PATH is not provided and no 'TMP' variable can be found, the '$HOME/tmp' folder will be used for the ${solution_name} jar path.

### DETECT_EXIT_CODE_PASSTHRU (PowerShell Only)

Setting this variable to '1' will cause the script to simply return the exit code but not exit. By default, the ${solution_name} PowerShell script will exit with the exit code of ${solution_name}. This is desirable because many CI's such as TFS will look at the scripts exit code to decide build status. However it may be undesirable to exit the script such as when debugging in a terminal.

### DETECT_SKIP_JAVA_TEST (PowerShell Only)

Setting this variable to '1' causes the script not to ensure that java is on the path. By default the script will attempt to execute "java -version" to ensure that java is available and executable.

### DETECT_CURL_OPTS (Bash Only)

Additional options can be specified to the curl command used to download detect such as proxy settings. The PowerShell does not support this as it does not use curl. To supply proxy information to the PowerShell you can simply set the ${solution_name} proxy settings as environment variables.

### DETECT_JAVA_OPTS (Bash Only)

Additional options can be specified to the java command used to execute ${solution_name}. The PowerShell script does not currently support this setting.