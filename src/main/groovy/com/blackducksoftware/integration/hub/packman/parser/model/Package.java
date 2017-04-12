package com.blackducksoftware.integration.hub.packman.parser.model;

import java.util.ArrayList;
import java.util.List;

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
}
