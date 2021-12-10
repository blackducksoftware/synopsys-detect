package com.synopsys.integration.detectable.detectables.ivy;

import java.util.List;

import org.xml.sax.helpers.DefaultHandler;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

//TODO- implement this class!
public class IvyDependenciesHandler extends DefaultHandler {
    private final ExternalIdFactory externalIdFactory;

    public IvyDependenciesHandler(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    List<Dependency> getDependencies() {
        return null;
    }
}
