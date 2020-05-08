package com.synopsys.integration.detectable.detectables.pip.poetry.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class PoetryLock {
    @SerializedName("package")
    public List<Package> packages;
}
