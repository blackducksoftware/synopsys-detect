/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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

public class SbtProjectMatcher {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final SbtDotGraphNodeParser sbtDotGraphNodeParser;

    public SbtProjectMatcher(final SbtDotGraphNodeParser sbtDotGraphNodeParser) {
        this.sbtDotGraphNodeParser = sbtDotGraphNodeParser;
    }

    public String determineProjectNodeID(GraphParser graphParser) throws DetectableException {
        Set<String> nodeIdsUsedInDestination = graphParser.getEdges().values().stream()
                                                   .map(GraphEdge::getNode2)
                                                   .map(GraphElement::getId)
                                                   .collect(Collectors.toSet());
        Set<String> allNodeIds = new HashSet<>(graphParser.getNodes().keySet());
        Set<String> nodeIdsWithNoDestination = SetUtils.difference(allNodeIds, nodeIdsUsedInDestination);

        if (nodeIdsWithNoDestination.size() == 1) {
            return nodeIdsWithNoDestination.stream().findFirst().get();
        } else {
            throw new DetectableException("Unable to determine which node was the project in an SBT graph. Please contact support. Possibilities are: " + String.join(",", nodeIdsWithNoDestination));
        }
    }
}
