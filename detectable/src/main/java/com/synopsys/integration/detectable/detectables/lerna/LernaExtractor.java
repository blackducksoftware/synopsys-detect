package com.synopsys.integration.detectable.detectables.lerna;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.detectable.detectables.lerna.model.PackageJson;

public class LernaExtractor {
    private final ExecutableRunner executableRunner;
    private final FileFinder fileFinder;
    private final Gson gson;

    public LernaExtractor(final ExecutableRunner executableRunner, final FileFinder fileFinder, final Gson gson) {
        this.executableRunner = executableRunner;
        this.fileFinder = fileFinder;
        this.gson = gson;
    }

    public Extraction extract(final File lernaExecutable, final ExtractionEnvironment extractionEnvironment) {
        try {
            final List<LernaPackage> lernaPackages = determineLernaPackages(extractionEnvironment.getOutputDirectory(), lernaExecutable);
            final List<PackageJson> packageJsons = parsePackageJsonFiles(extractionEnvironment.getOutputDirectory(), lernaPackages);

            // TODO: Match components in the packageJsons with finalized versions in package-lock.json (npm) or yarn.lock (yarn).
            
            return null;
        } catch (final ExecutableRunnerException | FileNotFoundException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private List<LernaPackage> determineLernaPackages(final File workingDirectory, final File lernaExecutable) throws ExecutableRunnerException {
        final ExecutableOutput lernaLsExecutableOutput = executableRunner.execute(workingDirectory, lernaExecutable, "ls", "--all", "--json");
        final String lernaLsOutput = lernaLsExecutableOutput.getStandardOutput();

        final Type lernaPackageListType = new TypeToken<ArrayList<LernaPackage>>() {}.getType();

        return gson.fromJson(lernaLsOutput, lernaPackageListType);
    }

    private List<PackageJson> parsePackageJsonFiles(final File workingDirectory, final List<LernaPackage> lernaPackages) throws FileNotFoundException {
        final List<PackageJson> packageJsons = new ArrayList<>();

        for (final LernaPackage lernaPackage : lernaPackages) {
            final File lernaPackageDirectory = new File(workingDirectory.getParent(), lernaPackage.getLocation());
            final File packageJsonFile = fileFinder.findFile(lernaPackageDirectory, "package.json");

            if (packageJsonFile == null) {
                throw new FileNotFoundException(String.format("Missing package.json file in %s", lernaPackageDirectory.getAbsolutePath()));
            }

            final InputStream packageJsonInputStream = new FileInputStream(packageJsonFile);
            final Reader packageJsonReader = new InputStreamReader(packageJsonInputStream);
            final PackageJson packageJson = gson.fromJson(packageJsonReader, PackageJson.class);

            packageJsons.add(packageJson);
        }

        return packageJsons;
    }
}
