# Auto-escaping Parameters

In [solution_name] for Jenkins, several special parameters are automatically escaped. 
The workflows pertaining to quotation marks and spaces are as follows.

- Detect properties must be separated by spaces or carriage returns/line feeds.
- Values containing spaces must be surrounded by either single or double quotation marks ('single' or “double”).
- Values containing single quotes must be surrounded with double quotation marks.
- Values containing double quotes must be surrounded with single quotation marks.

## Considerations for name escaping conventions

You can turn off auto escaping by setting the environment variable *DETECT\_PLUGIN\_ESCAPING* to false.
Jenkins enables you to set an environment variable at different levels, such as globally or on a per-job basis. If you set the environment variable globally to one value, you can set it at the job level to another value. Synopsys recommends setting the environment variable globally to skip escaping (ensuring past jobs work as expected), and then if you want to make jobs with the auto escaping enabled, you modify the environment variable flag in that job's configuration to enable escaping the characters. The easiest way to accomplish this is to install the ["Environment Injector" Jenkins plugin](https://plugins.jenkins.io/envinject/).

**Note:** In [solution_name] plugin version 8, the above recommendations remain the same for agents on Windows systems.  For those agents running on 'NIX systems, *DETECT\_PLUGIN\_ESCAPING* should be set to false.  Ensure that you adhere to the quoting conventions described above. Any input with spaces in the Jenkins configuration should be enclosed in quotes.

For users of [solution_name] for Jenkins versions 2.0.0/2.0.1 that are upgrading: if you escaped your values, your escape characters are escaped and appear literally.

For example, you have a job that was created in versions 2.0.0 or 2.0.1. In [solution_name] for Jenkins version 8.0.0, spaces are not allowed; therefore they must be escaped to have a name with spaces. If the project name is My Test Project1, you must pass the project name as `My\ Test\ Project1`. Hence, the project is uploaded in [blackduck_product_name] as `My Test Project1*.*`

[solution_name] for Jenkins allows some special characters, and spaces can be included without escape sequences. Therefore, instead of `My\ Test\ Project1`, you can pass it as My Test Project1 and the project is created successfully and uploaded to [blackduck_product_name] as `My Test Project1*.*`

If there are jobs that are already configured in 2.0.1 as `My\ Test\ Project2` and after upgrading the [solution_name] for Jenkins, these existing projects are uploaded to [blackduck_product_name] as `My\ Test\ Project2*.*`

**Note:** that this change has an impact on jobs created in version 2.0.1 with existing project names containing escape sequences and then upgrading [solution_name] for Jenkins to version 2.0.2+.

If there are existing jobs created with escape sequences, the impact of this change can be greater.
