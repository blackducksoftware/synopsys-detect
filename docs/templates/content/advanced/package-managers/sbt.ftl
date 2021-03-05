# SBT support

${solution_name} runs the dependecyDot task when it finds the following in your project

* build.sbt

The SBT detector only requires a compatible dependency graph plugin. Specifically it requires that the  dependencyDot task be available and runs.

The [dependency graph plugin](https://classic.yarnpkg.com/en/docs/workspaces/) ${solution_name} uses can be installed [globally](https://www.scala-sbt.org/1.x/docs/Using-Plugins.html) or per project.

To install the plugin globally add the following to "$HOME/.sbt/1.0/plugins/build.sbt"
```
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.10.0-RC1")
```
To install the plugin to a single project, add the same line to "/project/plugins.sbt" where project is a folder alongside the build.sbt.

You can verify the plugin is installed by running "sbt plugins" and verifying "" appears in the output. ${solution_name} will perform this check when SBT applies.
```
net.virtualvoid.sbt.graph.DependencyGraphPlugin
```

The SBT detector runs the "dependencyDot" task which generates "target/configuration-dependencies.dot" for each project. It creates a code location for each of these files.
If the dot files are not generated in target, the incorrect project folder may be found. This doesn't affect results but could affect the project and version chosen.

**NOTE: Older SBT projects that generate a resolution cache are still supported but are being deprecated. You must install the plugin for SBT to continue working uninterrupted.