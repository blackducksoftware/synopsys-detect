package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.bitbake.model.GraphNodeLabelDetails;
import com.synopsys.integration.exception.IntegrationException;

// Example of a GraphNode label value:
// acl-native do_compile\n:2.3.1-r0\nvirtual:native:/workdir/poky/meta/recipes-support/attr/acl_2.3.1.bb
// Split into:
//  acl-native do_compile -> GraphNodeLabelDetails.nameType
//  2.3.1-r0 -> GraphNodeLabelDetails.version
//  virtual:native:/workdir/poky/meta/recipes-support/attr/acl_2.3.1.bb -> GraphNodeLabelDetails.recipeSpec
public class GraphNodeLabelParser {
    private static final String LABEL_PATH_SEPARATOR = "/";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Optional<String> parseVersionFromLabel(String label) {
        Optional<GraphNodeLabelDetails> labelDetails = parseLabelParts(label);
        if (labelDetails.isPresent()) {
            return Optional.of(labelDetails.get().getVersion());
        }
        return Optional.empty();
    }

    public Optional<String> parseLayerFromLabel(String label, Set<String> knownLayerNames) {
        Optional<GraphNodeLabelDetails> labelDetails = parseLabelParts(label);
        if (!labelDetails.isPresent()) {
            return Optional.empty();
        }
        String recipeSpec = labelDetails.get().getRecipeSpec();
        for (String candidateLayerName : knownLayerNames) {
            String possibleLayerPathSubstring = LABEL_PATH_SEPARATOR + candidateLayerName + LABEL_PATH_SEPARATOR;
            if (recipeSpec.contains(possibleLayerPathSubstring)) {
                return Optional.of(candidateLayerName);
            }
        }
        logger.warn("Graph Node recipe '%s' does not correspond to any known layer (%s)", label, knownLayerNames);
        return Optional.empty();
    }

    @NotNull
    private Optional<GraphNodeLabelDetails> parseLabelParts(final String label) {
        String[] labelParts = label.split("\\\\n:|\\\\n");
        if (labelParts.length < 3) {
            logger.warn("Error parsing Graph Node label '%s'; unexpected format.", label);
            return Optional.empty();
        }
        return Optional.of(new GraphNodeLabelDetails(labelParts[0], labelParts[1], labelParts[2]));
    }
}
