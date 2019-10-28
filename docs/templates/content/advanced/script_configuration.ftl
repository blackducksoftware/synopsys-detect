# Shell script configuration

The Detect bash and powershell scripts use the following (optional) environment variables.

### DETECT_SOURCE

The URL of the Detect .jar file to download and run.

### DETECT_LATEST_RELEASE_VERSION

If you would like to run a Detect version other than the latest, set DETECT_LATEST_RELEASE_VERSION to the Detect version you would like to run (for example: 5.6.2). DETECT_SOURCE has precedence over DETECT_LATEST_RELEASE_VERSION.

### DETECT_VERSION_KEY

If no Detect source or version is specified, the script will use the version key to query Artifactory for the correct version to download. By default it will look for DETECT_LATEST, however the detect artifactory also includes keys for (some of) the major versions of Detect such as DETECT_LATEST_4.

### DETECT_JAR_PATH

If Detect jar path is provided, the script will use this location when downloading and running detect. The location of the jar will be DETECT_JAR_PATH/hub-detect-<version>.jar. The bash script will default to '/tmp' if no option is specified.

### TMP (Powershell Only)

If no Detect jar path is provided, the script will use the environment 'TMP' variable as the folder for the detect jar path.

### $HOME (Powershell Only)

If no Detect jar path is provided and no 'TMP' variable can be found, the '$HOME/tmp' folder will be used for the Detect jar path.

### DETECT_EXIT_CODE_PASSTHRU (Powershell Only)

Setting this variable to '1' will cause the script to simply return the exit code but not exit. By default, the Detect PowerShell script will exit with the exit code of Detect. This is desirable because many CI's such as TFS will look at the scripts exit code to decide build status. However it may be undesirable to exit the script such as when debugging in a terminal.

### DETECT_SKIP_JAVA_TEST (Powershell Only)

Setting this variable to '1' causes the script not to ensure that java is on the path. By default the script will attempt to execute "java -version" to ensure that java is available and executable.

### DETECT_CURL_OPTS (Bash Only)

Additional options can be specified to the curl command used to download detect such as proxy settings. The PowerShell does not support this as it does not use curl. To supply proxy information to the PowerShell you can simply set the Detect proxy settings as environment variables.

### DETECT_JAVA_OPTS (Bash Only)

Additional options can be specified to the java command used to execute Detect. The PowerShell script does not currently support this setting.