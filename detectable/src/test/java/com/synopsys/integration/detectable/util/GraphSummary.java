package com.synopsys.integration.detectable.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.synopsys.integration.bdio.model.BdioId;
import com.synopsys.integration.util.NameVersion;

public class GraphSummary {
    public Set<BdioId> rootExternalDataIds = new HashSet<>();
    public Map<BdioId, Set<BdioId>> externalDataIdRelationships = new HashMap<>();
    public Map<BdioId, NameVersion> dependencySummaries = new HashMap<>();

}
