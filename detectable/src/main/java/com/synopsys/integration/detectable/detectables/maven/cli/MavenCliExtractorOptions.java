package com.synopsys.integration.detectable.detectables.maven.cli;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.synopsys.integration.common.util.parse.CommandParser;

public class MavenCliExtractorOptions {
    private final static String[] THREAD_SPECIFYING_ARGUMENT_PREFIXES = {
        "--threads",
        "-threads",
        "-T",
        "--T"
    };

    private final String mavenBuildCommand;
    private final List<String> mavenExcludedScopes;
    private final List<String> mavenIncludedScopes;
    private final List<String> mavenExcludedModules;
    private final List<String> mavenIncludedModules;

    public MavenCliExtractorOptions(
        String mavenBuildCommand,
        List<String> mavenExcludedScopes,
        List<String> mavenIncludedScopes,
        List<String> mavenExcludedModules,
        List<String> mavenIncludedModules
    ) {
        this.mavenBuildCommand = mavenBuildCommand;
        this.mavenExcludedScopes = mavenExcludedScopes;
        this.mavenIncludedScopes = mavenIncludedScopes;
        this.mavenExcludedModules = mavenExcludedModules;
        this.mavenIncludedModules = mavenIncludedModules;
    }

    public Optional<String> getMavenBuildCommand() {
        return Optional.ofNullable(mavenBuildCommand);
    }

    public List<String> buildCliArguments(CommandParser commandParser) {
        List<String> arguments = new ArrayList<>();
        List<String> passedArgs = commandParser.parseCommandString(getMavenBuildCommand().orElse(""));

        boolean omitArg = false;
        for (String arg : passedArgs) {
            if (omitArg || arg.equals("dependency:tree")) {
                omitArg = false;
                continue;
            }

            boolean isThreadSpecifier = false;

            for (String prefix : THREAD_SPECIFYING_ARGUMENT_PREFIXES) {
                if (arg.startsWith(prefix)) {
                    isThreadSpecifier = true;
                    omitArg = arg.length() == prefix.length(); // value can either be specified as part of the same argument or next
                    break;
                }
            }
            if (isThreadSpecifier)
                continue;
            arguments.add(arg);
        }
        arguments.add("dependency:tree");

        // Force maven to use a single thread to ensure the tree output is in the correct order.
        // When multiple threads are enabled the tree output is often unparseable.
        arguments.add("-T1");
        return arguments;
    }

    public List<String> getMavenExcludedScopes() {
        return mavenExcludedScopes;
    }

    public List<String> getMavenIncludedScopes() {
        return mavenIncludedScopes;
    }

    public List<String> getMavenExcludedModules() {
        return mavenExcludedModules;
    }

    public List<String> getMavenIncludedModules() {
        return mavenIncludedModules;
    }
}
