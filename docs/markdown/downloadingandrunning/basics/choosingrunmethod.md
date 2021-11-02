# Choosing a run method (script, .jar, or Docker container)

There are three ways to run [solution_name]:

1. Download and run a [solution_name] script.
1. Download and run a [solution_name] .jar file.
1. Run [solution_name] from within a Docker container.

The primary reason to run one of the [solution_name] scripts is that the scripts have an auto-update feature.
By default, they always
run the latest version of the [solution_name] .jar file within a specific major version; downloading it for you if necessary.
When you run [solution_name] via one of the provided scripts, you automatically pick up fixes and new features as they are released.
Each script limits itself to a specific [solution_name] major version (for example, 7.y.z, or 6.y.z), unless you override
this default behavior.

| [solution_name] version | Script Type | Script Name |
| --- | --- | --- |
| 7 | Bash | detect7.sh |
| 7 | PowerShell | detect7.ps1 |
| 6 | Bash | detect.sh |
| 6 | PowerShell | detect.ps1 |

Instuctions and examples in this documentation that reference the scripts assume you are running
[solution_name] 7, so refer to detect7.sh or detect7.ps1. To run [solution_name] 6 instead,
simply substitute detect.sh for detect7.sh, or detect.ps1 for detect7.ps1.

The primary reason to run the [solution_name] .jar directly is that this method provides
direct control over the exact [solution_name] version;
[solution_name] does not automatically update in this scenario.

The primary reason to run [solution_name] from within a Docker container is to take advantage of the benefits of Docker containers, which include standardized run environment configuration;
[solution_name] does not automatically update in this scenario.
