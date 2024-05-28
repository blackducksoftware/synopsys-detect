# Choosing a run method (script, .jar, or Docker container)

There are three ways to run [company_name] [solution_name]:

1. Download and run a [company_name] [solution_name] [script](runningscript.md).
1. Download and run a [company_name] [solution_name] [.jar file](runningjar.md).
1. Run [company_name] [solution_name] from [within a Docker container](../runincontainer.md).

The primary reason to run one of the [company_name] [solution_name] scripts is that the scripts have an auto-update feature.
By default, they always
run the latest version of the [company_name] [solution_name] .jar file within a specific major version; downloading it for you if necessary.
When you run [company_name] [solution_name] via one of the provided scripts, you automatically pick up fixes and new features as they are released.
Each script limits itself to a specific [company_name] [solution_name] major version (for example, 7.y.z, or 6.y.z), unless you override
this default behavior.

| [company_name] [solution_name] version | Script Type | Script Name |
|---| --- |-------------|
| 9 | Bash | detect9.sh  |
| 9 | PowerShell | detect9.ps1 |
| 8 | Bash | detect8.sh  |
| 8 | PowerShell | detect8.ps1 |

Instuctions and examples in this documentation that reference the scripts assume you are running
[company_name] [solution_name] 9, so refer to detect9.sh or detect9.ps1. To run [company_name] [solution_name] 8 instead,
substitute detect8.sh for detect9.sh, or detect8.ps1 for detect9.ps1.

The primary reason to run the [company_name] [solution_name] .jar directly is that this method provides
direct control over the exact [company_name] [solution_name] version;
[company_name] [solution_name] does not automatically update in this scenario.

The primary reason to run [company_name] [solution_name] from within a Docker container is to take advantage of the benefits of Docker containers, which include standardized run environment configuration;
[company_name] [solution_name] does not automatically update in this scenario.
