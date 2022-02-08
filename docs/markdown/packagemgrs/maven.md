# Maven support

[solution_name] has two detectors for Maven:

* Maven Pom detector
* Maven Parse detector

## Maven Pom detector

The Maven Pom detector discovers dependencies of Maven projects.

The Maven Pom detector attempts to run on your project if it finds a pom.xml file in the source directory (top level).

The Maven Pom detector also requires either mvnw or mvn:

1. [solution_name] looks for mvnw in the source directory (top level). You can override this by setting the Maven path property. If not overridden and not found:
1. [solution_name] looks for mvn on $PATH.

The Maven Pom detector runs `mvn dependency:tree` to get a list of the project's dependencies and then parses the output.
[solution_name] assumes the output of this command will be in Maven's default logging format. Customizations of Maven's logging format can break [solution_name]'s parsing.

Scope inclusion/exclusion is performed during the parsing of the output of the `mvn dependency:tree` command.

### Components with unknown graph locations

The output of `mvn dependency:tree` does not always provide information
on a component's positions in the dependency graph. When this information is missing,
[solution_name] will place the component under a placeholder "component" named *Additional_Components*.

For example: Imagine a project with two scopes (compile and test), two direct dependencies A and B,
both of which depend transitively on C, and imagine we want [solution_name] to exclude test scope from
its output. The actual dependency graphs for this project look like:
````
compile scope:
A
\- C

test scope:
B
\- C
````

For this project, the output of `mvn dependency:tree` may only show:
````
compile scope:
A

test scope:
B
\- C (compile)
````
From that output we can tell that C is part of the compile scope, but there is no information about where in the compile scope
graph C belongs. Its position in the test scope is irrelevant since test scope is being excluded. Rather than excluding C in this case,
[solution_name] puts it under the placeholder "component" named *Additional_Components*.

## Maven Parse detector

The Maven buildless parser has two different methods of finding dependencies.

The original implementation, now called legacy mode, is a simple parse of the pom for dependency information and supports finding maven plugins.

The new implementation, which uses Project Inspector, is more accurate but currently does not support plugins.

In 7.6.0, legacy mode is the default. You can enable the new mode by setting --detect.maven.buildless.legacy.mode=false.

In 8.0.0, support for legacy mode will be dropped, as will support for finding plugins. Though Project Inspector support for plugins will likely be added in the future.

In 7.6.0, when maven legacy mode is active, plugins can be enabled by setting --detect.maven.include.plugins=true.