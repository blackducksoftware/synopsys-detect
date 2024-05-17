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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    private final List<DetectTool> autoIncludedTools = new ArrayList<>();
    private final Map<Enum, Set<String>> scanTypeEvidenceMap = new HashMap<>();
    
    public void decide(boolean hasImageOrTar, DetectPropertyConfiguration detectConfiguration) {
        if (!hasImageOrTar && detectConfiguration.getValue(DetectProperties.DETECT_AUTONOMOUS_SCAN_ENABLED)) {
            Path detectSourcePath = detectConfiguration.getPathOrNull(DetectProperties.DETECT_SOURCE_PATH);
            AllNoneEnumCollection<DetectTool> includedTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS);
            AllNoneEnumCollection<DetectTool> excludedTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS_EXCLUDED);
            if (detectSourcePath == null) {
                logger.error("Detect autonomous scan mode requires Detect Source Path (--detect.source.path) to be set.");
            } else {
                // This map has individual paths to files grouped under types.
                searchFileSystem(detectSourcePath);
                if (!excludedTools.containsValue(DetectTool.BINARY_SCAN)
                        && !includedTools.containsValue(DetectTool.BINARY_SCAN)
                        && !scanTypeEvidenceMap.get(DetectTool.BINARY_SCAN).isEmpty()) {
                    autoIncludedTools.add(DetectTool.BINARY_SCAN);
                }
                if (!excludedTools.containsValue(DetectTool.DETECTOR)
                        && !includedTools.containsValue(DetectTool.DETECTOR)
                        && !scanTypeEvidenceMap.get(DetectTool.DETECTOR).isEmpty()) {
                    autoIncludedTools.add(DetectTool.DETECTOR);
                }
                if (!excludedTools.containsValue(DetectTool.SIGNATURE_SCAN)
                        && !includedTools.containsValue(DetectTool.SIGNATURE_SCAN)
                        && includedTools.containsNone()) {
                    autoIncludedTools.add(DetectTool.SIGNATURE_SCAN);
                }
            }
        }
    }

    public List<DetectTool> getAutoIncludedTools() {
        return autoIncludedTools;
    }

    public Map<Enum, Set<String>> getScanTypeEvidenceMap() {
        return scanTypeEvidenceMap;
    }
    
    private final Set<String> avoid = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            ".gitattributes", 
            ".gitignore", 
            ".github", 
            ".git",
            "__MACOSX",
            ".DS_Store")));
    
    private final Set<String> binaryExtensions = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            ".bin", 
            ".exe", 
            ".out", 
            ".dll")));
    
    private final MediaTypeRegistry mediaTypeRegistry = MediaTypeRegistry.getDefaultRegistry();
    
    public boolean shouldAvoid(String name) {
        for (String includedExtension : binaryExtensions) {
            if (name.endsWith(includedExtension)) {
                return true;
            }
        }
        return avoid.contains(name);
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
    
    private void searchFileSystem(Path pathToSearch) {
        long t1 = System.currentTimeMillis();
        try {
            Files.walkFileTree(pathToSearch, new FileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException {
                    final String fileName = file.getFileName().toString().trim().toLowerCase();
                    if (!Files.isDirectory(file) && !shouldAvoid(fileName)) {
                        if (isBinary(file.toFile())) {
                            scanTypeEvidenceMap.computeIfAbsent(DetectTool.BINARY_SCAN, k -> new HashSet<>()).add(file.toAbsolutePath().toString());
                        } else {
                            scanTypeEvidenceMap.computeIfAbsent(DetectTool.DETECTOR, k -> new HashSet<>()).add(file.toAbsolutePath().toString());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    String directoryName = dir.getFileName().toString().trim().toLowerCase();
                    if(!shouldAvoid(directoryName)){
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }
                
                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.TERMINATE;
                }
                
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
                
            });
        } catch (IOException ex) {
            logger.error("Failure when attempting to locate build config files.", ex);
        } finally {
            logger.debug("Done. Seconds: {}", (System.currentTimeMillis()-t1)/1000D);
        }
    }
}