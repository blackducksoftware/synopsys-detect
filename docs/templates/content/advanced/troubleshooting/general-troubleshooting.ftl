# Troubleshooting tips

## Troubleshooting best practices

1. Run ${solution_name} with `--logging.level.com.synopsys.integration=DEBUG` (the default logging level, INFO, is insufficient for troubleshooting) and read through the entire log for clues.
1. ${solution_name} typically runs package manager commands or build tool commands similar to commands used in your build.
When run by ${solution_name}, those commands (as well as the environment
in which they run) need to be consistent with your build, and it's important to verify that they are.
For example, the Gradle detector defaults to running
*./gradlew dependencies* if it finds the file ./gradlew. If your build runs a different Gradle command or wrapper
(say, /usr/local/bin/gradle), use property
*detect.gradle.path* to tell ${solution_name} to run the same Gradle command that your build runs.
Check a DEBUG log for the package manager commands that ${solution_name} is running, and compare
them to the commands your build runs.
1. For more troubleshooting information: Run ${solution_name} with `--detect.diagnostic=true`. This will generate a diagnostic zip that contains many useful intermediate files and logs, including the generated BDIO (.jsonld) files and ${blackduck_signature_scanner_name} logs.
1. For even more troubleshooting information: Run ${solution_name} with `--detect.diagnostic.extended=true`. This will generate an extended diagnostic zip that will also include lock files and build artifacts when appropriate.
1. See if you can reproduce the problem using the latest version of ${solution_name} with the latest version of ${blackduck_product_name}. If not, the problem may be either fixed, or due to incompatible ${solution_name} / ${blackduck_product_name} versions.
1. Remember to consider the possibility that the ${blackduck_product_name} user lacks the necessary permissions (to create the project, update the BOM, receive notifications, etc.) in ${blackduck_product_name}. For more information, see [${blackduck_product_name} user role requirements](../../BD-user-role-requirements/).
1. Remember to consider the possibility that the ${blackduck_product_name} server (registration key) may not have required capabilities enabled (binary upload, snippet scanning, etc.).
1. For issues related to tools invoked by ${solution_name} (${blackduck_signature_scanner_name}, Docker Inspector, etc.), please check that tool's documentation.
1. For issues related to incorrect components in the ${blackduck_product_name} BOM: ${solution_name} has a great deal of control over matches produced by detectors (that are written to BDIO/.jsonld files), but no control over matches produced by the ${blackduck_signature_scanner_name}. When investigating an incorrect component in a ${blackduck_product_name} BOM, you need to determine whether the component was contributed by a detector, or by the ${blackduck_signature_scanner_name}: On the ${blackduck_product_name} Components tab for the project/version: Click on the "N Matches" link next to the component. The next screen lists the matches on the right side. Matches from the ${blackduck_signature_scanner_name} have a filename in the Name column. Matches from detectors have an external ID (such as "org.hamcrest:hamcrest-core:1.3") in the Name column.
1. For issues related to components missing from or or incorrectly categorized in the ${blackduck_product_name} BOM: ${solution_name} has a great deal of control over the production of .jsonld files (use [--detect.diagnostic](../../../properties/configuration/debug/#diagnostic-mode) to save these), but no control over how they are converted into a BOM by ${blackduck_product_name}. A good first step is to determine whether the .jsonld files produced are correct. If they are incorrect, the problem is related to what ${solution_name} is doing. If they are correct, but the BOM is incorrect, the problem is related to what ${blackduck_product_name} is doing. Similarly, ${solution_name} is responsible for passing the correct arguments to the ${blackduck_signature_scanner_name}, but has little control over the results it produces.
1. ${solution_name} is a Spring Boot application, and leverages Spring Boot to provide various mechanism to configure it through property settings: https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html. This flexibility comes with a risk: it's possible for ${solution_name} to be influenced by files (application.properties, application.xml) that may exist in the directory from which ${solution_name} is run that are intended for some other application. This can produce some strange results. If properties have unexpected values (see the ${solution_name} log), this is a possibility worth considering. The best solution may be simply to run ${solution_name} from a different (ideally empty) directory (use the --detect.source.path argument).
1. Similarly, ${solution_name} can be influenced by environment variables via the same Spring Boot mechanism, so it's worth checking the environment for variables that correspond to ${solution_name} property names.

## Diagnostic mode

${solution_name}'s diagnostic mode automatically enabled debug logging (where possible), saves all output (logs and inspector files) to a zip file, and notifies you of the location of the zip.

Diagnostic mode properties are:

-d --diagnostic [--detect.diagnostic](../../../properties/configuration/debug/#diagnostic-mode): When enabled, diagnostic mode collects all files generated by ${solution_name} and zips the files using a unique run ID. It includes logs, BDIO files, extraction files, and reports.

-de --diagnosticExtended [--detect.diagnostic.extended](../../../properties/configuration/debug/#diagnostic-mode-extended): When enabled, ${solution_name} collects relevant files such as lock files and build artifacts.

The zip file is created inside the ${solution_name} output directory, and must be provided when support issues are opened.

By default, diagnostic mode only includes files generated by ${solution_name} where extended diagnostics might include files it finds.

Synopsys does not recommend keeping diagnostic mode on, as it disables parts of cleanup such as deleting the generated diagnostic zip file(s); regardless of the ${solution_name} cleanup flag.
While diagnostic can be enabled through properties for consistency, it is generally preferable to use the command line flags to prevent accidentally keeping diagnostic mode on.
If diagnostic mode is accidentally enabled globally (such as using environment variables) the accumulation of diagnostic zips will likely eventually require user intervention as the zips will accumulate.

Diagnostic mode actions:

* Sets the output log level to DEBUG.

* Zips the entire run directory of ${solution_name} which includes:

* All BDIO files created by ${solution_name}.

* Any intermediary files generated by ${solution_name}, such as Gradle inspector output files.

* Includes any additional reports ${solution_name} might make such as dependency counts.

* Additionally extended diagnostics (-de or --detect.diagnostic.extended) may include additional relevant files such as:
    - Npm package locks.
    - vendor.config.
    - C/C++ compilation database.

After running a scan with diagnostic mode, results similar to the following display in your output.

```
2019-03-29 09:32:02 INFO  [main] --- Creating diagnostics zip.
2019-03-29 09:32:02 INFO  [main] --- Diagnostics zip location: C:\detect\blackduck\runs\detect-run-2019-03-29-13-31-50-492.zip
2019-03-29 09:32:03 INFO  [main] --- Diagnostics file created at: C:detect\blackduck\runs\detect-run-2019-03-29-13-31-50-492.zip
2019-03-29 09:32:03 INFO  [main] --- Diagnostic mode has completed.
```
