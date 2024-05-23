package com.synopsys.integration.detect.lifecycle.boot.decision;

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

public class AutoScanTypeDecider {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public Map<DetectTool, Set<String>> decide(boolean hasImageOrTar, DetectPropertyConfiguration detectConfiguration) {
        if (!hasImageOrTar && detectConfiguration.getValue(DetectProperties.DETECT_AUTONOMOUS_SCAN_ENABLED)) {
            Path detectSourcePath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_SOURCE_PATH);
            AllNoneEnumCollection<DetectTool> includedTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS);
            AllNoneEnumCollection<DetectTool> excludedTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS_EXCLUDED);
            if (detectSourcePath == null) {
                logger.error("Detect autonomous scan mode requires Detect Source Path (--detect.source.path) to be set.");
            } else {
                // This map has individual paths to files grouped under types.
                PathsCollection pathsCollection = search(detectSourcePath);
                logger.info("includedTools: {}", includedTools.toPresentValues());
                logger.info("excludedTools: {}", excludedTools.toPresentValues());
                final Map<DetectTool, Set<String>> scanTypeEvidenceMap = new HashMap<>();
                if (!excludedTools.containsValue(DetectTool.BINARY_SCAN)
                        && !includedTools.containsValue(DetectTool.BINARY_SCAN)
                        && !pathsCollection.binaryPaths.isEmpty()) {
                    scanTypeEvidenceMap.put(DetectTool.BINARY_SCAN, pathsCollection.binaryPaths);
                }
                if (!excludedTools.containsValue(DetectTool.DETECTOR)
                        && !includedTools.containsValue(DetectTool.DETECTOR)
                        && !pathsCollection.detectorPaths.isEmpty()) {
                    scanTypeEvidenceMap.put(DetectTool.DETECTOR, pathsCollection.detectorPaths);
                }
                if (!excludedTools.containsValue(DetectTool.SIGNATURE_SCAN)
                        && !includedTools.containsValue(DetectTool.SIGNATURE_SCAN)
                        && !pathsCollection.signaturePaths.isEmpty()) {
                    scanTypeEvidenceMap.put(DetectTool.SIGNATURE_SCAN, pathsCollection.signaturePaths);
                }
                return scanTypeEvidenceMap;
            }
        }
        return Collections.EMPTY_MAP;
    }
    
    private final Set<String> avoidAbsolutley = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            ".gitattributes", 
            ".gitignore", 
            ".github", 
            ".git",
            "__MACOSX",
            ".DS_Store")));
    
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
            ".jpg")));
    
    private final MediaTypeRegistry mediaTypeRegistry = MediaTypeRegistry.getDefaultRegistry();
    
    private boolean shouldAvoidDirectory(String name) {
        return avoidAbsolutley.contains(name);
    }
    
    private boolean isEligibleFile(String name) {
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
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (isEligibleFile(file.toFile().getName())) {
                        if (isBinary(file.toFile())) {
                            pathsCollection.binaryPaths.add(file.toAbsolutePath().toString());
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
            logger.debug("Done. Seconds: {}", (System.currentTimeMillis()-t1)/1000D);
        }
        return pathsCollection;
    }
    
    class PathsCollection {
        private final Set<String> binaryPaths = new HashSet<>();
        private final Set<String> detectorPaths = new HashSet<>(); 
        private final Set<String> signaturePaths = new HashSet<>();
    }
}