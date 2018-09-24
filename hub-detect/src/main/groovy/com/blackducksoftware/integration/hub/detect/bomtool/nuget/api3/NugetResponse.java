package com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class NugetResponse {
    @SerializedName("items")
    private List<NugetCatalogPage> items;

    public List<NugetCatalogPage> getItems() {
        return items;
    }

    public void setItems(final List<NugetCatalogPage> items) {
        this.items = items;
    }
}
