# Auto-escaping Parameters

In [solution_name] for Jenkins, several special parameters are automatically escaped. 
The workflows pertaining to quotation marks and spaces are as follows.

- Detect properties must be separated by spaces or carriage returns/line feeds.
- Values containing single quotes must be surrounded with double quotation marks.
- Values containing double quotes must be surrounded with single quotation marks.

## Considerations for name escaping conventions

You can turn off auto escaping by setting the environment variable *DETECT\_PLUGIN\_ESCAPING* to false.
Jenkins enables you to set an environment variable at different levels, such as globally or on a per-job basis. If you set the environment variable globally to one value, you can set it at the job level to another value. Synopsys recommends setting the environment variable globally to skip escaping (ensuring past jobs work as expected), and then if you want to make jobs with the auto escaping enabled, you modify the environment variable flag in that job's configuration to enable escaping the characters. The easiest way to accomplish this is to install the ["Environment Injector" Jenkins plugin](https://plugins.jenkins.io/envinject/).

**Note:** In [solution_name] plugin version 9, the above recommendations remain the same for agents on Windows systems.  For those agents running on 'NIX systems, *DETECT\_PLUGIN\_ESCAPING* should be set to false.  Ensure that you adhere to the quoting conventions described above. Any input with spaces in the Jenkins configuration should be enclosed in quotes.

[solution_name] for Jenkins allows some special characters, and spaces can be included without escape sequences. Therefore, instead of `My\ Test\ Project1`, you can pass it as My Test Project1 and the project is created successfully and uploaded to [blackduck_product_name] as `My Test Project1*.*`
