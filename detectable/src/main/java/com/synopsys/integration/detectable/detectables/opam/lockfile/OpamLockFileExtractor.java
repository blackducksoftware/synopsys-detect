package com.synopsys.integration.detectable.detectables.opam.lockfile;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.opam.lockfile.parse.OpamLockFileParser;
import com.synopsys.integration.detectable.detectables.opam.parse.OpamFileParser;
import com.synopsys.integration.detectable.detectables.opam.parse.OpamParsedResult;
import com.synopsys.integration.detectable.detectables.opam.transform.OpamGraphTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.Extraction.Builder;
import com.synopsys.integration.executable.ExecutableRunnerException;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.io.File;
import java.util.Optional;

public class OpamLockFileExtractor {

    private final OpamGraphTransformer opamGraphTransformer;

    public OpamLockFileExtractor(OpamGraphTransformer opamGraphTransformer) {
        this.opamGraphTransformer = opamGraphTransformer;
    }

    public Extraction extract(List<File> opamFiles, String rootProjectName, OpamLockFileParser opamLockFileParser) throws ExecutableRunnerException {
        try {
            OpamFileParser opamFileParser = new OpamFileParser();

            Map<String, String> opamLockDependencies = opamLockFileParser.parse(); // parse lock file to get dependencies with correct version

            List<OpamParsedResult> opamParsedResults = new ArrayList<>();

            for(File opamFile: opamFiles) {
                OpamParsedResult opamParsedResult = opamFileParser.parse(opamFile); // parse normal opam file to get direct dependencies
                opamParsedResult.setLockFileDependencies(opamLockDependencies);
                opamParsedResults.add(opamParsedResult);
            }

            List<CodeLocation> codeLocations = new ArrayList<>();

            for(OpamParsedResult opamParsedResult: opamParsedResults) {
                DependencyGraph dependencyGraph = opamGraphTransformer.transform(null, opamParsedResult);
                codeLocations.add(new CodeLocation(dependencyGraph));
            }

            Builder builder = new Builder();
            builder.success(codeLocations);

            Optional<OpamParsedResult> resultWithVersion = Bds.of(opamParsedResults).firstFiltered(result -> !result.getProjectName().equals(rootProjectName));

            resultWithVersion.ifPresent(result -> builder.projectName(result.getProjectName()));
            resultWithVersion.ifPresent(result -> builder.projectVersion(result.getProjectVersion()));

            return builder.build();
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
