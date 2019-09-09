# ${groupName}

Detect uses "Detectors" to denote different supported package managers. Each package manager has a detector that extract dependencies if it is found to apply to your project.

Detectors first check if they "apply" to your project by looking for specific triggers such as present files or specified configuration properties.

Detectors then check that your environment is "extractable", meaning you have all the relevant executables (such as npm or a gradle wrapper) and all the relevant downloads such as inspectors are present or available.

Inspectors are used by detectors when the package manager requires an integration or embedded plugin to work. For example, Gradle uses an inspector as a plugin that executes it's own task. Most detectors do not require an inspector.

Finally, Detectors perform their "extraction" to find your dependencies. This may require but is not limited to: running executables, performing builds, parsing files and talking to web services.

<#list detectors as detector>
|Detetor||
|---|---|
|Strict|${option.strictValues?then("Yes", "No")}|
</#list>
