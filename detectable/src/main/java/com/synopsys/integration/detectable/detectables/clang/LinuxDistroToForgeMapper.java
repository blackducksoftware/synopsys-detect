package com.synopsys.integration.detectable.detectables.clang;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.synopsys.integration.bdio.model.Forge;

// This might move into the libraries; it's also used by by hub-imageinspector-lib
public class LinuxDistroToForgeMapper {
    private static final String REDHAT_KB_NAME = "redhat";
    private static final String REDHAT_DISTRO_NAME = "rhel";

    private static final String OPENSUSE_KB_NAME = "opensuse";
    private static final String OPENSUSE_DISTRO_NAME1 = "sles";
    private static final String OPENSUSE_DISTRO_NAME2 = OPENSUSE_KB_NAME;

    private static final String AMAZON_KB_NAME = "centos";
    private static final String AMAZON_DISTRO_NAME = "amzn";

    // For cases where the KB name does not match the Linux distro ID found in os-release/lsb-release,
    // this table provides the mapping.
    // If the KB name matches the Linux distro ID found in os-release/lsb-release, there is
    // no need to add the distro to this table.
    // Linux distro names are mapped to lowercase before being looked up in this table.
    // The lookup is a "starts with" comparison, so a key of "opensuse" matches any Linux distro
    // name that starts with "opensuse" (case INsensitive).
    private static final Map<String, String> linuxDistroNameToKbForgeNameMapping = new HashMap<>();

    static {
        linuxDistroNameToKbForgeNameMapping.put(REDHAT_DISTRO_NAME, REDHAT_KB_NAME);
        linuxDistroNameToKbForgeNameMapping.put(OPENSUSE_DISTRO_NAME1, OPENSUSE_KB_NAME);
        linuxDistroNameToKbForgeNameMapping.put(OPENSUSE_DISTRO_NAME2, OPENSUSE_KB_NAME);
        linuxDistroNameToKbForgeNameMapping.put(AMAZON_DISTRO_NAME, AMAZON_KB_NAME);
    }

    public Forge createPreferredAliasNamespaceForge(String linuxDistroName) {
        String linuxDistroNameLowerCase = linuxDistroName.toLowerCase();
        Optional<String> overriddenKbName = findMatch(linuxDistroNameLowerCase);
        String kbName = overriddenKbName.orElse(linuxDistroNameLowerCase);
        return new Forge("/", kbName, true);
    }

    private Optional<String> findMatch(String linuxDistroNameLowerCase) {
        for (Map.Entry<String, String> mappingEntry : linuxDistroNameToKbForgeNameMapping.entrySet()) {
            if (linuxDistroNameLowerCase.startsWith(mappingEntry.getKey().toLowerCase())) {
                return Optional.of(mappingEntry.getValue());
            }
        }
        return Optional.empty();
    }
}
