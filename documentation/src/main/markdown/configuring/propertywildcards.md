# Property wildcard support

The values of the following [solution_name] properties can utilize filename globbing-style wildcards described below:

* detect.maven.included.scopes
* detect.maven.excluded.scopes
* detect.maven.included.modules
* detect.maven.excluded.modules
* detect.gradle.included.configurations
* detect.gradle.excluded.configurations
* detect.gradle.included.projects
* detect.gradle.excluded.projects
* detect.binary.scan.file.name.patterns
* detect.lerna.included.packages
* detect.lerna.excluded.packages
* detect.excluded.directories

The supported wildcards and their effect are:

* An asterisk (*) matches any sequence of zero or more characters
* A question mark (?) matches any single character

For example:

* *.jpg matches someimage.jpg, but not somedocument.doc
* *.??? matches someimage.jpg and somedocument.doc, but not somedocument.docx

Wildcard evaluation in these values is similar to Linux command line file globbing, and different from regular expression matching.

[solution_name] uses the
[Apache Commons IO FilenameUtils.wildcardMatch()](https://commons.apache.org/proper/commons-io/javadocs/api-release/org/apache/commons/io/FilenameUtils.html#wildcardMatch-java.lang.String-java.lang.String-) method to determine whether a string matches the given pattern.
