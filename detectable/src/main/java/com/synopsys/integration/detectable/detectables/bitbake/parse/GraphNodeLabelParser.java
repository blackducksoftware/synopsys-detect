package com.synopsys.integration.detectable.detectables.bitbake.parse;

import java.util.Set;

import org.jetbrains.annotations.NotNull;

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

    public String parseVersionFromLabel(String label) throws IntegrationException {
        GraphNodeLabelDetails labelDetails = parseLabelParts(label);
        return labelDetails.getVersion();
    }

    public String parseLayerFromLabel(String label, Set<String> knownLayerNames) throws IntegrationException {
        GraphNodeLabelDetails labelDetails = parseLabelParts(label);
        String recipeSpec = labelDetails.getRecipeSpec();
        for (String candidateLayerName : knownLayerNames) {
            String possibleLayerPathSubstring = LABEL_PATH_SEPARATOR + candidateLayerName + LABEL_PATH_SEPARATOR;
            if (recipeSpec.contains(possibleLayerPathSubstring)) {
                return candidateLayerName;
            }
        }
        throw new IntegrationException(String.format("Graph Node recipe '%s' does not correspond to any known layer (%s)", label, knownLayerNames));
    }

    @NotNull
    private GraphNodeLabelDetails parseLabelParts(final String label) throws IntegrationException {
        String[] labelParts = label.split("\\\\n:|\\\\n");
        if (labelParts.length < 3) {
            throw new IntegrationException(String.format("Error parsing Graph Node label '%s'; unexpected format.", label));
        }
        return new GraphNodeLabelDetails(labelParts[0], labelParts[1], labelParts[2]);
    }
}
