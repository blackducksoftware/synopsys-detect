# Running behind a proxy

When running behind a proxy:

1. The script (${bash_script_name} or ${powershell_script_name}) requires proxy details in order to download
the ${solution_name} .jar file.
1. ${solution_name} itself (the code in the .jar file) requires proxy details in order to download inspectors and
connect to ${blackduck_product_name} and ${polaris_product_name}.

TTTTTTTTTTBBBBBBBBBBBDDDDDDDDDDDDd

## Providing proxy details to ${solution_name}

${solution_name} looks for proxy details in the properties

* Proxy host: blackduck.proxy.host or BLACKDUCK_PROXY_HOST
* Proxy port: blackduck.proxy.port or BLACKDUCK_PROXY_PORT
* Proxy username: blackduck.proxy.username or BLACKDUCK_PROXY_USERNAME
* Proxy password: blackduck.proxy.password or BLACKDUCK_PROXY_PASSWORD

THERE ARE MORE THAN THESE.........

## Providing proxy details to ${bash_script_name}
## Providing proxy details to ${powershell_script_name}

${powershell_script_name} derives proxy details from the following environment variables:



Proxy details can be passed to the ${powershell_script_name} as follows:

    ${r"${Env:blackduck.proxy.host}"} = $ProxyHost
    ${r"${Env:blackduck.proxy.port}"} = $ProxyPort
    ${r"${Env:blackduck.proxy.password}"} = $ProxyUsername
    ${r"${Env:blackduck.proxy.username}"} = $ProxyPassword
    powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect.ps1?$(Get-Random) | iex; detect"

Because ${powershell_script_name} uses the same environment variable names as ${solution_name} itself,
${solution_name} also picks up the proxy details from these environment variables.





