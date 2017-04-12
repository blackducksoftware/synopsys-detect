package com.blackducksoftware.integration.hub.packman.parser.cocoapods;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.packman.parser.model.Package;

public class Podfile {

    public List<Package> targets = new ArrayList<>();

    public String platform;

    public String useFramworks;

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        if (platform != null) {
            builder.append("platform :" + platform + "\n\n");
        }
        for (final Package p : targets) {
            builder.append("target '");
            builder.append(p.name);
            builder.append("' do\n");
            for (final Package dep : p.dependencies) {
                builder.append("  pod '");
                builder.append(dep.name);
                builder.append("', '");
                builder.append(dep.version);
                builder.append("'\n");
            }
            builder.append("end\n\n");
        }
        return builder.toString();
    }
}
