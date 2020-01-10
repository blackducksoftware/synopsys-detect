# Property wildcard support

The values of the following ${solution_name} properties can utilize the wildcards described below:

* detect.maven.included.scopes
* detect.maven.excluded.scopes
* detect.maven.included.modules
* detect.maven.excluded.modules
* detect.sbt.included.configurations
* detect.sbt.excluded.configurations
* detect.tools
* detect.tools.excluded
* detect.included.detector.types
* detect.excluded.detector.types
* detect.gradle.included.configurations
* detect.gradle.excluded.configurations
* detect.gradle.included.projects
* detect.gradle.excluded.projects

The supported wildcards and their effect are:

* An asterisk (*) matches any sequence of zero or more characters
* A question mark (?) matches any single character

Wildcard evaluation in these values is similar to Linux command line file globbing, and different from regular expression matching.