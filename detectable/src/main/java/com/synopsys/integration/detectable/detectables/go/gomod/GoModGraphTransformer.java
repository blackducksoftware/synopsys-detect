package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;

public class GoModGraphTransformer {
    private GoModCommandExecutor goModCommandExecutor;
    private Gson gson = BlackDuckServicesFactory.createDefaultGsonBuilder().setPrettyPrinting().setLenient().create();
    private ReplacementDataExtractor replacementDataExtractor = new ReplacementDataExtractor(gson);

    public GoModGraphTransformer(final GoModCommandExecutor goModCommandExecutor) {
        this.goModCommandExecutor = goModCommandExecutor;
    }

    public List<String> generateGoModGraphOutput(File directory, File goExe, List<String> listUJsonOutput) throws ExecutableRunnerException, DetectableException {
        List<String> modGraphOutput = goModCommandExecutor.execute(directory, goExe, "Querying for the go mod graph failed:", "mod", "graph");
        if (!listUJsonOutput.isEmpty()) {
            return modGraphOutputWithReplacements(modGraphOutput, listUJsonOutput);
        } else {
            return modGraphOutput;
        }
    }

    private List<String> modGraphOutputWithReplacements(List<String> modGraphOutput, List<String> listUJsonOutput) throws DetectableException {
        Map<String, String> replacementData = replacementDataExtractor.extractReplacementData(listUJsonOutput);

        for (String line : modGraphOutput) {
            int indexOfLine = modGraphOutput.indexOf(line);
            boolean hasBeenModified = false;
            for (Map.Entry<String, String> replacement : replacementData.entrySet()) {
                String newLine;
                boolean shouldModify;
                if (hasBeenModified) {
                    newLine = modGraphOutput.get(indexOfLine).replace(replacement.getKey(), replacement.getValue());
                    shouldModify = !modGraphOutput.get(indexOfLine).equals(newLine);
                } else {
                    newLine = line.replace(replacement.getKey(), replacement.getValue());
                    shouldModify = !line.equals(newLine);
                }
                if (shouldModify) {
                    modGraphOutput.set(indexOfLine, newLine);
                    hasBeenModified = true;
                }
            }
        }
        return modGraphOutput;
    }

}
