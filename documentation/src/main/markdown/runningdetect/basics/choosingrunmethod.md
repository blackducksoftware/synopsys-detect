# Choosing a run method (script, .jar, or Docker container)

There are three ways to run [detect_product_long]:

1. Download and run a [detect_product_short] [script](runningscript.md).
1. Download and run a [detect_product_short] [.jar file](runningjar.md).
1. Run [detect_product_short] from [within a Docker container](../runincontainer.md).

The primary reason to run one of the [detect_product_short] scripts is that the scripts have an auto-update feature.
By default, they always
run the latest version of the [detect_product_short] .jar file within a specific major version; downloading it for you if necessary.
When you run [detect_product_short] via one of the provided scripts, you automatically pick up fixes and new features as they are released.
Each script limits itself to a specific [detect_product_short] major version (for example, 9.y.z, or 8.y.z), unless you override
this default behavior.

| [detect_product_short] version | Script Type | Script Name |
|---| --- |-------------|
| 9 | Bash | detect9.sh  |
| 9 | PowerShell | detect9.ps1 |
| 8 | Bash | detect8.sh  |
| 8 | PowerShell | detect8.ps1 |

Instuctions and examples in this documentation that reference the scripts assume you are running
[detect_product_short] 9, so refer to detect9.sh or detect9.ps1. To run [detect_product_shorte] 8 instead,
substitute detect8.sh for detect9.sh, or detect8.ps1 for detect9.ps1.

The primary reason to run the [detect_product_short] .jar directly is that this method provides
direct control over the exact [detect_product_short] version;
[detect_product_short] does not automatically update in this scenario.

The primary reason to run [detect_product_short] from within a Docker container is to take advantage of the benefits of Docker containers, which include standardized run environment configuration;
[detect_product_short] does not automatically update in this scenario.
