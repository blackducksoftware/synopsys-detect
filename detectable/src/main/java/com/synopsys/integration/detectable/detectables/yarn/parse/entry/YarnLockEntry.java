package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

import java.util.List;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.util.Stringable;

public class YarnLockEntry extends Stringable {
    private final boolean metadataEntry;
    private final List<YarnLockEntryId> ids;
    private final String version;
    private final List<YarnLockDependency> dependencies;

    public YarnLockEntry(boolean metadataEntry, List<YarnLockEntryId> ids, String version, List<YarnLockDependency> dependencies) {
        this.metadataEntry = metadataEntry;
        this.ids = ids;
        this.version = version;
        this.dependencies = dependencies;
    }

    public boolean isMetadataEntry() {
        return metadataEntry;
    }

    public List<YarnLockEntryId> getIds() {
        return ids;
    }

    public List<YarnLockDependency> getDependencies() {
        return dependencies;
    }

    public String getVersion() {
        return version;
    }
    
    public String toPrettyString() {
        if (!ids.isEmpty()) {
            String name = ids.get(0).getName();
            StringBuilder sb = new StringBuilder();
            ids.forEach(id -> {
                if (sb.length() != 0) {
                    sb.append(", ").append(id.getVersion());
                } else {
                    sb.append(id.getVersion());
                }
            });
            sb.append("], Dependencies:");
            if (!dependencies.isEmpty()) {
                dependencies.forEach(dep -> {
                    sb.append("\n\t").append(dep.getName()).append(" : ").append(dep.getVersion()).append(", Optional:").append(dep.isOptional());
                });
            }
            return name+" : "+version+" <- ["+sb.toString();
        }
        return "";
    }
}
