package com.blackducksoftware.integration.hub.packman.bomtool.npm

import java.util.List

import org.apache.commons.lang3.builder.RecursiveToStringStyle
import org.apache.commons.lang3.builder.ReflectionToStringBuilder

import com.google.gson.annotations.SerializedName

class NpmCliNode {
	String name
	String version
	Map<String, NpmCliNode> dependencies
}