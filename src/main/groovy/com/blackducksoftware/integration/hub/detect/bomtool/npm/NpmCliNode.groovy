package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.springframework.stereotype.Component

@Component
class NpmCliNode {
	String name
	String version
	Map<String, NpmCliNode> dependencies
}