# Concurrent execution

Concurrent execution of [bd_product_long] by the same user can result in collisions as the [detect_product_short] script,
the [detect_product_short] .jar, the [detect_product_short] inspectors, and the [blackduck_signature_scanner_name]
are each downloaded to the same default location during execution. There are also potential race conditions that
can occur when multiple concurrent runs of [detect_product_short] create or update the same [bd_product_short]
project/version or codelocation.

Concurrent execution of [detect_product_short] runs that include Docker image inspection involves additional
challenges. For that scenario, we recommend engaging [professional_services] for a solution tailored to your environment.
The rest of this page addresses scenarios that do not involved inspecting Docker images.

The recommended way for a single user to execute multiple [detect_product_short] runs concurrently and
avoid the collisions mentioned above is to:

1. Run [detect_product_short] using the [air gap](../downloadingandinstalling/airgap.md) capability. This avoids downloading the [detect_product_short] script, .jar, or inspectors during execution.
1. Manually download and install the [blackduck_signature_scanner_name], and point [detect_product_short] to it. This avoids downloading the [blackduck_signature_scanner_name] during execution.
1. Ensure that concurrent runs do not attempt to create or update the same [bd_product_short] project/version, or the same codelocation.

To accomplish the first two:

1. Log into [bd_product_short], and under Tools > Legacy Downloads, download and unzip the [blackduck_signature_scanner_name].
1. Download the [detect_product_short] "no docker" air gap zip from the location specified in [download locations](../downloadingandinstalling/downloadlocations.md), and unzip it. More details on using air gap mode can be found on the [air gap page](../downloadingandinstalling/airgap.md).
1. Run [detect_product_short] as shown in this example:

````
java -jar {airgap dir}/blackduck-detect-{version}.jar --detect.blackduck.signature.scanner.local.path={scan.cli-yourBlackDuckVersion dir}
````
