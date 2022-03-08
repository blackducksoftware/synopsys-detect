package com.synopsys.integration.detectable.detectables.conan;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNode;

public class ConanExternalIdVersionGenerator {

    public static String generateExternalIdVersionString(ConanNode<String> node, boolean preferLongFormExternalIds) throws DetectableException {
        String externalIdVersion;
        if (hasValue(node.getRecipeRevision().orElse(null)) &&
            hasValue(node.getPackageRevision().orElse(null)) &&
            preferLongFormExternalIds) {
            // generate long form
            // <name>/<version>@<user>/<channel>#<recipe_revision>:<package_id>#<package_revision>
            externalIdVersion = String.format(
                "%s@%s/%s#%s:%s#%s",
                node.getVersion().orElseThrow(() -> new DetectableException(String.format("Missing dependency version: %s", node))),
                node.getUser().orElse("_"),
                node.getChannel().orElse("_"),
                node.getRecipeRevision().get(),
                node.getPackageId().orElse("0"),
                node.getPackageRevision().get()
            );
        } else {
            // generate short form
            // <name>/<version>@<user>/<channel>#<recipe_revision>
            externalIdVersion = String.format(
                "%s@%s/%s#%s",
                node.getVersion().orElseThrow(() -> new DetectableException(String.format("Missing dependency version: %s", node))),
                node.getUser().orElse("_"),
                node.getChannel().orElse("_"),
                node.getRecipeRevision().orElse("0")
            );
        }
        return externalIdVersion;
    }

    private static boolean hasValue(String value) {
        return value != null && !"None".equals(value);
    }
}
