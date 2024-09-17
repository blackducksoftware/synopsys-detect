# Script

The primary function of the [detect_product_short] script is to download and execute the [detect_product_short] JAR file, which enables the scan capability.

Users download and run the latest version of [detect_product_short] by providing the following commands, and adding properties to refine the behaviour.

Windows:
````
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.blackduck.com/detect9.ps1?$(Get-Random) | iex; detect"
````

Linux/MacOs:
````
bash <(curl -s https://detect.blackduck.com/detect9.sh)
````
