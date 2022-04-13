# Deprecated Properties

This page lists [solution_name]'s deprecated properties; for both active and deprecated properties, refer to [all properties](all-properties.md).

<#list groups as group>

## [${group.groupName}](${group.location}.md)

| Property | Description |
| --- | --- |
<#list group.options as option>
| [${option.propertyKey}](${option.location}) | <#if option.defaultValue?has_content><p>default: ${option.defaultValue}</p> </#if><#if option.hasAcceptableValues> <p>Acceptable Values: ${option.acceptableValues?join(", ")} </p></#if><p><#if option.propertyName?has_content>${option.propertyName}: </#if>${option.description} </p> <p>**DEPRECATED: ${option.deprecatedDescription} This property will be removed in ${option.deprecatedRemoveInVersion}.** </p> |
</#list>

</#list>
