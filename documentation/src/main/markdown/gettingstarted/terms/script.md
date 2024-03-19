# Script

The primary function of the [company_name] [solution_name] script is to download and execute the [company_name] [solution_name] JAR file, which enables the scan capability.

Users download and run the latest version of [company_name] [solution_name] by providing the following commands, and adding properties to refine the behaviour.

Windows:
````
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect9.ps1?$(Get-Random) | iex; detect"
````

Linux/MacOs:
````
bash <(curl -s https://detect.synopsys.com/detect9.sh)
````
