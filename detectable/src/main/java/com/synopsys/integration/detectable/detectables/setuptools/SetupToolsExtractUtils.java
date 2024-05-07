package com.synopsys.integration.detectable.detectables.setuptools;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.tomlj.Toml;
import org.tomlj.TomlArray;
import org.tomlj.TomlParseResult;

import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectable.result.SetupToolsRequiresNotFoundDetectableResult;

public class SetupToolsExtractUtils {
    
    private static final String BUILD_KEY = "build-system.requires";
    private static final String REQUIRED_KEY = "setuptools";

    public static TomlParseResult extractToml(File projectToml) throws IOException {
        String projectTomlText = FileUtils.readFileToString(projectToml, StandardCharsets.UTF_8);

        return Toml.parse(projectTomlText);
    }

    public static DetectableResult checkTomlRequiresSetupTools(TomlParseResult parsedToml) {
        if (parsedToml != null) {
            TomlArray buildRequires = parsedToml.getArray(BUILD_KEY);

            if (buildRequires != null) {
                for (int i = 0; i < buildRequires.size(); i++) {
                    String requires = buildRequires.getString(i);

                    if (requires.equals(REQUIRED_KEY)) {
                        return new PassedDetectableResult();
                    }
                }
            }
        }

        return new SetupToolsRequiresNotFoundDetectableResult();
    }
}
