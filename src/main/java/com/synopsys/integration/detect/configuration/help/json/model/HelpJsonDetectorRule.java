package com.synopsys.integration.detect.configuration.help.json.model;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonDetectorRule {
    private String detectorType = "";
    private Integer maxDepth = 0;
    private Boolean nestable = false;
    private Boolean nestInvisible = false;
    private List<HelpJsonDetectorEntryPoint> entryPoints = new ArrayList<>();
    private List<String> yieldsTo = new ArrayList<>();

    public List<HelpJsonDetectorEntryPoint> getEntryPoints() {
        return entryPoints;
    }

    public void setEntryPoints(List<HelpJsonDetectorEntryPoint> entryPoints) {
        this.entryPoints = entryPoints;
    }

    public String getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(String detectorType) {
        this.detectorType = detectorType;
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth;
    }

    public Boolean getNestable() {
        return nestable;
    }

    public void setNestable(Boolean nestable) {
        this.nestable = nestable;
    }

    public Boolean getNestInvisible() {
        return nestInvisible;
    }

    public void setNestInvisible(Boolean nestInvisible) {
        this.nestInvisible = nestInvisible;
    }

    public List<String> getYieldsTo() {
        return yieldsTo;
    }

    public void setYieldsTo(List<String> yieldsTo) {
        this.yieldsTo = yieldsTo;
    }

}