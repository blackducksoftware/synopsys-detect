package com.blackducksoftware.integration.hub.packman.parser.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Package {
    public String name;

    public String version;

    public List<Package> dependencies = new ArrayList<>();

    public Package() {
    }

    public Package(final String packageName, final String packageVersion) {
        this.name = packageName;
        this.version = packageVersion;
    }

    @Override
    public String toString() {
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        final String json = gson.toJson(this);
        return json;
    }

    public static Package packageFromString(final String str, final Pattern regex, final int group1, final int group2) {
        final Matcher matcher = regex.matcher(str);
        if (matcher.matches()) {
            try {
                final Package dependency = new Package(matcher.group(group1).trim(), matcher.group(group2).trim());
                return dependency;
            } catch (final IndexOutOfBoundsException e) {
                // TODO: Log
                System.out.println("Couldn't regex match " + regex.toString() + " >" + str);
            }
        }
        return null;
    }
}
