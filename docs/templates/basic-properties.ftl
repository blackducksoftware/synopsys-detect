This page lists only detect's basic properties, for advanced and deprecated properties see [all properties](../all-properties).

<#list groups as group>

[${group.groupName}](../${group.groupName}/)

| Property | Description |
| --- | --- |
<#list group.options as option>
| [${option.propertyKey}](../${option.location}) | <#if option.defaultValue??>default: ${option.defaultValue} <br /><br /> </#if><#if option.hasAcceptableValues> Acceptable Values: ${option.acceptableValues?join(", ")} <br /><br /></#if><#if option.propertyName?has_content>${option.propertyName}: </#if>${option.description} <br /><br /> <#if option.deprecated>**DEPRECATED: ${option.deprecatedDescription!"This property is deprecated."}**</#if> |
</#list>

</#list>
