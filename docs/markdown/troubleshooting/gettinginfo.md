# Getting information

## Simple issues

- Run [solution_name] with `--logging.level.detect=DEBUG` (the default logging level, INFO, is insufficient for troubleshooting) and read through the entire log for clues.
- For additional detail, run with `--logging.level.detect=TRACE`.
- [solution_name] typically runs package manager commands or build tool commands similar to commands used in your build.
When run by [solution_name], those commands (as well as the environment
in which they run) need to be consistent with your build, and it's important to verify that they are.
For example, the Gradle detector defaults to running
*./gradlew dependencies* if it finds the file ./gradlew. If your build runs a different Gradle command or wrapper
(say, /usr/local/bin/gradle), use property
*detect.gradle.path* to tell [solution_name] to run the same Gradle command that your build runs.
Check a DEBUG log for the package manager commands that [solution_name] is running, and compare
them to the commands your build runs.

## More complex issues

- For a diagnostiz zip containing files needed for troubleshooting: Run [solution_name] with `--detect.diagnostic.extended=true`. This will generate a diagnostic zip that contains many useful output files, intermediate files, and logs, including the generated BDIO files and [solution_name] and [blackduck_signature_scanner_name] logs. Review every file in this diagnostic zip for important clues. If you contact Synopsys Technical Support for help, you will need to provide this file.

