package com.synopsys.integration.detect.configuration.help.json.model;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonSearchRule {
    private List<String> yieldsTo = new ArrayList<>();
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

}