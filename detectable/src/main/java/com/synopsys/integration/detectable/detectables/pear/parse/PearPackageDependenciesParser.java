package com.synopsys.integration.detectable.detectables.pear.parse;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;
import com.synopsys.integration.exception.IntegrationException;

public class PearPackageDependenciesParser {
    private static final String START_TOKEN = "=========";

    public List<PackageDependency> parse(List<String> packageDependenciesLines) throws IntegrationException {
        List<PackageDependency> packageDependencies = new ArrayList<>();

        boolean started = false;
        for (String rawLine : packageDependenciesLines) {
            String line = rawLine.trim();

            if (!started) {
                started = line.startsWith(START_TOKEN);
                continue;
            } else if (StringUtils.isBlank(line) || line.startsWith("Required") || line.startsWith("REQUIRED")) {
                continue;
            }

            String[] entry = line.split(" +");
            if (entry.length < 3) {
                throw new IntegrationException("Unable to parse package-dependencies");
            }

            boolean required = BooleanUtils.toBoolean(entry[0]);
            String type = entry[1].trim();
            String[] namePieces = entry[2].split("/");
            String name = namePieces[namePieces.length - 1].trim();

            if ("Package".equalsIgnoreCase(type)) {
                PackageDependency packageDependency = new PackageDependency(name, required);
                packageDependencies.add(packageDependency);
            }
        }

        return packageDependencies;
    }
}
