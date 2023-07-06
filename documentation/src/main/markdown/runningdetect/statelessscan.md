# Stateless Scan LCA

Stateless Scan, or Stateless Scan Mode, is a way of running [solution_name] with [blackduck_product_name]. This mode is designed to be as fast as possible and does not persist any data on [blackduck_product_name]. Stateless Scan Mode has a unique set of restrictions, mode of configuration, and set of results.  It is similar to Rapid Scan Mode, however it differs in that it supports usage of the SIGNATURE_SCAN, BINARY_SCAN, and CONTAINER_SCAN tools.

Enable this feature by adding [--detect.blackduck.scan.mode=STATELESS](../properties/configuration/blackduck-server.md#detect-scan-mode-advanced) to a run of Detect.

## Requirements and Limitations

### General Requirements
 * Stateless scanning is available under [blackduck_product_name] “Limited Customer Availability (LCA)”.
 * Have Match as a Service (MaaS) enabled within [blackduck_product_name], a feature which will be available with the [blackduck_product_name] 2022.10.0 release.
 * A limited subset of Tools can be run.
    * The currently supported tools are: DETECTOR, BAZEL, SIGNATURE_SCAN, DOCKER, BINARY_SCAN, and CONTAINER_SCAN.
    * All Stateless Scans will not persist on Black Duck. 
    * All other tools are disabled when running in Stateless Scan mode.
 * Stateless Scan requires Black Duck policies. 
    * Stateless Scan only reports components that violate policies. 
    * If no policies are violated or there are no defined policies, then no components are returned.
 * Stateless Scan does not support ```detect.policy.check.fail.on.severities```
    * [solution_name] will fail with FAILURE_POLICY_VIOLATION if any component violates Black Duck polices with a CRITICAL or BLOCKER severity. 
    * See the Black Duck documentation for a list of policy conditions that are supported by Stateless Scan. 
 * Stateless Scan does not support ```detect.policy.check.fail.on.names```
 * Stateless Scan cannot create a Risk or Notices report.
 * Stateless Scan will not create a Project or Version on Black Duck.
 * Stateless Scan when running SIGNATURE_SCAN, BINARY_SCAN, or CONTAINER_SCAN requires communication with Black Duck.
 * Stateless Scan and non-persistent SIGNATURE_SCAN, BINARY_SCAN, or CONTAINER_SCAN
    * To perform a non-persistent scan in Stateless mode, SIGNATURE_SCAN, BINARY_SCAN, or CONTAINER_SCAN must be included within --detect.tools.
    * Permitted tools omitted from the detect.tools list will not be run.

### Signature Scan Requirements
 * Must be running [blackduck_product_name] 2022.10.0 or greater using the hosted KB.
 
### Binary and Container Scan Requirements
 * Must be running [blackduck_product_name] 2023.4.0 or greater using the hosted KB.
 * It is necessary to have [solution_name] and [blackduck_product_name] running in the hosted SCAaaS environment to perform these scans. 
 * To run binary or container Stateless Scan a Black Duck Binary Analysis (BDBA) license is required.
 
## Invocation
 * To invoke a stateless signature scan only
    * --detect.tools=SIGNATURE_SCAN --detect.blackduck.scan.mode=STATELESS
 * To invoke a stateless package manager scan
    * --detect.tools=DETECTOR --detect.blackduck.scan.mode=STATELESS
    * --detect.tools=DETECTOR --detect.blackduck.scan.mode=RAPID
    * --detect.tools=BAZEL --detect.blackduck.scan.mode=RAPID
    * --detect.tools=BAZEL --detect.blackduck.scan.mode=STATELESS
    * --detect.tools=DOCKER --detect.blackduck.scan.mode=STATELESS
    * --detect.tools=DOCKER --detect.blackduck.scan.mode=RAPID
    * --detect.target.type=IMAGE --detect.blackduck.scan.mode=RAPID
    * --detect.target.type=IMAGE --detect.blackduck.scan.mode=STATELESS
 * To invoke a combined a stateless scan (non-exhaustive list):
    * --detect.tools=DETECTOR,SIGNATURE_SCAN --detect.blackduck.scan.mode=STATELESS
    * --detect.tools=DETECTOR,SIGNATURE_SCAN,DOCKER --detect.blackduck.scan.mode=STATELESS
    * --detect.tools=BAZEL,SIGNATURE_SCAN --detect.blackduck.scan.mode=STATELESS
    * --detect.tools=DETECTOR,DOCKER --detect.blackduck.scan.mode=RAPID
 * To invoke a stateless binary scan
    * --detect.tools=BINARY_SCAN --detect.blackduck.scan.mode=STATELESS --detect.scaaas.scan.path=file:///foo/bar.exe
 * To invoke a stateless container scan
    * --detect.tools=CONTAINER_SCAN --detect.blackduck.scan.mode=STATELESS --detect.scaaas.scan.path=file:///foo/docker-image.tar

## Results

Unlike persistent scans, no data is stored on Black Duck and all scans are done transiently. These scans are primarily intended to be fast, although they can take some time as communication with Black Duck is a requirement.

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
For [solution_name] version 8.7.0 and later, with [blackduck_product_name] 2023.1.2, Rapid Scan output now reports upgrade guidance for transitive dependencies with known vulnerabilities. The output gives information as to the direct dependency upgrade options and the transitive dependencies affected. This output is given in the results section which appears near the end of the [solution_name] run and appears as follows:
```
2023-03-09 13:01:56 EST INFO  [main] --- ===== Transitive Guidance =====
2023-03-09 13:01:56 EST INFO  [main] --- 
2023-03-09 13:01:56 EST INFO  [main] ---        Transitive upgrade guidance:
2023-03-09 13:01:56 EST INFO  [main] ---            Upgrade component com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.13.2 to version 2.14.2 in order to upgrade transitive components com.fasterxml.jackson.core:jackson-databind:2.13.2, org.yaml:snakeyaml:1.29, com.fasterxml.jackson.core:jackson-databind:2.13.2.2, org.yaml:snakeyaml:1.30
2023-03-09 13:01:56 EST INFO  [main] ---            Upgrade component org.apache.httpcomponents:httpclient-osgi:4.5.13 to version 4.5.14 in order to upgrade transitive component commons-codec:commons-codec:1.11
2023-03-09 13:01:56 EST INFO  [main] ---            Upgrade component org.apache.pdfbox:pdfbox:2.0.25 to version 2.0.27 in order to upgrade transitive component org.apache.pdfbox:fontbox:2.0.25
2023-03-09 13:01:56 EST INFO  [main] ---            Upgrade component org.springframework.boot:spring-boot:2.6.6 to versions (Short Term) 2.7.9, (Long Term) 3.0.4 in order to upgrade transitive components org.springframework:spring-beans:5.3.18, org.springframework:spring-expression:5.3.18, org.springframework:spring-aop:5.3.18, org.springframework:spring-context:5.3.18
2023-03-09 13:01:56 EST INFO  [main] --- 
2023-03-09 13:01:56 EST INFO  [main] --- ======== Detect Status ========
2023-03-09 13:01:56 EST INFO  [main] --- 
2023-03-09 13:01:56 EST INFO  [main] --- GIT: SUCCESS
2023-03-09 13:01:56 EST INFO  [main] --- GRADLE: SUCCESS
2023-03-09 13:01:56 EST INFO  [main] --- Overall Status: SUCCESS - Detect exited successfully.
2023-03-09 13:01:56 EST INFO  [main] --- 
2023-03-09 13:01:56 EST INFO  [main] --- ===============================
```

For further remediation and transitive dependency upgrade guidance, please consult the documentation provided by [blackduck_product_name] under the topic:
<xref href="RiskGuidance.dita" scope="peer">Getting remediation guidance for components with security vulnerabilities.
<data name="facets" value="pubname=bd-hub"/>

## Stateless Scan Compare Mode

You can configure Package Manager and Signature Stateless Scans to return only the difference in policy violations between the current scan and previous Persistent Scans using the same configuration. To return only the difference in policy violations, configure detect.blackduck.rapid.compare.mode to BOM_COMPARE or BOM_COMPARE_STRICT.

Setting the compare mode to ALL evaluates all RAPID or FULL policies. BOM_COMPARE_STRICT only shows policy violations not present in an existing project version BOM. BOM_COMPARE depends on the type of policy rule modes and behaves like ALL if the policy rule is only RAPID and like BOM_COMPARE_STRICT when the policy rule is RAPID and FULL. See the Black Duck documentation for complete details.
