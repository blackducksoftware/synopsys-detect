package com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3;

import com.google.gson.annotations.SerializedName;

public class NugetCatalogEntry {
    @SerializedName("@id")
    private String id;

    @SerializedName("authors")
    private String authors;

    @SerializedName("id")
    private String packageName;

    @SerializedName("version")
    private String packageVersion;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(final String authors) {
        this.authors = authors;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public String getPackageVersion() {
        return packageVersion;
    }

    public void setPackageVersion(final String packageVersion) {
        this.packageVersion = packageVersion;
    }
}
