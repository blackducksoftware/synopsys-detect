package com.synopsys.integration.detectable.detectables.bazel.pipeline.step;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IntermediateStepDeDupLines implements IntermediateStep {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<String> process(List<String> input) {
        Set<String> deDupedAsSet = new HashSet<>(input);
        List<String> deDupedAsList = new ArrayList<>(deDupedAsSet);
        logger.trace("Deduped {} input lines down to {}", input.size(), deDupedAsList.size());
        return deDupedAsList;
    }
}
