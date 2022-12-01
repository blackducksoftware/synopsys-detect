# Configuring and Running the Plugin
After you install the plugin, you configure it in Pipeline task.

Configure your [solution_name] for Azure DevOps plugin by adding configuration for your [blackduck_product_name] server and adding Detect arguments.

   <figure>
    <img src="../azureplugin/images/configuringplugin.png"
         alt="Configuring plugin">
    <figcaption>Configuring and running the plugin</figcaption>
</figure>

## Configuring the plugin

1. Navigate to **Your Collection > Project > Pipelines > Tasks**. The plugin adds a new task of **Run [solution_name] for your build**. 
   You must add this task to your build queue. 
1. Click **Run [solution_name] for your build**, and the **[solution_name]** panel displays on the right. In the **[solution_name]** configuration panel, complete the following fields and options.
1. **Display name:** Type a unique name in this field.  Note that the name you type here displays in the left panel; the default name is **Run [solution_name] for your build**.
1. Click **+ New** to add a new **[blackduck_product_name] Service Endpoint** and then configure the details.
1. Click **+ New** to add a new **[blackduck_product_name] Proxy Service Endpoint** and then configure the details.
1. **Detect Version**: Version of the [solution_name] binary to use. Synopsys recommends using the latest but you can specify a version override if desired.
1. **Detect Run Mode:** Select the run mode. If you select Use Airgap Mode, a Detect Air Gap Jar Directory Path field opens in which you must specify the [solution_name] Air Gap Jar Path.
1. **Detect Arguments**: Here you can include additional [solution_name]* arguments; [solution_name] picks up your build environment variables and your project variables. Use a new line or space to separate multiple arguments. Use double quotes to escape. You can use environment and build variables.
For more information on [solution_name] arguments, refer to [Properties](../../properties/configuration/overview.md).

1. **Detect Folder**: The location to download the Detect jar or the location of an existing Detect jar. The default is the system temp directory.  To specify a different directory, type the directory path and name in the field.

Windows agents require an absolute path when specifying detect download location in the **Detect Folder** field.

1. **Add Detect Task Summary**: Click this checkbox to add a summary of the Detect task to the build summary task.


In the user interface, fields with a red asterisk ( **\*** ) are required.  Some default values are provided, such as version.

**Note:** that the following fields belong to Azure DevOps, and are not part of the Detect plugin:

- Task version
- Display name
- Control Options
- Output Variables
