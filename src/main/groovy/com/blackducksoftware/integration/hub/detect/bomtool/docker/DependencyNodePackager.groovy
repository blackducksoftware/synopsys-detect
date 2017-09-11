package com.blackducksoftware.integration.hub.detect.bomtool.docker

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.google.gson.Gson
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class DependencyNodePackager {
    @Autowired
    Gson gson

    public DependencyNode parse(String dependencyNodeJsonText) {
        gson.fromJson(dependencyNodeJsonText, DependencyNode.class)
    }
}
