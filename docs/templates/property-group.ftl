<#ftl output_format="Markdown">
# ${groupName}

<#list simple as option>
## ${option.propertyName}
```<#noautoesc>
<#if option.hasAcceptableValues>
--${option.propertyKey}=${option.acceptableValues?join(",")} 
<#elseif option.defaultValue?has_content>
--${option.propertyKey}=${option.defaultValue}
<#else>
--${option.propertyKey}
</#if>
```</#noautoesc>

${option.description}

${option.detailedDescription!""}

|Details||
|---|---|
|Added|${option.addedInVersion}|
|Type|${option.propertyType}|
|Default Value|${option.defaultValue!"None"}|
|Comma Separated|${option.commaSeparatedList?then("Yes", "No")}|
|Case Sensitive|${option.caseSensitiveValues?then("Yes", "No")}|
<#if option.hasAcceptableValues>
|Acceptable Values|${option.acceptableValues?join(", ")}|
<#else>
|Acceptable Values|Any|
</#if>
|Strict|${option.strictValues?then("Yes", "No")}|
|Example|<#noautoesc>`${option.example!""}`</#noautoesc>|

</#list> 

<#list advanced as option>
## ${option.propertyName} (Advanced)
```<#noautoesc>
<#if option.hasAcceptableValues>
--${option.propertyKey}=${option.acceptableValues?join(",")} 
<#elseif option.defaultValue?has_content>
--${option.propertyKey}=${option.defaultValue}
<#else>
--${option.propertyKey}
</#if>
```</#noautoesc>

${option.description}

${option.detailedDescription!""}

|Details||
|---|---|
|Added|${option.addedInVersion}|
|Type|${option.propertyType}|
|Default Value|${option.defaultValue!"None"}|
|Comma Separated|${option.commaSeparatedList?then("Yes", "No")}|
|Case Sensitive|${option.caseSensitiveValues?then("Yes", "No")}|
<#if option.hasAcceptableValues>
|Acceptable Values|${option.acceptableValues?join(", ")}|
<#else>
|Acceptable Values|Any|
</#if>
|Strict|${option.strictValues?then("Yes", "No")}|
|Example|<#noautoesc>`${option.example!""}`</#noautoesc>|

</#list> 


<#list deprecated as option>
## ${option.propertyName} (Deprecated) 
```<#noautoesc>
<#if option.hasAcceptableValues>
--${option.propertyKey}=${option.acceptableValues?join(",")} 
<#elseif option.defaultValue?has_content>
--${option.propertyKey}=${option.defaultValue}
<#else>
--${option.propertyKey}
</#if>
```</#noautoesc>

${option.description}

${option.detailedDescription!""}

**DEPRECATED: ${option.deprecatedDescription!"This property is deprecated."} It will be removed in ${option.deprecatedRemoveInVersion}.**

|Details||
|---|---|
|Added|${option.addedInVersion}|
|Type|${option.propertyType}|
|Default Value|${option.defaultValue!"None"}|
|Comma Separated|${option.commaSeparatedList?then("Yes", "No")}|
|Case Sensitive|${option.caseSensitiveValues?then("Yes", "No")}|
<#if option.hasAcceptableValues>
|Acceptable Values|${option.acceptableValues?join(", ")}|
<#else>
|Acceptable Values|Any|
</#if>
|Strict|${option.strictValues?then("Yes", "No")}|

</#list> 
