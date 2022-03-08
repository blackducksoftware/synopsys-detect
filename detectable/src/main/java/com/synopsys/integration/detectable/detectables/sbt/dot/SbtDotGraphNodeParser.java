package com.synopsys.integration.detectable.detectables.sbt.dot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class SbtDotGraphNodeParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public SbtDotGraphNodeParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public Dependency nodeToDependency(String id) {
        String[] pieces = id.trim().split(":");
        String group = unquote(pieces[0]);
        String name = unquote(pieces[1]);
        String version = unquote(pieces[2]);
        ExternalId externalId = externalIdFactory.createMavenExternalId(group, name, version);
        return new Dependency(name, version, externalId);
    }

    public String unquote(String item) {
        if (item.startsWith("\""))
            item = item.substring(1);
        if (item.endsWith("\""))
            item = item.substring(0, item.length() - 1);
        return item;
    }
}
