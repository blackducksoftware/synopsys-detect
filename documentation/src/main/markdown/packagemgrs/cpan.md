# CPAN Support

## Related properties

[Detector properties](../properties/detectors/cpan.md)

## Overview

The CPAN detectable will run if it finds a Makefile.PL file.

The detectable requires the following executables:

* cpan - used to determine the list of direct dependencies required by the project
* cpanm - used to determine the list of installed Perl modules on the system in order to assign versions to the dependencies found by cpan

When executing the cpan command, [solution_name] will set the PERL_MM_USE_DEFAULT environment variable to true. This ensures that if the cpan has not been configured on the system before, default configuration settings will be accepted.

The CPAN detectable reports only direct dependencies and not transitive ones.
