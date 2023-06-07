package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class CLLComponent {

    private final String groupID;
    private final String artifactID;
    private final String version;
    private final CLLMetadata metadata;
    public CLLComponent(ExternalId externalId, CLLMetadata metadata) {
        this.groupID = externalId.getGroup();
        this.artifactID = externalId.getName();
        this.version = externalId.getVersion();
        this.metadata = metadata;
    }

    // function to split an external ID into g, a and v (and maybe other types in the future?)

}
