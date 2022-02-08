package com.synopsys.integration.detectable.detectables.yarn.packagejson;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.YarnLockDetectable;
import com.synopsys.integration.detectable.detectables.yarn.workspace.YarnWorkspace;

public class PackageJsonFiles {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PackageJsonReader packageJsonReader;

    public PackageJsonFiles(PackageJsonReader packageJsonReader) {
        this.packageJsonReader = packageJsonReader;
    }

    public NullSafePackageJson read(File packageJsonFile) throws IOException {
        logger.trace("\tReading package.json file: {}", packageJsonFile.getAbsolutePath());
        String packageJsonText = FileUtils.readFileToString(packageJsonFile, StandardCharsets.UTF_8);
        return packageJsonReader.read(packageJsonText);
    }

    @NotNull
    public Collection<YarnWorkspace> readWorkspacePackageJsonFiles(File workspaceDir) throws IOException {
        String forwardSlashedWorkspaceDirPath = deriveForwardSlashedPath(workspaceDir);
        File packageJsonFile = new File(workspaceDir, YarnLockDetectable.YARN_PACKAGE_JSON);
        List<String> workspaceDirPatterns = extractWorkspaceDirPatterns(packageJsonFile);

        Collection<YarnWorkspace> workspaces = new LinkedList<>();
        for (String workspaceSubdirPattern : workspaceDirPatterns) {
            logger.trace("workspaceSubdirPattern: {}", workspaceSubdirPattern);
            String globString = String.format("glob:%s/%s/package.json", forwardSlashedWorkspaceDirPath, workspaceSubdirPattern);
            logger.trace("workspace subdir globString: {}", globString);
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher(globString);
            Files.walkFileTree(workspaceDir.toPath(), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (matcher.matches(file)) {
                        logger.trace("\tFound a match: {}", file);
                        NullSafePackageJson packageJson = read(file.toFile());
                        Path rel = workspaceDir.toPath().relativize(file.getParent());
                        WorkspacePackageJson workspacePackageJson = new WorkspacePackageJson(file.toFile(), packageJson, rel.toString());
                        YarnWorkspace workspace = new YarnWorkspace(workspacePackageJson);
                        workspaces.add(workspace);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) {
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        if (!workspaceDirPatterns.isEmpty()) {
            logger.debug("Found {} matching workspace package.json files for workspaces listed in {}", workspaces.size(), packageJsonFile.getAbsolutePath());
        }
        return workspaces;
    }

    @NotNull
    private String deriveForwardSlashedPath(File file) {
        String forwardSlashWorkspaceDirPath;
        if (!File.separator.equals("/")) {
            forwardSlashWorkspaceDirPath = file.getAbsolutePath().replace(File.separator, "/");
        } else {
            forwardSlashWorkspaceDirPath = file.getAbsolutePath();
        }
        return forwardSlashWorkspaceDirPath;
    }

    @NotNull
    private List<String> extractWorkspaceDirPatterns(File packageJsonFile) throws IOException {
        String packageJsonText = FileUtils.readFileToString(packageJsonFile, StandardCharsets.UTF_8);
        return packageJsonReader.extractWorkspaceDirPatterns(packageJsonText);
    }
}
