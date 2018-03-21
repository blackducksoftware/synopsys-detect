package com.blackducksoftware.integration.hub.detect.bomtool.cran;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PackratPackager {
    public ExternalIdFactory externalIdFactory;

    public PackratPackager(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public DependencyGraph extractProjectDependencies(final List<String> packratLock) {
        PackRatNodeParser packRatNodeParser = new PackRatNodeParser(externalIdFactory);
        return packRatNodeParser.parseProjectDependencies(packratLock);
    }

    public String getProjectName(final List<String> descriptionContents) {
        String name = null;
        for (String line : descriptionContents) {
            if (line.contains("Package: ")) {
                name = line.replace("Package: ", "").trim();
                break;
            }
        }
        return name;
    }

    public String getVersion(final List<String> descriptionContents) {
        for(String descriptionContent : descriptionContents) {
            if(descriptionContent.contains("Version: ")) {
               return  descriptionContent.replace("Version: ", "").trim();
            }
        }
        return null;
    }

}
