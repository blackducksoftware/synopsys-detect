package com.blackduck.integration.detectable.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.blackduck.integration.bdio.model.BdioId;
import com.blackduck.integration.util.NameVersion;

public class GraphSummary {
    public Set<BdioId> rootExternalDataIds = new HashSet<>();
    public Map<BdioId, Set<BdioId>> externalDataIdRelationships = new HashMap<>();
    public Map<BdioId, NameVersion> dependencySummaries = new HashMap<>();

}
