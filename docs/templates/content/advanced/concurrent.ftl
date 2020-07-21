# Concurrent execution

Executing multiple ${solution_name} runs in parallel requires additional configuration.

When the same user executes multiple runs of ${solution_name} in parallel, there is (when
using default settings) contention over the following resources:

1. The location to which your curl command (if you use one) downloads ${bash_script_name} or ${powershell_script_name},
which can be controlled via your curl command.
1. The location to which ${bash_script_name} or ${powershell_script_name} (if you use one of them) downloads the
${solution_name} .jar, which can be
controlled via environment variable DETECT_JAR_DOWNLOAD_DIR.
1. The location to which the ${solution_name} .jar downloads files like the signature scanner, which can be
controlled via the property --detect.output.path.

Solutions available for #1 include:

* Download the script only once in advance, or
* Download the script to a unique location for each run.

The simplest solution to #2 and #3 is to assign each run a unique pair of download locations
using environment variable DETECT_JAR_DOWNLOAD_DIR and property --detect.output.path. Here is
a very simple bash script that demonstrates this approach:
```
#!/bin/bash
curl -O https://detect.synopsys.com/detect.sh
chmod +x detect.sh
for i in {1..10}; do
echo "==================== Starting run $i ====================="
export DETECT_JAR_DOWNLOAD_DIR=/tmp/rundir/detectjardownload${r"${i}"}
./detect.sh --detect.source.path=/projects/integration-common --detect.output.path=/tmp/rundir/detectoutput${r"${i}"} | tee /tmp/rundir/log${r"${i}"}.txt &
done
```