# Stateless Scan LCA

Stateless Scan, or Stateless Scan Mode, is a new way of running [solution_name] with [blackduck_product_name]. This mode is designed to be as fast as possible and does not persist any data on [blackduck_product_name]. Stateless Scan Mode has a unique set of restrictions, mode of configuration and set of results.  It is similar to Rapid Scan Mode however it differs in that it supports usage of the SIGNATURE_SCAN tool.  Stateless Scan allows non-persistent signature scans to be performed.

Enable this feature by adding [--detect.blackduck.scan.mode=STATELESS](../properties/configuration/blackduck-server.md#detect-scan-mode-advanced) to a run of Detect.

## Requirements and Limitations

 * Stateless scanning is available under [blackduck_product_name] “Limited Customer Availability (LCA)”.
 * Must be running [blackduck_product_name] 2022.10.0 or greater using the hosted KB.
 * Have Match as a Service (MaaS) enabled within [blackduck_product_name], a feature which will be available with the [blackduck_product_name] 2022.10.0 release.
 * A limited subset of Tools can be run.
    * The currently supported tools are: DETECTOR, BAZEL, SIGNATURE_SCAN and DOCKER.
    * The Stateless Signature Scan will not persist on Black Duck. 
    * All other tools are disabled when running in Stateless Scan mode.
 * Stateless Scan and non-persistent SIGNATURE_SCAN
    * To perform a non-persistent Signature Scan in Stateless mode, SIGNATURE_SCAN must be included within --detect.tools.
    * Permitted tools omitted from the detect.tools list will not be run.
 * Stateless Scan requires Black Duck policies. 
    * Stateless Scan only reports components that violate policies. 
    * If no policies are violated or there are no defined policies, then no components are returned.
 * Stateless Scan does not support ```detect.policy.check.fail.on.severities```
    * [solution_name] will fail with FAILURE_POLICY_VIOLATION if any component violates Black Duck polices with a CRITICAL or BLOCKER severity. 
    * See the Black Duck documentation for a list of policy conditions that are supported by Stateless Scan. 
 * Stateless Scan does not support ```detect.policy.check.fail.on.names```
 * Stateless Scan cannot create a Risk or Notices report.
 * Stateless Scan will not create a Project or Version on Black Duck.
 * Stateless Scan when running SIGNATURE_SCAN requires communication with Black Duck.
 
## Invocation
 * To invoke non-persistent (Rapid/Stateless) signature scan only
    * --detect.tools=SIGNATURE_SCAN --detect.blackduck.scan.mode=STATELESS
 * To invoke Rapid/Stateless package manager scans
    * --detect.tools=DETECTOR --detect.blackduck.scan.mode=STATELESS
    * --detect.tools=DETECTOR --detect.blackduck.scan.mode=RAPID
    * --detect.tools=BAZEL --detect.blackduck.scan.mode=RAPID
    * --detect.tools=BAZEL --detect.blackduck.scan.mode=STATELESS
    * --detect.tools=DOCKER --detect.blackduck.scan.mode=STATELESS
    * --detect.tools=DOCKER --detect.blackduck.scan.mode=RAPID
    * --detect.target.type=IMAGE --detect.blackduck.scan.mode=RAPID
    * --detect.target.type=IMAGE --detect.blackduck.scan.mode=STATELESS
 * To invoke combined Rapid/Stateless scans (non-exhaustive list):
    * --detect.tools=DETECTOR,SIGNATURE_SCAN --detect.blackduck.scan.mode=STATELESS
    * --detect.tools=DETECTOR,SIGNATURE_SCAN,DOCKER --detect.blackduck.scan.mode=STATELESS
    * --detect.tools=BAZEL,SIGNATURE_SCAN --detect.blackduck.scan.mode=STATELESS
    * --detect.tools=DETECTOR,DOCKER --detect.blackduck.scan.mode=RAPID

## Results

Unlike persistent scans, no data is stored on Black Duck and all scans are done transiently. These scans are primarily intended to be fast, although the SIGNATURE_SCAN can take some time as communication with Black Duck is a requirement.

The results are saved to a json file named 'name_version_BlackDuck_DeveloperMode_Result.json' in the Scan Output directory, where name and version are the project's name and version.

**NOTE:**
 * The format of this results file is dependent on Black Duck and in the future, different versions of Black Duck may produce a different file format.

The results are also printed in the logs:
```
2021-07-20 13:25:18 EDT INFO  [main] --- Stateless Scan Result: (for more detail look in the log for Stateless Scan Result Details)
2021-07-20 13:25:18 EDT INFO  [main] ---
2021-07-20 13:25:18 EDT INFO  [main] --- 		Critical and blocking policy violations for
2021-07-20 13:25:18 EDT INFO  [main] --- 			* Components: 0
2021-07-20 13:25:18 EDT INFO  [main] --- 			* Security: 5
2021-07-20 13:25:18 EDT INFO  [main] --- 			* License: 0
2021-07-20 13:25:18 EDT INFO  [main] ---
2021-07-20 13:25:18 EDT INFO  [main] --- 		Other policy violations
2021-07-20 13:25:18 EDT INFO  [main] --- 			* Components: 101
2021-07-20 13:25:18 EDT INFO  [main] --- 			* Security: 0
2021-07-20 13:25:18 EDT INFO  [main] --- 			* License: 0
2021-07-20 13:25:18 EDT INFO  [main] ---
2021-07-20 13:25:18 EDT INFO  [main] --- 		Policies Violated:
2021-07-20 13:25:18 EDT INFO  [main] --- 			Security Vulnerabilities Great Than Or Equal to High
2021-07-20 13:25:18 EDT INFO  [main] --- 			Warn on Low Security Vulnerabilities
2021-07-20 13:25:18 EDT INFO  [main] --- 			Warn on Medium Security Vulnerabilities
2021-07-20 13:25:18 EDT INFO  [main] ---
2021-07-20 13:25:18 EDT INFO  [main] --- 		Components with Policy Violations:
2021-07-20 13:25:18 EDT INFO  [main] --- 			Apache PDFBox 2.0.12 (maven:org.apache.pdfbox:pdfbox:2.0.12)
2021-07-20 13:25:18 EDT INFO  [main] --- 			Handlebars.js 4.0.11 (npmjs:handlebars/4.0.11)
2021-07-20 13:25:18 EDT INFO  [main] ---
2021-07-20 13:25:18 EDT INFO  [main] --- 		Components with Policy Violation Warnings:
2021-07-20 13:25:18 EDT INFO  [main] --- 			Acorn 5.5.3 (npmjs:acorn/5.5.3)
```
