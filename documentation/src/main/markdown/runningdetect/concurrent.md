# Concurrent execution

Concurrent execution of [solution_name] by the same user can result in collisions as the [solution_name] script,
the [solution_name] .jar, the [solution_name] inspectors, and the [blackduck_signature_scanner_name]
are each downloaded to the same default location during execution. There are also potential race conditions that
can occur when multiple concurrent runs of [solution_name] create or update the same [blackduck_product_name]
project/version or codelocation.

Concurrent execution of [solution_name] runs that include Docker image inspection involves additional
challenges. For that scenario, we recommend engaging [professional_services] for a solution tailored to your environment.
The rest of this page addresses scenarios that do not involved inspecting Docker images.

The recommended way for a single user to execute multiple [solution_name] runs concurrently and
avoid the collisions mentioned above is to:

1. Run [solution_name] using the [air gap](../downloadingandinstalling/airgap.md) capability. This avoids downloading the [solution_name] script, .jar, or inspectors during execution.
1. Manually download and install the [blackduck_signature_scanner_name], and point [solution_name] to it. This avoids downloading the [blackduck_signature_scanner_name] during execution.
1. Ensure that concurrent runs do not attempt to create or update the same [blackduck_product_name] project/version, or the same codelocation.

To accomplish the first two:

1. Log into [blackduck_product_name], and under Tools > Legacy Downloads, download and unzip the [blackduck_signature_scanner_name].
1. Download the [solution_name] "no docker" air gap zip from the location specified in [download locations](../downloadingandinstalling/downloadlocations.md), and unzip it. More details on using air gap mode can be found on the [air gap page](../downloadingandinstalling/airgap.md).
1. Run [solution_name] as shown in this example:

````
java -jar {airgap dir}/synopsys-detect-{version}.jar --detect.blackduck.signature.scanner.local.path={scan.cli-yourBlackDuckVersion dir}
````
