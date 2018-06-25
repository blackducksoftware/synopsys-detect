package com.blackducksoftware.integration.hub.detect.bomtool.go.parse;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class GodepsFile {
    @SerializedName("ImportPath")
    private String importPath;

    @SerializedName("GoVersion")
    private String goVersion;

    @SerializedName("GodepVersion")
    private String godepVersion;

    @SerializedName("Packages")
    private List<String> packages;

    @SerializedName("Deps")
    private List<GodepDependency> deps;

    public String getImportPath() {
        return importPath;
    }

    public void setImportPath(final String importPath) {
        this.importPath = importPath;
    }

    public String getGoVersion() {
        return goVersion;
    }

    public void setGoVersion(final String goVersion) {
        this.goVersion = goVersion;
    }

    public String getGodepVersion() {
        return godepVersion;
    }

    public void setGodepVersion(final String godepVersion) {
        this.godepVersion = godepVersion;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(final List<String> packages) {
        this.packages = packages;
    }

    public List<GodepDependency> getDeps() {
        return deps;
    }

    public void setDeps(final List<GodepDependency> deps) {
        this.deps = deps;
    }

}
