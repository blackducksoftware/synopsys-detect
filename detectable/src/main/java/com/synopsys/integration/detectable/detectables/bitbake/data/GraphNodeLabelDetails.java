package com.synopsys.integration.detectable.detectables.bitbake.data;

public class GraphNodeLabelDetails {
    private final String nameType;
    private final String version;
    private final String recipeSpec;

    public GraphNodeLabelDetails(String nameType, String version, String recipeSpec) {
        this.nameType = nameType;
        this.version = version;
        this.recipeSpec = recipeSpec;
    }

    public String getNameType() {
        return nameType;
    }

    public String getVersion() {
        return version;
    }

    public String getRecipeSpec() {
        return recipeSpec;
    }
}
