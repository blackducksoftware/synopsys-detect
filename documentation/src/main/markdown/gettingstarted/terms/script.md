# [solution_name] script

The primary function of the [solution_name] scripts is to download and execute the [solution_name] JAR file, which enables the scan.

You download and run the latest version of [solution_name] using the following commands, and add properties to refine the instruction.

Windows:
````
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect8.ps1?$(Get-Random) | iex; detect"
````

Linux/MacOs:
````
bash <(curl -s https://detect.synopsys.com/detect8.sh)
````
