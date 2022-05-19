# Using environment variables

[solution_name] properties can also be set using environment variables.

On Linux, when setting a property value using an environment variable, the environment variable name
is the property name converted to uppercase, with period characters (".") converted to underscore
characters ("_"). For example:
```
export DETECT_PROJECT_NAME=MyProject
bash <(curl -s -L https://detect.synopsys.com/detect8.sh)
```

On Windows, the environment variable name can either be the original property
name, or the property name converted to uppercase with period characters (".") converted to underscore
characters ("_"). For example:
```
$Env:DETECT_PROJECT_NAME = MyProject
powershell "[Net.ServicePointManager]::SecurityProtocol = 'tls12'; irm https://detect.synopsys.com/detect8.ps1?$(Get-Random) | iex; detect"
```
