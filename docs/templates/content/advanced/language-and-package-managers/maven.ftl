# Maven support

${solution_name} has two detectors for Maven:

* Maven Pom detector
* Maven Parse detector

## Maven Pom detector

The Maven Pom detector discovers dependencies of Maven projects.

The Maven Pom detector attempts to run on your project if it finds a pom.xml file in the source directory (top level).

The Maven Pom detector also requires either mvnw or mvn:

1. ${solution_name} looks for mvnw in the source directory (top level). You can override this by setting the Maven path property. If not overridden and not found:
1. ${solution_name} looks for mvn on $PATH.

The Maven Pom detector runs `mvn dependency:tree` to get a list of the project's dependencies and then parses the output.

Scope inclusion/exclusion is performed during the parsing of the output of the `mvn dependency:tree` command.
When a component of an included scope is found under a component of an excluded scope, it is added
to the BOM under a top level placeholder component named *Additional_Components*.

## Maven Parse detector

TBD