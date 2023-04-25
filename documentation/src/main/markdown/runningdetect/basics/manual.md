# Installation Best Practices

Manually installing [solution_name] enables you to assure that the running version is compatible with your environment. Invoking [solution_name] with the bash and powershell scripts is easy but automatically downloaded updates may not be compatible with your environment.  

The best practice for resilience is to add [solution_name] on the path, allowing for an easier invocation than even the bash and powershell scripts. It still allows easy updating without modifying commands just as the bash and powershell scripts do. This is the recommended best practice approach when resiliency is required.  

## Basic Manual Installation Steps

1. Download Java and make sure it is on your PATH
2. Download the version of [solution_name] you want to use from https://sig-repo.synopsys.com/bds-integrations-release/com/synopsys/integration/synopsys-detect/
    * You should download the air-gap zip if you do not want [solution_name] to download Inspectors at runtime
3. Put the [solution_name] jar/zip somewhere you can manage it
    * Examples: 
    *    Mac/Linux: 	$HOME/synopsys-detect/download/synopsys-detect-X.X.X.jar
    *    Windows:	   C:\Program Files\synopsys-detect\download\synopsys-detect-X.X.X.jar
4. You can now run [solution_name]
    * Example: java -jar $HOME/synopsys-detect/download/synopsys-detect-X.X.X.jar --help

## Mac/Linux Best Practice Installation Steps for Resilience  

1. Download Java and make sure it is on your PATH
2. Download the version of [solution_name] you want to use from https://sig-repo.synopsys.com/bds-integrations-release/com/synopsys/integration/synopsys-detect/
   * You should download the air-gap zip if you do not want [solution_name] to download Inspectors at runtime
3. Create a symlink for the [solution_name] jar
   *     ln -s $HOME/synopsys-detect/download/synopsys-detect-X.X.X.jar $HOME/synopsys-detect/download/latest-detect.jar
4. Create a bash script named "detect" with the following content.
   *     #!/bin/bash
   *     java -jar $HOME/synopsys-detect/download/latest-detect.jar "$@"
5. Add the script to your PATH variable
   *     export PATH=${PATH}:${path_to_folder_containing_detect_script}
6. OR instead of altering your PATH you can place the script in a directory that is already on your PATH
   * Example: /usr/local/bin
7. You can now run [solution_name]
   * Example: detect --help

## Windows Best Practice Installation Steps for Resilience 

1. Download Java and make sure it is on your PATH
2. Download the version of [solution_name] you want to use from https://sig-repo.synopsys.com/bds-integrations-release/com/synopsys/integration/synopsys-detect/
   * You should download the air-gap zip if you do not want [solution_name] to download Inspectors at runtime
3. Create a symbolic link for the [solution_name] jar, called latest-detect.jar
   * Start a command prompt in the folder you downloaded detect.
   * Run the following: mklink latest-detect.jar synopsys-detect-X.X.X.jar
4. Create a bat script named "detect.cmd" in the same folder with the following content
   *     @java -jar "C:\Program Files\synopsys-detect\download\latest-detect.jar" %*
5. Add the script to your PATH variable
   * In File Explorer right-click on the This PC (or Computer) icon, then click Properties -> Advanced System Settings -> Environment Variables
   * Under System Variables select Path, then click Edit
   * Add an entry with the path to the folder containing the script "C:\Program Files\synopsys-detect\download\"
7. You can now run [solution_name]
   * Example: detect --help
