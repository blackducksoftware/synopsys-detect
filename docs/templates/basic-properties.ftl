# Basic Properties

This page lists ${solution_name}'s basic properties; for advanced and deprecated properties, refer to [all properties](all-properties.md).

<#list groups as group>

## [${group.groupName}](${group.location}.md)

| Property | Description |
| --- | --- |
<#list group.options as option>
| [${option.propertyKey}](${option.location}) | <#if option.defaultValue?has_content>default: ${option.defaultValue} <br /><br /> </#if><#if option.hasAcceptableValues> Acceptable Values: ${option.acceptableValues?join(", ")} <br /><br /></#if><#if option.propertyName?has_content>${option.propertyName}: </#if>${option.description} <br /><br /> <#if option.deprecated>**DEPRECATED: ${option.deprecatedDescription!"This property is deprecated."}**</#if> |
</#list>

</#list>
