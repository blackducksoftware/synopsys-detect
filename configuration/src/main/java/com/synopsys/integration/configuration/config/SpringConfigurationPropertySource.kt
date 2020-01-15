package com.synopsys.integration.configuration.config

import org.springframework.boot.context.properties.source.ConfigurationPropertyName
import org.springframework.boot.context.properties.source.ConfigurationPropertySource
import org.springframework.boot.context.properties.source.ConfigurationPropertySources
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.EnumerablePropertySource

class SpringConfigurationPropertySource(private val name: String, private val propertySource: ConfigurationPropertySource) : PropertySource {
    companion object {
        fun fromConfigurableEnvironment(configurableEnvironment: ConfigurableEnvironment): List<SpringConfigurationPropertySource> {
            val sources = ConfigurationPropertySources.get(configurableEnvironment).toList()
            return sources.map {
                val underlying = it.underlyingSource
                if (underlying is org.springframework.core.env.PropertySource<*>) {
                    SpringConfigurationPropertySource(underlying.name, it)
                } else {
                    SpringConfigurationPropertySource("unknown", it)
                }
            }
        }
    }

    override fun hasKey(key: String): Boolean {
        return null != propertySource.getConfigurationProperty(ConfigurationPropertyName.of(key))
    }

    override fun getValue(key: String): String? {
        val property = propertySource.getConfigurationProperty(ConfigurationPropertyName.of(key))
        if (property != null) {
            return property.value.toString()
        }
        return null
    }

    override fun getName(): String = name

    //A basic 'Spring Property Source' does not have the concept of Origin. Only Spring Configuration Property Sources do.
    override fun getOrigin(key: String): String? {
        val property = propertySource.getConfigurationProperty(ConfigurationPropertyName.of(key))
        if (property != null) {
            val origin = property.origin
            if (origin == null) {
                return origin.toString()
            }
        }
        return getName()
    }

    override fun getKeys(): Set<String> {
        return if (propertySource is EnumerablePropertySource<*>) {
            propertySource.propertyNames.toSet()
        } else {
            emptySet()
        }
    }
}