package com.blackducksoftware.integration.hub.detect.workflow.diagnostic.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.workflow.bomtool.BomToolEvaluation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;

public class CodeLocationReporter {

    public void writeCodeLocationReport(final DiagnosticReportWriter writer, final DiagnosticReportWriter writer2, final List<BomToolEvaluation> bomToolEvaluations, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        for (final BomToolEvaluation evaluation : bomToolEvaluations) {
            if (evaluation.isExtractable()) {
                writeCodeLocationDetails(writer, writer2, evaluation, codeLocationNameMap);
            }
        }
    }

    private void writeCodeLocationDetails(final DiagnosticReportWriter writer, final DiagnosticReportWriter writer2, final BomToolEvaluation evaluation, final Map<DetectCodeLocation, String> codeLocationNameMap) {
        final Map<BomToolGroupType, Integer> dependencyCounts = new HashMap<>();
        for (final DetectCodeLocation codeLocation : evaluation.getExtraction().codeLocations) {
            writer.writeSeperator();
            writer.writeLine("Name : " + codeLocationNameMap.get(codeLocation));
            writer.writeLine("Directory : " + codeLocation.getSourcePath());
            writer.writeLine("Extraction : " + evaluation.getExtractionId().toUniqueString());
            writer.writeLine("Bom Tool : " + codeLocation.getBomToolType());
            writer.writeLine("Bom Tool Group : " + codeLocation.getBomToolGroupType());

            final DependencyGraph graph = codeLocation.getDependencyGraph();
            final Integer dependencyCount = countDependencies(new ArrayList<ExternalId>(), graph.getRootDependencyExternalIds(), graph);
            writer.writeLine("Root Dependencies : " + graph.getRootDependencies().size());
            writer.writeLine("Total Dependencies : " + dependencyCount);

            final BomToolGroupType group = codeLocation.getBomToolGroupType();
            if (!dependencyCounts.containsKey(group)) {
                dependencyCounts.put(group, 0);
            }
            dependencyCounts.put(group, dependencyCounts.get(group) + dependencyCount);
        }

        for (final BomToolGroupType group : dependencyCounts.keySet()) {
            final Integer count = dependencyCounts.get(group);

            writer2.writeLine(group.toString() + " : " + count);
        }
    }

    private int countDependencies(final List<ExternalId> processed, final Set<ExternalId> remaining, final DependencyGraph graph) {
        int sum = 0;
        for (final ExternalId dependency : remaining) {
            if (processed.contains(dependency)) {
                continue;
            }
            processed.add(dependency);
            sum += 1 + countDependencies(processed, graph.getChildrenExternalIdsForParent(dependency), graph);
        }
        return sum;
    }

}
