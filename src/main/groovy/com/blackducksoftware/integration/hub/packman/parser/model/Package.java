package com.blackducksoftware.integration.hub.packman.parser.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.blackducksoftware.integration.hub.bdio.simple.model.ExternalId;
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Package {
    public ExternalId externalId;

    public Forge forge;

    public List<Package> dependencies = new ArrayList<>();

    public Package() {
    }

    public Package(final ExternalId externalId) {
        this.externalId = externalId;
    }

    public Package(final String packageName, final String packageVersion) {
        this.externalId = new ExternalId(packageName, packageVersion);
    }

    @Override
    public String toString() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String json = gson.toJson(this);
        return json;
    }

    public static Package packageFromString(final String str, final Pattern regex, final int group1, final int group2, final Forge forge) {
        final Matcher matcher = regex.matcher(str);
        if (matcher.matches()) {
            try {
                final Package dependency = new Package(matcher.group(group1).trim(), matcher.group(group2).trim());
                dependency.forge = forge;
                return dependency;
            } catch (final IndexOutOfBoundsException e) {
                // TODO: Log
                System.out.println("Couldn't regex match " + regex.toString() + " >" + str);
            }
        }
        return null;
    }
}
