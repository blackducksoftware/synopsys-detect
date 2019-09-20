# ${groupName}

<#list simple as option>
##${option.propertyName}
```
<#if option.hasAcceptableValues>
--${option.propertyKey}=${option.acceptableValues?join(",")} 
<#elseif option.defaultValue??>
--${option.propertyKey}=${option.defaultValue}
<#else>
--${option.propertyKey}
</#if>
```

${option.description}

${option.detailedDescription!""}

|Details||
|---|---|
|Added|${option.addedInVersion}|
|Type|${option.propertyType}|
|Default Value|${option.defaultValue!"None"}|
|Comma Seperated|${option.isCommaSeparatedList?then("Yes", "No")}|
|Case Sensative|${option.caseSensitiveValues?then("Yes", "No")}|
<#if option.hasAcceptableValues>
|Acceptable Values|${option.acceptableValues?join(", ")}|
<#else>
|Acceptable Values|Any|
</#if>
|Strict|${option.strictValues?then("Yes", "No")}|

</#list> 

<#list advanced as option>
##${option.propertyName} (Advanced)
```
<#if option.hasAcceptableValues>
--${option.propertyKey}=${option.acceptableValues?join(",")} 
<#elseif option.defaultValue??>
--${option.propertyKey}=${option.defaultValue}
<#else>
--${option.propertyKey}
</#if>
```

${option.description}

${option.detailedDescription!""}

|Details||
|---|---|
|Added|${option.addedInVersion}|
|Type|${option.propertyType}|
|Default Value|${option.defaultValue!"None"}|
|Comma Seperated|${option.isCommaSeparatedList?then("Yes", "No")}|
|Case Sensative|${option.caseSensitiveValues?then("Yes", "No")}|
<#if option.hasAcceptableValues>
|Acceptable Values|${option.acceptableValues?join(", ")}|
<#else>
|Acceptable Values|Any|
</#if>
|Strict|${option.strictValues?then("Yes", "No")}|

</#list> 


<#list deprecated as option>
##${option.propertyName} (Deprecated) 
```
<#if option.hasAcceptableValues>
--${option.propertyKey}=${option.acceptableValues?join(",")} 
<#elseif option.defaultValue??>
--${option.propertyKey}=${option.defaultValue}
<#else>
--${option.propertyKey}
</#if>
```

${option.description}

${option.detailedDescription!""}

|Details||
|---|---|
|Added|${option.addedInVersion}|
|Type|${option.propertyType}|
|Default Value|${option.defaultValue!"None"}|
|Comma Seperated|${option.isCommaSeparatedList?then("Yes", "No")}|
|Case Sensative|${option.caseSensitiveValues?then("Yes", "No")}|
<#if option.hasAcceptableValues>
|Acceptable Values|${option.acceptableValues?join(", ")}|
<#else>
|Acceptable Values|Any|
</#if>
|Strict|${option.strictValues?then("Yes", "No")}|

</#list> 
