package com.blackducksoftware.integration.hub.packman.util

import org.apache.commons.lang3.builder.RecursiveToStringStyle
import org.apache.commons.lang3.builder.ReflectionToStringBuilder

class NameVersionNode {
    def name
    def version
    def children = []

    @Override
    String toString() {
        return ReflectionToStringBuilder.toString(this, RecursiveToStringStyle.JSON_STYLE)
    }
}
