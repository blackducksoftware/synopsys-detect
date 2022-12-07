package com.synopsys.integration.detect.configuration.help.json.model;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonSearchRule {
    private List<String> yieldsTo = new ArrayList<>();
    private List<String> notNestableBeneath = new ArrayList<>();
    private List<String> notNestableBeneathDetectables = new ArrayList<>();
    private Integer maxDepth = 0;
    private Boolean nestable = false;

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

    public List<String> getYieldsTo() {
        return yieldsTo;
    }

    public void setYieldsTo(List<String> yieldsTo) {
        this.yieldsTo = yieldsTo;
    }

    public void setNotNestableBeneath(final List<String> notNestableBeneath) {
        this.notNestableBeneath = notNestableBeneath;
    }
    public List<String> getNotNestableBeneath() {
        return notNestableBeneath;
    }

    public void setNotNestableBeneathDetectables(final List<String> notNestableBeneathDetectables) {
        this.notNestableBeneathDetectables = notNestableBeneathDetectables;
    }

    public List<String> getNotNestableBeneathDetectables() {
        return notNestableBeneathDetectables;
    }
}