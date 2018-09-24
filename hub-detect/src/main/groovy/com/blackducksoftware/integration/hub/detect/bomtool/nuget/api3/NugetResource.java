package com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3;

import java.util.Optional;

import com.google.gson.annotations.SerializedName;

public class NugetResource {
    @SerializedName("@id")
    private String id;

    @SerializedName("@type")
    private String type;

    @SerializedName("comment")
    private String comment;

    public Optional<String> getId() {
        return Optional.ofNullable(id);
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Optional<String> getType() {
        return Optional.ofNullable(type);
    }

    public void setType(final String type) {
        this.type = type;
    }

    public Optional<String> getComment() {
        return Optional.ofNullable(comment);
    }

    public void setComment(final String comment) {
        this.comment = comment;
    }
}
