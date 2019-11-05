# Maven support

Detect has two detectors for Maven:

* [Maven Pom detector](#mavenpomdetector)
* [Maven Parse detector](#mavenparsedetector)

<a name="mavenpomdetector"></a>
# Maven Pom detector

The Maven Pom detector can discover dependencies of Maven projects.

The Maven Pom detector will attempt to run on your project if it finds a pom.xml file in the source directory (top level).

The Maven Pom detector also requires either mvnw or mvn:

1. Detect looks for mvnw in the source directory (top level). You can override this by setting the maven path property. If not overridden and not found:
1. Detect looks for mvn on $PATH.

The Maven Pom detector runs `mvn dependency:tree` to get a list of the project's dependencies, and then parses the output.

<a name="mavenparsedetector"></a>
# Maven Parse detector

TBD