package com.synopsys.integration.detectable.detectables.npm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.Discovery;
import com.synopsys.integration.detectable.detectables.npm.lockfile.parse.NpmLockfileParser;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class NpmPackageJsonDiscoverer {
    private final Logger logger = LoggerFactory.getLogger(NpmLockfileParser.class);
    private final Gson gson;

    public NpmPackageJsonDiscoverer(final Gson gson) {
        this.gson = gson;
    }

    public Discovery discover(File packageJson) {
        try {
            String packageJsonText = FileUtils.readFileToString(packageJson, StandardCharsets.UTF_8);
            PackageJson packageJsonModel = gson.fromJson(packageJsonText, PackageJson.class);
            return new Discovery.Builder().success(packageJsonModel.name, packageJsonModel.version).build();
        } catch (IOException e) {
            return new Discovery.Builder().exception(e).build();
        }
    }
}
