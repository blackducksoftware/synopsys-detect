package com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class NugetCatalogPage {
    @SerializedName("items")
    private List<NugetPackage> items;

    public List<NugetPackage> getItems() {
        return items;
    }

    public void setItems(final List<NugetPackage> items) {
        this.items = items;
    }
}
