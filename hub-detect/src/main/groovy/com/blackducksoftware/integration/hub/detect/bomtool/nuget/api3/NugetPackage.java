package com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3;

import com.google.gson.annotations.SerializedName;

public class NugetPackage {
    @SerializedName("catalogEntry")
    private NugetCatalogEntry catalogEntry;

    public NugetCatalogEntry getCatalogEntry() {
        return catalogEntry;
    }

    public void setCatalogEntry(final NugetCatalogEntry catalogEntry) {
        this.catalogEntry = catalogEntry;
    }
}
