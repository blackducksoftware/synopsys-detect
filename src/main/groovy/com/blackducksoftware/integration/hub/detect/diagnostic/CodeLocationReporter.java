package com.blackducksoftware.integration.hub.detect.diagnostic;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.diagnostic.DiagnosticsManager.ReportTypes;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;


@Component
public class CodeLocationReporter {
    private final Logger logger = LoggerFactory.getLogger(Profiler.class);

    @Autowired
    public DiagnosticsManager diagnosticsManager;

    public void report(final List<DetectCodeLocation> codeLocations) {

        final DiagnosticReportWriter writer = diagnosticsManager.getReportWriter(ReportTypes.extractionProfile);


        for (final DetectCodeLocation codeLocation : codeLocations) {
            writer.writeLine("Detect Code Location");
            writer.writeLine("Root Dependencies: " + codeLocation.getDependencyGraph().getRootDependencies());
        }
    }

}
