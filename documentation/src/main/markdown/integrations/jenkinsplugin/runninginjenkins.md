# Running Synopsys Detect in Jenkins

By default, [solution_name] for Jenkins downloads either the latest [solution_name] shell script when run on a UNIX node, or PowerShell script when it's run on a Windows node, to the Jenkins tools directory, and then executes that script. Note that you can also use the JAR option to run [solution_name].

The [solution_name] PowerShell or shell script is downloaded once and placed in the [solution_name] working directory. If you want to force the plugin to fetch the latest script, clear out the Detect directory in your Jenkins tools directory.

# **JAR option**
If you do not want to download [solution_name], you can manually put the JAR on the node where you want [solution_name] to run and specify the DETECT\_JAR environment variable that points to your provided JAR, and that JAR will be executed instead. 

To use the JAR option, perform the following steps:

1. Navigate to **Dashboard > Manage Jenkins > Configure System > Global properties > Environment variables**. 
1. Click **Add**.
1. Set an environment variable with the following properties:
   1. **Name**: `DETECT\_JAR`.
   1. **Value:** `<path to the Detect jar file on your Jenkins node>`.
   
**Note:** When your build runs, Jenkins looks for configured environment variables, and if it locates DETECT\_JAR, it uses that instead of pulling the latest [solution_name] shell script.
## Air Gap option
[solution_name] can be configured to run in an air gap fashion, see: [Air Gap](../../downloadingandinstalling/airgap.md).

In freestyle and Pipeline jobs, you can toggle between the different modes for running [solution_name] in the plugin such as pulling the Detect.jar from scripts or $DETECT\_JAR\_PATH, or from a specified Tool Installation.
## Running Detect in a job
You can run [solution_name] as a post-build action or a Pipeline step.
### Pipeline step
You can configure the scan as a pipeline step in a Pipeline job.

Refer to the [pipeline example](../../integrations/jenkinsplugin/jenkinspipelinejob.md)
### Post-build actions
You can configure the scan as a post-build action in a freestyle job. You can have multiple post-build actions, but only one [solution_name] post-build action.

Refer to the [freestyle example](../../integrations/jenkinsplugin/jenkinsfreestylejob.md).
## DSL considerations
The [solution_name] for Jenkins plugin provides Dynamic DSL for both freestyle steps and pipeline steps. Read more at [Dynamic DSL](https://github.com/jenkinsci/job-dsl-plugin/wiki/Dynamic-DSL).

**Note:** that versions 1.72 and later of the DSL plugin do not support the [solution_name] for Jenkins plugin pipeline steps.
