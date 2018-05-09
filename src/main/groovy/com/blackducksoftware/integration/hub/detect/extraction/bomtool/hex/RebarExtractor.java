package com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction.ExtractionResult;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex.parse.Rebar3TreeParser;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.hex.parse.RebarParseResult;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class RebarExtractor extends Extractor<RebarContext> {

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableRunner executableRunner;

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    Rebar3TreeParser rebarTreeParser;

    @Override
    public Extraction extract(final RebarContext context) {
        try {
            final List<DetectCodeLocation> codeLocations = new ArrayList<>();

            final Map<String, String> envVars = new HashMap<>();
            envVars.put("REBAR_COLOR", "none");

            final List<String> arguments = new ArrayList<>();
            arguments.add("tree");

            final Executable rebar3TreeExe = new Executable(context.directory, envVars, context.rebarExe.toString(), arguments);
            final List<String> output = executableRunner.execute(rebar3TreeExe).getStandardOutputAsList();
            final RebarParseResult parseResult = rebarTreeParser.parseRebarTreeOutput(output, context.directory.toString());

            codeLocations.add(parseResult.codeLocation);

            return new Extraction(ExtractionResult.Success, codeLocations);
        } catch (final Exception e) {
            return new Extraction(ExtractionResult.Exception, e);
        }
    }

}
