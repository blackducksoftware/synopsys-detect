# Rapid Scan

Rapid Scan or Rapid Scan Mode is a new way of running ${solution_name} with Black Duck. This mode is designed to be as fast as possible and does not persist any data on Black Duck.

It is enabled by adding [--detect.blackduck.scan.mode=RAPID](../properties/configuration/blackduck%20server/#detect-scan-mode-advanced) to a run of detect.

The rapid scan can be configured by providing a file named '.bd-rapid-scan.yaml' in the source directory. ${solution_name} will automatically upload the config file during a rapid scan.

# Overview

Unlike persistent scans, no data is stored on Black Duck and all scans are done transiently. These scans are primarily intended to be fast.

Because Rapid Mode is an entirely different way to run ${solution_name} many of the features normally available to persistent scans cannot be used, see Restrictions for details on what is available.

The results are saved to a json file named 'name_version_BlackDuck_DeveloperMode_Result.json' in the Scan Output directory, where name and version are the project's name and version.

The results are also printed in the logs:
```
2021-07-20 13:25:18 EDT INFO  [main] --- Rapid Scan Result: (for more detail look in the log for Rapid Scan Result Details)
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

# Restrictions

When Rapid Scan is enabled, ${solution_name} will only run certain tools to ensure results are calculated as fast as possible.

The currently supported tools are: DETECTOR and DOCKER.

All other tools are disabled when running in Rapid Scan mode.

Rapid mode should be run with aggregation so server-side metrics can be properly collected.

Rapid Scan requires Black Duck policies. Rapid Scan only reports components that violates policies. If no policies are violated or there are no defined policies, then no components are returned.

Detect will fail with FAILURE_POLICY_VIOLATION if any component violate polices with a CRITICAL or BLOCKER severity.

See Black Duck documentation for a list of policy conditions that are supported by Rapid Scan.

The Detect property detect.policy.check.fail.on.severities does not apply to Rapid Scanning.

Detect does not create a Project or Version on Black Duck in Rapid Mode.

Detect cannot create a Risk or Notices report for Rapid Scans.

Rapid Scan requires bdio2 and will not run if bdio2 is not enabled.