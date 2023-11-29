package com.synopsys.integration.detectable.detectables.conan.cli.parser;

import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.conan.cli.parser.element.NodeElementParser;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNode;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;

public class ConanInfoNodeParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConanInfoLineAnalyzer conanInfoLineAnalyzer;
    private final NodeElementParser nodeElementParser;

    public ConanInfoNodeParser(ConanInfoLineAnalyzer conanInfoLineAnalyzer, NodeElementParser nodeElementParser) {
        this.conanInfoLineAnalyzer = conanInfoLineAnalyzer;
        this.nodeElementParser = nodeElementParser;
    }

    /*
     * A node looks like this:
     * ref:
     *     node body element (either key: value, key:\nlist-of-values)
     */
    public ConanInfoNodeParseResult parseNode(List<String> conanInfoOutputLines, int nodeStartIndex) {
        String nodeHeaderLine = conanInfoOutputLines.get(nodeStartIndex);
        ConanNodeBuilder<String> nodeBuilder = new ConanNodeBuilder<>();
        setRefAndDerivedFields(nodeBuilder, nodeHeaderLine.trim());
        int bodyLineCount = 0;
        for (int lineIndex = nodeStartIndex + 1; lineIndex < conanInfoOutputLines.size(); lineIndex++) {
            String nodeBodyLine = conanInfoOutputLines.get(lineIndex);
            logger.trace("Parsing line: {}: {}", lineIndex + 1, nodeBodyLine);
            // Check to see if we've overshot the end of the node
            Optional<ConanInfoNodeParseResult> result = getResultIfDone(nodeBodyLine, lineIndex, nodeStartIndex, bodyLineCount, nodeBuilder);
            if (result.isPresent()) {
                return result.get();
            }
            bodyLineCount++;
            // parseElement tells this code what line to parse next (= where it left off)
            lineIndex = nodeElementParser.parseElement(nodeBuilder, conanInfoOutputLines, lineIndex);
        }
        logger.trace("Reached end of conan info output");
        Optional<ConanNode<String>> node = nodeBuilder.build();
        return new ConanInfoNodeParseResult(conanInfoOutputLines.size() - 1, node.orElse(null));
    }

    private Optional<ConanInfoNodeParseResult> getResultIfDone(String nodeBodyLine, int lineIndex, int nodeStartIndex, int bodyLineCount, ConanNodeBuilder<String> nodeBuilder) {
        int indentDepth = conanInfoLineAnalyzer.measureIndentDepth(nodeBodyLine);
        if (indentDepth > 0) {
            // We're not done parsing this node
            return Optional.empty();
        }
        if (bodyLineCount == 0) {
            logger.trace("This wasn't a node (it was just a conan info command log message)");
            return Optional.of(new ConanInfoNodeParseResult(nodeStartIndex));
        } else {
            logger.trace("Reached end of node");
            Optional<ConanNode<String>> node = nodeBuilder.build();
            return Optional.of(new ConanInfoNodeParseResult(lineIndex - 1, node.orElse(null)));
        }
    }

    private void setRefAndDerivedFields(ConanNodeBuilder<String> nodeBuilder, String ref) {
        if (StringUtils.isBlank(ref)) {
            return;
        }

        if (ref.startsWith("conanfile.")) {
            setFieldsForRootNode(nodeBuilder, ref);
        } else {
            if (setFieldsForNonRootNode(nodeBuilder, ref)) {
                return;
            }
        }
        nodeBuilder.setRef(ref);
    }

    // conanfile.{txt,py}[ (projectname/version)]
    private boolean setFieldsForNonRootNode(ConanNodeBuilder<String> nodeBuilder, String ref) {
        StringTokenizer tokenizer = new StringTokenizer(ref, "/@");
        String name = tokenizer.nextToken();
        nodeBuilder.setName(name);
        if (name.contains(" ")) {
            nodeBuilder.setValid(false);
            return true;
        }
        if (tokenizer.hasMoreTokens()) {
            String version = tokenizer.nextToken();
            nodeBuilder.setVersion(version);
            if (version.contains(" ")) {
                nodeBuilder.setValid(false);
                return true;
            } else if (tokenizer.hasMoreTokens()) {
                nodeBuilder.setUser(tokenizer.nextToken());
                if (tokenizer.hasMoreTokens()) {
                    nodeBuilder.setChannel(tokenizer.nextToken());
                }
            }
        }
        return false;
    }

    // package/version[@user/channel]
    private void setFieldsForRootNode(ConanNodeBuilder<String> nodeBuilder, String ref) {
        StringTokenizer tokenizer = new StringTokenizer(ref, " \t()/");
        nodeBuilder.setPath(tokenizer.nextToken());
        if (tokenizer.hasMoreTokens()) {
            nodeBuilder.setName(tokenizer.nextToken());
            if (tokenizer.hasMoreTokens()) {
                nodeBuilder.setVersion(tokenizer.nextToken());
            }
        }
    }
}
