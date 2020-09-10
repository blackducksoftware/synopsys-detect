package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;

public class GoListUJsonGenerator {

    private final GoModCommandExecutor goModCommandExecutor;

    public GoListUJsonGenerator(final GoModCommandExecutor goModCommandExecutor) {
        this.goModCommandExecutor = goModCommandExecutor;
    }

    public List<String> generateListUJsonOutput(File directory, File goExe) throws ExecutableRunnerException, DetectableException {
        List<String> goVersionOutput = goModCommandExecutor.execute(directory, goExe, "Querying for the version failed: ", "version");
        Pattern pattern = Pattern.compile("\\d+\\.[\\d.]+");
        Matcher matcher = pattern.matcher(goVersionOutput.get(0));
        if (matcher.find()) {
            String version = matcher.group();
            String[] parts = version.split("\\.");
            if (Integer.parseInt(parts[0]) > 1 || Integer.parseInt(parts[1]) >= 14) {
                return goModCommandExecutor.execute(directory, goExe, "Querying for the go mod graph failed:", "list", "-mod=readonly", "-m", "-u", "-json", "all");
            } else {
                return goModCommandExecutor.execute(directory, goExe, "Querying for the go mod graph failed:", "list", "-m", "-u", "-json", "all");
            }
        }
        return new ArrayList<>();
    }
}
