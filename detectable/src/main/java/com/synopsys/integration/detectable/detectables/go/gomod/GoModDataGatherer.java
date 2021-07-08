package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoGraphRelationship;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListAllData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListModule;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoGraphParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoListParser;
import com.synopsys.integration.detectable.detectables.go.gomod.parse.GoModWhyParser;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class GoModDataGatherer {
    public static class GoModData {
        public final List<GoListModule> goListModules;
        public final List<GoListAllData> goListAllData;
        public final List<GoGraphRelationship> goGraphRelationships;
        public final Set<String> moduleExclusions;

        private GoModData(List<GoListModule> goListModules, List<GoListAllData> goListAllData, List<GoGraphRelationship> goGraphRelationships, Set<String> moduleExclusions) {
            this.goListModules = goListModules;
            this.goListAllData = goListAllData;
            this.goGraphRelationships = goGraphRelationships;
            this.moduleExclusions = moduleExclusions;
        }
    }

    private final GoModCommandExecutor goModCommandExecutor;
    private final GoListParser goListParser;
    private final GoGraphParser goGraphParser;
    private final GoModWhyParser goModWhyParser;

    public GoModDataGatherer(GoModCommandExecutor goModCommandExecutor, GoListParser goListParser, GoGraphParser goGraphParser, GoModWhyParser goModWhyParser) {
        this.goModCommandExecutor = goModCommandExecutor;
        this.goListParser = goListParser;
        this.goGraphParser = goGraphParser;
        this.goModWhyParser = goModWhyParser;
    }

    public GoModData gatherGoModData(File directory, ExecutableTarget goExe, boolean dependencyVerificationEnabled) throws ExecutableRunnerException, DetectableException {
        List<String> listOutput = goModCommandExecutor.generateGoListOutput(directory, goExe);
        List<GoListModule> goListModules = goListParser.parseGoListModuleJsonOutput(listOutput);

        List<String> listAllOutput = goModCommandExecutor.generateGoListUJsonOutput(directory, goExe);
        List<GoListAllData> goListAllData = goListParser.parseGoListAllJsonOutput(listAllOutput);

        List<String> modGraphOutput = goModCommandExecutor.generateGoModGraphOutput(directory, goExe);
        List<GoGraphRelationship> goGraphRelationships = goGraphParser.parseRelationshipsFromGoModGraph(modGraphOutput);

        Set<String> moduleExclusions = Collections.emptySet();
        if (dependencyVerificationEnabled) {
            List<String> modWhyOutput = goModCommandExecutor.generateGoModWhyOutput(directory, goExe);
            moduleExclusions = goModWhyParser.createModuleExclusionList(modWhyOutput);
        }

        return new GoModData(goListModules, goListAllData, goGraphRelationships, moduleExclusions);
    }
}
