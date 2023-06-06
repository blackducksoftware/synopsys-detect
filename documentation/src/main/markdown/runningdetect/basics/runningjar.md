# Running the [solution_name] .jar

Recent versions of the [solution_name] .jar file are available for download from the location specified in [download locations](../../downloadingandinstalling/downloadlocations.md).

To run [solution_name] by invoking the .jar file:

````
java -jar {path to .jar file}
````

For example:

````
curl -O https://sig-repo.synopsys.com/bds-integrations-release/com/synopsys/integration/synopsys-detect/5.6.2/synopsys-detect-5.6.2.jar
java -jar synopsys-detect-5.6.2.jar
````

You can use the [solution_name] Bash script ([bash_script_name]) to download the [solution_name] .jar file:

````
export DETECT_DOWNLOAD_ONLY=1
./detect8.sh
````
