# Rapid Scan

Rapid Scan or Rapid Scan Mode is a new way of running ${solution_name} with Black Duck. This mode is designed to be as fast as possible and does not persist any data on Black Duck.

It is enabled by adding [--detect.blackduck.scan.mode=RAPID](../properties/configuration/blackduck%20server/#detect-scan-mode-advanced) to a run of detect.

# Overview

Unlike persistent scans, no data is stored on Black Duck and all scans are done transiently. These scans are primarily intended to be fast.

Because Rapid Mode is an entirely different way to run ${solution_name} many of the features normally available to persistent scans cannot be used, see Restrictions for details on what is available.

The results are saved to a json file named 'name_version_BlackDuck_DeveloperMode_Result.json' in the Scan Output directory, where name and version are the project's name and version.

The results are also printed in the logs:
```
2021-06-18 11:38:18 EDT INFO  [main] --- Rapid Scan Result: (for more detail look in the log for Rapid Scan Result Details)
2021-06-18 11:38:18 EDT INFO  [main] ---
2021-06-18 11:38:18 EDT INFO  [main] --- 		Policy Errors = 0
2021-06-18 11:38:18 EDT INFO  [main] --- 		Policy Warnings = 99
2021-06-18 11:38:18 EDT INFO  [main] --- 		Security Errors = 7
2021-06-18 11:38:18 EDT INFO  [main] --- 		Security Warnings = 0
2021-06-18 11:38:18 EDT INFO  [main] --- 		License Errors = 0
2021-06-18 11:38:18 EDT INFO  [main] --- 		License Warnings = 0
2021-06-18 11:38:18 EDT INFO  [main] ---
2021-06-18 11:38:18 EDT INFO  [main] --- 		Policies Violated:
2021-06-18 11:38:18 EDT INFO  [main] --- 			Security Vulnerabilities Great Than Or Equal to High
2021-06-18 11:38:18 EDT INFO  [main] --- 			Warn on Low Security Vulnerabilities
2021-06-18 11:38:18 EDT INFO  [main] --- 			Warn on Medium Security Vulnerabilities
2021-06-18 11:38:18 EDT INFO  [main] ---
2021-06-18 11:38:18 EDT INFO  [main] --- 		Components with Policy Violations:
2021-06-18 11:38:18 EDT INFO  [main] --- 			Apache PDFBox 2.0.12 (maven:org.apache.pdfbox:pdfbox:2.0.12)
2021-06-18 11:38:18 EDT INFO  [main] --- 			node-ini 1.3.5 (npmjs:ini/1.3.5)
2021-06-18 11:38:18 EDT INFO  [main] ---
2021-06-18 11:38:18 EDT INFO  [main] --- 		Components with Policy Violation Warnings:
2021-06-18 11:38:18 EDT INFO  [main] --- 			Acorn 5.5.3 (npmjs:acorn/5.5.3)
2021-06-18 11:38:18 EDT INFO  [main] --- 			Apache ActiveMQ 5.15.9 (maven:org.apache.activemq:activemq-client:5.15.9)
```

# Restrictions

When Rapid Scan is enabled, ${solution_name} will only run certain tools to ensure results are calculated as fast as possible.

The currently supported tools are: DETECTOR

All other tools are disabled when running in Rapid Scan mode.

Rapid mode should be run with aggregation so server-side metrics can be properly collected.

Rapid Scan requires Black Duck policies. Rapid Scan only reports components that violates policies. If no policies are violated or there are no defined policies, then no components are returned.

Detect will fail with FAILURE_POLICY_VIOLATION if any component violate polices with a CRITICAL or BLOCKER severity.

See Black Duck documentation for a list of policy conditions that are supported by Rapid Scan.

The Detect property detect.policy.check.fail.on.severities does not apply to Rapid Scanning.

Detect does not create a Project or Version on Black Duck in Rapid Mode.

Detect cannot create a Risk or Notices report for Rapid Scans.

Rapid Scan requires bdio2 and will not run if bdio2 is not enabled.