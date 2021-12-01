# Swift support
[solution_name] has two detectors for Swift:

* Xcode Swift detector
* Swift CLI detector

## Xcode Swift detector
The Xcode Swift detector discovers dependencies of Xcode projects utilizing [built-in tools within Xcode](https://developer.apple.com/documentation/swift_packages/adding_package_dependencies_to_your_app) for managing Swift dependencies.

The Xcode Swift detector applies to directories matching the `*.xcodeproj` filename pattern within bounds of the *detect.detector.search.depth* property.

Once the Xcode Swift detector finds a matching directory, it searches for a `Package.resolved` file inside your `.xcodeproj` directory at `[appName].xcodeproj/project.workspace/xcshareddata/swiftpm/Package.resolved`.

This detector does not require any executables to run.

### Running the Xcode Swift detector

Xcode projects are often found within Xcode workspaces to utilize a shared build environment.
Although Xcode projects within a workspace share a build environment, the project dependencies are still resolved within each Xcode project.

[solution_name] does not currently parse Xcode workspace files to determine project locations.
Instead [solution_name] will only search for directories matching the `*.xcodeproj` pattern. 

If there are Xcode projects not at the root of the scanned directory, you may have to set the *detect.detector.search.depth* property to a depth capable of reaching all projects.

### Package.resolved file

The `Package.resolved` is a JSON file containing a flat list of Swift packages required by the project.
This file can be empty if the project has no dependencies, in which case [solution_name] will create an empty code-location for the Xcode project.

Example `Package.resolved` file contents:
````
{
  "object": {
    "pins": [
      {
        "package": "swift-argument-parser",
        "repositoryURL": "https://github.com/apple/swift-argument-parser.git",
        "state": {
          "branch": null,
          "revision": "d2930e8fcf9c33162b9fcc1d522bc975e2d4179b",
          "version": "1.0.1"
        }
      }
    ]
  },
  "version": 1
}
````

### Extraction method
[solution_name] is capable of extracting component name and versions from the JSON content within the `Package.resolved` file.

#### Forge
Currently, all packages are assumed to come from GitHub. For support of additional public repositories, please contact the [solution_name] support team.

#### Component Name
The component name is derived from the **repositoryURL** field.
````
"repositoryURL": "https://github.com/apple/swift-argument-parser.git"
````
[solution_name] will parse the path of the url to remove the host and `.git` extensions.
In the above example, this produces a component name of `apple/swift-argument-parser`.

#### Component Version
[solution_name] will use the **state.version** field to identify the component version.
````
"state": {
    "branch": null,
    "revision": "d2930e8fcf9c33162b9fcc1d522bc975e2d4179b",
    "version": "1.0.1"
}
````
In the above example, this produces a component version of `1.0.1`.

## Swift CLI detector

The Swift CLI detector discovers dependencies of projects utilizing the Swift CLI.

The Swift CLI detector applies to directories containing a `Package.swift` file.

This detector requires a `swift` executable to run.

### Extraction method

[solution_name] uses the `swift package show-dependencies` command to create a dependency graph of Swift packages.

All packages of the root node are considered direct dependencies in the BlackDuck BOM.

Packages found with a version of `unspecified` will appear in the BOM without a version.