# Including and Excluding Tools, Detectors, Directories, etc.

[Properties](../properties/all-properties.md) provide a variety of additional options for configuring ${solution_name} behavior. One of the
most fundamental ways to modify ${solution_name} is by including and excluding [tools](../components/tools.md) and [detectors](../components/detectors.md).

## Tools

By default, all tools are eligible to run; the set of tools that actually run
depends on the properties you set.
To limit the eligible tools to a given list, use:

--detect.tools={comma-separated list of tool names, all uppercase}

To exclude specific tools, use:

````
--detect.tools.excluded={comma-separated list of tool names, all uppercase}
````

Exclusions take precedence over inclusions.

Refer to [Tools](../components/tools.md) for the list of tool names.

Refer to [Properties](../properties/all-properties.md) for details.

## Detectors

By default, all detectors are eligible to run.  The set of detectors that actually
run depends on the files existing in your project directory.
To limit the eligible detectors to a given list, use:

````
--detect.included.detector.types={comma-separated list of detector names}
````

To exclude specific detectors, use:

````
--detect.excluded.detector.types={comma-separated list of detector names}
````

Exclusions take precedence over inclusions.

Refer to [Detectors](../components/detectors.md) for the list of detector names.

Refer to [Properties](../properties/all-properties.md) for details.

## Package Manager Exclusions

If you wish to specify package manager-specific exclusions you may do so using the following properties:

* [detect.gradle.included.configurations](../properties/detectors/gradle.md#gradle-include-configurations-advanced)
* [detect.gradle.excluded.configurations](../properties/detectors/gradle.md#gradle-exclude-configurations-advanced)
* [detect.gradle.included.projects](../properties/detectors/gradle.md#gradle-include-projects-advanced)
* [detect.gradle.excluded.projects](../properties/detectors/gradle.md#gradle-exclude-projects-advanced)
* [detect.lerna.included.packages](../properties/detectors/lerna.md#lerna-packages-included-advanced)
* [detect.lerna.excluded.packages](../properties/detectors/lerna.md#lerna-packages-excluded-advanced)
* [detect.maven.included.scopes](../properties/detectors/maven.md#dependency-scope-included)
* [detect.maven.excluded.scopes](../properties/detectors/maven.md#dependency-scope-excluded)
* [detect.maven.included.modules](../properties/detectors/maven.md#maven-modules-included-advanced)
* [detect.maven.excluded.modules](../properties/detectors/maven.md#maven-modules-excluded-advanced)
* [detect.nuget.included.modules](../properties/detectors/nuget.md#nuget-modules-included-advanced)
* [detect.nuget.excluded.modules](../properties/detectors/nuget.md#nuget-projects-excluded-advanced)
* [detect.sbt.included.configurations](../properties/detectors/sbt.md#sbt-configurations-included-deprecated)
* [detect.sbt.excluded.configurations](../properties/detectors/sbt.md#sbt-configurations-excluded-deprecated)

## Directory Exclusions

Use [detect.excluded.directories](../properties/configuration/paths.md#detect-excluded-directories-advanced) to exclude directories from search when looking for detectors, and when finding paths to pass to the signature scanner as values for an '--exclude' flag.

### Exclude directories by name

This property accepts explicit directory names, as well as globbing-style wildcard patterns. See [here](../configuring/propertywildcards.md) for more info.

Examples

| Value | Excluded | Not Excluded |
| --- | --- | --- |
|`foo` | /projectRoot/foo | /projectRoot/foobar
| `*bar` | /projectRoot/bar & /projectRoot/foobar | |

### Exclude directories by path

This property accepts explicit paths relative to the project's root, or you may specify glob-style patterns.

Examples

| Value | Excluded | Not Excluded |
| --- | --- | --- |
| `foo/bar` | /projectRoot/foo/bar | /projectRoot/dir/foo/bar |
| `**/foo/bar` | /projectRoot/dir/foo/bar & /projectRoot/directory/foo/bar | |
| `/projectRoot/d*/*` | /projectRoot/dir/foo & /projectRoot/directory/bar | |

When specifying path patterns:

* Use '*' to match 0 or more directory name characters (will not cross directory boundaries).
* Use '**' to match 0 or more directory path characters (will cross directory boundaries).

${solution_name} uses FileSystem::getPatchMatcher and its glob syntax implementation to exclude path patterns. See [here](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)) for more info.

### Related properties:

* [detect.excluded.directories.defaults.disabled](../properties/configuration/paths.md#detect-excluded-directories-defaults-disabled-advanced)
* [detect.excluded.directories.search.depth](../properties/configuration/signature-scanner.md#detect-excluded-directories-search-depth)
