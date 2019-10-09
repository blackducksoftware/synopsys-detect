@echo off
setlocal enabledelayedexpansion

for /f %%x in (${dataFile}) do (
    set /a var=%%x
)
set /a out=%var%+1

> ${dataFile} echo %out%

<#list files as file>
set cmd[${file?index}]="${file}"
</#list>

type !cmd[%var%]!