package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.bdio.model.externalid.ExternalId;

/**
 * This class is based on Component Locator Library's input schema.
 * Any changes made here to the expected input objects should be accompanied by changes in the library and vice versa.
 */
public class Component {

    private final String groupID;
    private final String artifactID;
    private final String version;
    private final Metadata metadata;
    public Component(ExternalId externalId, Metadata metadata) {
        this.groupID = externalId.getGroup();
        this.artifactID = externalId.getName();
        this.version = externalId.getVersion();
        this.metadata = metadata;
    }

    public Component(String groupID, String artifactID, String version, Metadata metadata) {
        this.groupID = groupID;
        this.artifactID = artifactID;
        this.version = version;
        this.metadata = metadata;
    }

}
