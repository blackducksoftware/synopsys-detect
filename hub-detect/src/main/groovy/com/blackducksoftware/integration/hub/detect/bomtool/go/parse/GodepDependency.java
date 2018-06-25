package com.blackducksoftware.integration.hub.detect.bomtool.go.parse;

import com.google.gson.annotations.SerializedName;

public class GodepDependency {
    @SerializedName("ImportPath")
    private String importPath;

    @SerializedName("Comment")
    private String comment;

    @SerializedName("Rev")
    private String rev;

    public String getImportPath() {
        return importPath;
    }

    public void setImportPath(final String importPath) {
        this.importPath = importPath;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(final String rev) {
        this.rev = rev;
    }

}
