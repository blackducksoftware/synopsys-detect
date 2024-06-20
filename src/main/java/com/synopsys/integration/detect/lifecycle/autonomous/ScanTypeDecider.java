package com.synopsys.integration.detect.lifecycle.autonomous;

import com.synopsys.integration.configuration.property.types.enumallnone.list.AllNoneEnumCollection;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MediaTypeRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScanTypeDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public Map<DetectTool, Set<String>> decide(boolean hasImageOrTar, DetectPropertyConfiguration detectConfiguration, Path detectSourcePath) {
        if (!hasImageOrTar && detectConfiguration.getValue(DetectProperties.DETECT_AUTONOMOUS_SCAN_ENABLED)) {
            AllNoneEnumCollection<DetectTool> includedTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS);
            AllNoneEnumCollection<DetectTool> excludedTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS_EXCLUDED);
            if (detectSourcePath == null) {
                logger.error("Detect autonomous scan mode requires Detect Source Path (--detect.source.path) to be set.");
            } else {
                // This map has individual paths to files grouped under types.
                PathsCollection pathsCollection = search(detectSourcePath);
                logger.debug("includedTools: {}", includedTools.toPresentValues());
                logger.debug("excludedTools: {}", excludedTools.toPresentValues());
                final Map<DetectTool, Set<String>> scanTypeEvidenceMap = new HashMap<>();
                decideTool(scanTypeEvidenceMap, pathsCollection.binaryPaths, includedTools, excludedTools, DetectTool.BINARY_SCAN);
                decideTool(scanTypeEvidenceMap, pathsCollection.detectorPaths, includedTools, excludedTools, DetectTool.DETECTOR);
                decideTool(scanTypeEvidenceMap, pathsCollection.signaturePaths, includedTools, excludedTools, DetectTool.SIGNATURE_SCAN);
                return scanTypeEvidenceMap;
            }
        }
        return Collections.EMPTY_MAP;
    }

    private void decideTool(Map<DetectTool, Set<String>> scanTypeEvidenceMap,
            Set<String> pathsForTool,
            AllNoneEnumCollection<DetectTool> includedTools, 
            AllNoneEnumCollection<DetectTool> excludedTools,
            DetectTool candidateTool) {
        if (!excludedTools.containsValue(candidateTool)
                && (includedTools.containsValue(candidateTool)
                || includedTools.isEmpty()
                || includedTools.containsAll())
                && !pathsForTool.isEmpty()) {
            scanTypeEvidenceMap.put(candidateTool, pathsForTool);
        }
    }

    private final Set<String> avoidAbsolutely = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            ".gitattributes", 
            ".gitignore", 
            ".github", 
            ".git",
            ".gradle",
            ".idea",
            "__MACOSX")));
    
    /**
     * This should be replaced with an intuitive binary reader that can zip and upload a stream of image formats
     * since a malicious binary can be embedded in an image file. The original paths to the 
     * individual image files should be stored in a text file for the BDBA worked to 
     * reference in its report.
     */
    private final Set<String> ignoreReluctantly = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            ".png", 
            ".gif", 
            ".ico",
            ".bmp",
            ".jpeg",
            ".jpg",
            ".class",
            ".DS_Store",
            ".bdio",
            ".txt",
            ".java",
            ".manifest")));
    
    private final MediaTypeRegistry mediaTypeRegistry = MediaTypeRegistry.getDefaultRegistry();
    
    private boolean shouldAvoidDirectory(String name) {
        return avoidAbsolutely.contains(name);
    }

    private boolean isEligibleFile(String name, long size) {
        if (size<=0L) {
            return false;
        }
        for(String extension : ignoreReluctantly) {
            if (name.toLowerCase().endsWith(extension)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isBinary(File file) throws IOException {
        Set<MediaType> mediaTypes = new HashSet<>();
        MediaType mediaType = MediaType.parse(new Tika().detect(new ByteArrayInputStream(FileUtils.readFileToByteArray(file))));
        while(mediaType != null) {
            mediaTypes.addAll(mediaTypeRegistry.getAliases(mediaType));
            mediaTypes.add(mediaType);
            mediaType = mediaTypeRegistry.getSupertype(mediaType);
        }
        return mediaTypes.stream().noneMatch(x -> x.getType().equals("text"));
    }
    
    private PathsCollection search(Path pathToSearch) {
        PathsCollection pathsCollection = new PathsCollection();
        long t1 = System.currentTimeMillis();
        try {
            Files.walkFileTree(pathToSearch, new FileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    String fileName = path.getFileName().toString();
                    if (isEligibleFile(fileName, attrs.size())) {
                        if (isBinary(path.toFile())) {
                            pathsCollection.binaryPaths.add(path.toAbsolutePath().toString());
                        } else if (pathsCollection.detectorPaths.isEmpty()) {
                            pathsCollection.detectorPaths.add(pathToSearch.toAbsolutePath().toString());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    String directoryName = dir.getFileName().toString().trim().toLowerCase();
                    if(shouldAvoidDirectory(directoryName)){
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    logger.error(exc.getMessage());
                    return FileVisitResult.TERMINATE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
                
            });
            pathsCollection.signaturePaths.add(pathToSearch.toAbsolutePath().toString());
        } catch (IOException ex) {
            logger.error("Failure when attempting to locate build config files.", ex);
        } finally {
            logger.info("Search for binary files is done. Seconds: {}", (System.currentTimeMillis()-t1)/1000D);
        }
        return pathsCollection;
    }
    
    class PathsCollection {
        private final Set<String> binaryPaths = new HashSet<>();
        private final Set<String> detectorPaths = new HashSet<>(); 
        private final Set<String> signaturePaths = new HashSet<>();
    }
}