# SBT support

## Related properties

[Detector properties](../properties/detectors/sbt.md)

## Overview

[solution_name] runs the dependecyDot task when it finds the following in your project

* build.sbt

The SBT detector requires a compatible dependency graph plugin and the "depedencyDot" task to generate dependency graphs for SBT projects. The plugin generates dot file graphs for each project and [solution_name] parses each dot graph into it's own code location.

Starting with SBT 1.4.0, a dependency graph plugin comes with SBT and can be enabled by adding ```addDependencyTreePlugin``` to "project/plugins.sbt".

For older versions of SBT, a [dependency graph plugin](https://github.com/sbt/sbt-dependency-graph) can be installed [globally](https://www.scala-sbt.org/1.x/docs/Using-Plugins.html) or per project.

To install the plugin globally add the following to "$HOME/.sbt/1.0/plugins/build.sbt" where "1.0" is replaced by your SBT version.
```
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")
```

To install the plugin to a single project, add the same line to "src/project/plugins.sbt" where src is your project's source directory.

[solution_name] verfies the plugin is installed by running "sbt plugins" and looking for any of the following plugin names in the output. You can also perform this check yourself by running the same command.
```
sbt.plugins.DependencyTreePlugin
net.virtualvoid.sbt.graph.DependencyGraphPlugin
```

The plugins include a "dependencyDot" task which generates "target/configuration-dependencies.dot" for each project.
In some cases, the dot files are not generated in "./target" and while those graphs will still be found and results are not affected, it could affect the project and version chosen and code location's directory.

**NOTE: Older SBT projects that generate a resolution cache are still supported but are being deprecated. You must install the plugin for SBT to continue working uninterrupted.

As of 8.8.0 of [solution_name], evicted dependencies in Simple Build Tool(SBT) projects will not be included in the Bill of Materials(BoM) generated during the scan.

## Background execution

The sbt command line utility is known to suspend when run in the background (this may be limited to Linux and Mac systems):
[https://github.com/sbt/sbt/issues/701](https://github.com/sbt/sbt/issues/701).
This can cause [solution_name] to suspend, if [solution_name] is run in the background,
and the SBT detector runs.
You can apply the workaround suggested in that github issue using the
*detect.sbt.arguments* property:
```
./detect8.sh --detect.sbt.arguments="-Djline.terminal=jline.UnsupportedTerminal"
```
