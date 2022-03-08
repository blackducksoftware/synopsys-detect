package com.synopsys.integration.detectable.detectables.sbt.dot;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.SetUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paypal.digraph.parser.GraphEdge;
import com.paypal.digraph.parser.GraphElement;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class SbtRootNodeFinder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SbtDotGraphNodeParser sbtDotGraphNodeParser;

    public SbtRootNodeFinder(SbtDotGraphNodeParser sbtDotGraphNodeParser) {
        this.sbtDotGraphNodeParser = sbtDotGraphNodeParser;
    }

    public Set<String> determineRootIDs(GraphParser graphParser) throws DetectableException {
        Set<String> nodeIdsUsedInDestination = graphParser.getEdges().values().stream()
            .map(GraphEdge::getNode2)
            .map(GraphElement::getId)
            .collect(Collectors.toSet());
        Set<String> allNodeIds = new HashSet<>(graphParser.getNodes().keySet());
        return SetUtils.difference(allNodeIds, nodeIdsUsedInDestination);
    }
}
