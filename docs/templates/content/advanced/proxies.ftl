# Running behind a proxy

When running behind a proxy:

1. The script (${bash_script_name} or ${powershell_script_name}) requires proxy details in order to do a version
check on, and/or download the ${solution_name} .jar file.
1. ${solution_name} itself (the code in the .jar file) requires proxy details in order to download inspectors and
connect to ${blackduck_product_name} and ${polaris_product_name}.

## Providing proxy details to ${solution_name}

${solution_name} looks for proxy details in the properties whose names start with blackduck.proxy,
including:

* blackduck.proxy.host (proxy host)
* blackduck.proxy.port (proxy port)
* blackduck.proxy.username (proxy username)
* blackduck.proxy.password (proxy password)

See [properties](/properties/all-properties) for more information.

## Providing proxy details to ${bash_script_name}

The curl commands executed by ${bash_script_name} to do a version check on, and/or download the ${solution_name}
.jar file require additional command line options when run behind a proxy. For more information
on curl options, refer to the [curl documentation](https://curl.haxx.se/docs/manpage.html).

To provide additional curl command line options for ${bash_script_name} to use
when it executes curl, set the environment variable DETECT_CURL_OPTS before running
${bash_script_name}. For example:

    export DETECT_CURL_OPTS=--proxy http://myproxy:3128
    ./${bash_script_name}

When using ${bash_script_name} to execute ${solution_name} you will also need to set proxy properties
for ${solution_name} as described above.

## Providing proxy details to ${powershell_script_name}

${powershell_script_name} derives proxy details from environment variables
whose names match the ${solution_name} proxy property names.
Configuring ${powershell_script_name} for your proxy involves
setting those environment variables before running ${powershell_script_name}.
For example:

    ${r"${Env:blackduck.proxy.host}"} = $ProxyHost
    ${r"${Env:blackduck.proxy.port}"} = $ProxyPort
    ${r"${Env:blackduck.proxy.password}"} = $ProxyUsername
    ${r"${Env:blackduck.proxy.username}"} = $ProxyPassword
    powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect"

When using ${powershell_script_name} to execute ${solution_name}, ${solution_name} will also receive the proxy details
from these environment variables, so no additional configuration is required for ${solution_name}.





