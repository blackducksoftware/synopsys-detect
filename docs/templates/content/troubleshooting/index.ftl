# Troubleshooting ${solution_name}

## Getting information

- Run ${solution_name} with `--logging.level.com.synopsys.integration=DEBUG` (the default logging level, INFO, is insufficient for troubleshooting) and read through the entire log for clues.
- ${solution_name} typically runs package manager commands or build tool commands similar to commands used in your build.
When run by ${solution_name}, those commands (as well as the environment
in which they run) need to be consistent with your build, and it's important to verify that they are.
For example, the Gradle detector defaults to running
*./gradlew dependencies* if it finds the file ./gradlew. If your build runs a different Gradle command or wrapper
(say, /usr/local/bin/gradle), use property
*detect.gradle.path* to tell ${solution_name} to run the same Gradle command that your build runs.
Check a DEBUG log for the package manager commands that ${solution_name} is running, and compare
them to the commands your build runs.
- For more troubleshooting information: Run ${solution_name} with `--detect.diagnostic=true`. This will generate a diagnostic zip that contains many useful intermediate files and logs, including the generated BDIO (.jsonld) files and ${blackduck_signature_scanner_name} logs.
- For even more troubleshooting information: Run ${solution_name} with `--detect.diagnostic.extended=true`. This will generate an extended diagnostic zip that will also include lock files and build artifacts when appropriate.

## Common problems

- See if you can reproduce the problem using the latest version of ${solution_name} with the latest version of ${blackduck_product_name}. If not, the problem may be either fixed, or due to incompatible ${solution_name} / ${blackduck_product_name} versions.
- Remember to consider the possibility that the ${blackduck_product_name} user lacks the necessary permissions (to create the project, update the BOM, receive notifications, etc.) in ${blackduck_product_name}. For more information, see [${blackduck_product_name} user role requirements](../../BD-user-role-requirements/).
- Remember to consider the possibility that the ${blackduck_product_name} server (registration key) may not have required capabilities enabled (binary upload, snippet scanning, etc.).

## Incorrect or missing components

- For issues related to tools invoked by ${solution_name} (${blackduck_signature_scanner_name}, Docker Inspector, etc.), please check that tool's documentation.
- For issues related to incorrect components in the ${blackduck_product_name} BOM: ${solution_name} has a great deal of control over matches produced by detectors (that are written to BDIO/.jsonld files), but no control over matches produced by the ${blackduck_signature_scanner_name}. When investigating an incorrect component in a ${blackduck_product_name} BOM, you need to determine whether the component was contributed by a detector, or by the ${blackduck_signature_scanner_name}: On the ${blackduck_product_name} Components tab for the project/version: Click on the "N Matches" link next to the component. The next screen lists the matches on the right side. Matches from the ${blackduck_signature_scanner_name} have a filename in the Name column. Matches from detectors have an external ID (such as "org.hamcrest:hamcrest-core:1.3") in the Name column.
- For issues related to components missing from or or incorrectly categorized in the ${blackduck_product_name} BOM: ${solution_name} has a great deal of control over the production of .jsonld files (use [--detect.diagnostic](../../../properties/configuration/debug/#diagnostic-mode) to save these), but no control over how they are converted into a BOM by ${blackduck_product_name}. A good first step is to determine whether the .jsonld files produced are correct. If they are incorrect, the problem is related to what ${solution_name} is doing. If they are correct, but the BOM is incorrect, the problem is related to what ${blackduck_product_name} is doing. Similarly, ${solution_name} is responsible for passing the correct arguments to the ${blackduck_signature_scanner_name}, but has little control over the results it produces.

## Spring Boot related issues

- ${solution_name} is a Spring Boot application, and leverages Spring Boot to provide various mechanism to configure it through property settings: https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html. This flexibility comes with a risk: it's possible for ${solution_name} to be influenced by files (application.properties, application.xml) that may exist in the directory from which ${solution_name} is run that are intended for some other application. This can produce some strange results. If properties have unexpected values (see the ${solution_name} log), this is a possibility worth considering. The best solution may be simply to run ${solution_name} from a different (ideally empty) directory (use the --detect.source.path argument).
- Similarly, ${solution_name} can be influenced by environment variables via the same Spring Boot mechanism, so it's worth checking the environment for variables that correspond to ${solution_name} property names.

