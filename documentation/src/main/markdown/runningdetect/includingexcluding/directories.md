# Directory Exclusions

Use [detect.excluded.directories](../../properties/configuration/paths.md#detect-excluded-directories-advanced) to exclude directories from search when looking for detectors, searching for files to binary scan when using property detect.binary.scan.file.name.patterns, and when finding paths to pass to the signature scanner as values for an '--exclude' flag.

## Exclude directories by name

This property accepts explicit directory names, as well as globbing-style wildcard patterns. See [configuring property wildcards](../../configuring/propertywildcards.md) for more info.

Examples

| Value | Excluded | Not Excluded |
| --- | --- | --- |
|`foo` | /projectRoot/foo | /projectRoot/foobar
| `*bar` | /projectRoot/bar & /projectRoot/foobar | |

## Exclude directories by path

This property accepts explicit paths relative to the project's root, or you may specify glob-style patterns.


When specifying path patterns:

* Use '*' to match 0 or more directory name characters (will not cross directory boundaries).
* Use '**' to match 0 or more directory path characters (will cross directory boundaries).

Examples

| Value | Excluded | Not Excluded |
| --- | --- | --- |
| `foo/bar` | /projectRoot/foo/bar | /projectRoot/dir/foo/bar |
| `**/foo/bar` | /projectRoot/dir/foo/bar & /projectRoot/directory/foo/bar | |
| `/projectRoot/d*/*` | /projectRoot/dir/foo & /projectRoot/directory/bar | |

[solution_name] uses FileSystem::getPatchMatcher and its glob syntax implementation to exclude path patterns. See [here](https://docs.oracle.com/en/java/javase/11/docs/api/java.base/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)) for more info.

### Wildcards in relative path patterns

When excluding paths, if you want to use wildcards in an exclusion pattern for a relative path, there are some confusing rules.

Name-wildcards ('*'), unless appearing in a pattern that begins with path-wildcards ('**'), will only work if the pattern refers to one-level below the source path root.  

Let's say you want to exclude /project/folder while scanning /project:

| Value | Excluded | Not Excluded |
| --- | --- | --- |
| `*older` | /project/folder |  |
| `f*` | /project/folder |  |
| `folder/*` |  | /project/folder or /project/folder/dir |
| `**folder/*` | /project/folder/dir | /project/folder |
| `*older/*` |  | /project/folder or /project/folder/dir |
| `**/*older/*` | /project/folder/dir | /project/folder |

## Related properties:

* [detect.excluded.directories.defaults.disabled](../../properties/configuration/paths.md#detect-excluded-directories-defaults-disabled-advanced)
* [detect.excluded.directories.search.depth](../../properties/configuration/signature-scanner.md#detect-excluded-directories-search-depth)
* [detect.binary.scan.file.name.patterns](../../properties/configuration/binary-scanner.md#binary-scan-filename-patterns)
