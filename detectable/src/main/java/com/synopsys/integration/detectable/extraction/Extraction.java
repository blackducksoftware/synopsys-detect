package com.synopsys.integration.detectable.extraction;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.result.FailedDetectableResult;
import com.synopsys.integration.util.NameVersion;

public class Extraction {
    private final List<CodeLocation> codeLocations;
    private final List<File> relevantFiles;
    private final List<File> unrecognizedPaths;
    private final ExtractionResultType result;

    // If you're an error you might have one of these filled.
    private final Exception error;
    private final String description;

    private final String projectVersion;
    private final String projectName;
    private final Map<ExtractionMetadata<?>, Object> metaData;

    public static Extraction failure(String description) {
        return new Extraction.Builder().failure(description).build();
    }

    public static Extraction failure(FailedDetectableResult... failedDetectableResults) {
        return failure(Arrays.asList(failedDetectableResults));
    }

    public static Extraction failure(List<FailedDetectableResult> failedDetectableResults) {
        List<String> failureDescriptions = failedDetectableResults.stream()
            .map(FailedDetectableResult::toDescription)
            .collect(Collectors.toList());
        String description = StringUtils.joinWith(". In addition, ", failureDescriptions);
        return new Extraction.Builder().failure(description).build();
    }

    public static Extraction success(CodeLocation codeLocation) {
        return new Extraction.Builder().success(codeLocation).build();
    }

    public static Extraction success(List<CodeLocation> codeLocations) {
        return new Extraction.Builder().success(codeLocations).build();
    }

    private Extraction(Builder builder) {
        this.codeLocations = builder.codeLocations;
        this.result = builder.result;
        this.error = builder.error;
        this.description = builder.description;

        this.projectVersion = builder.projectVersion;
        this.projectName = builder.projectName;
        this.metaData = builder.metaData;
        this.relevantFiles = builder.relevantFiles;
        this.unrecognizedPaths = builder.unrecognizedPaths;

        if (result == null) {
            throw new IllegalArgumentException("An extraction requires a result type.");
        }
    }

    public <T> boolean hasMetadata(ExtractionMetadata<T> extractionMetadata) {
        return metaData.containsKey(extractionMetadata);
    }

    public <T> Optional<T> getMetaData(ExtractionMetadata<T> extractionMetadata) {
        if (hasMetadata(extractionMetadata)) {
            Class<T> clazz = extractionMetadata.getMetadataClass();
            Object value = metaData.get(extractionMetadata);
            if (value != null && clazz.isAssignableFrom(value.getClass())) {
                return Optional.of(clazz.cast(value));
            }
        }
        return Optional.empty();
    }

    public boolean isSuccess() {
        return this.result == ExtractionResultType.SUCCESS;
    }

    public List<CodeLocation> getCodeLocations() {
        return codeLocations;
    }

    public Exception getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public String getProjectName() {
        return projectName;
    }

    public ExtractionResultType getResult() {
        return result;
    }

    public List<File> getRelevantFiles() {
        return relevantFiles;
    }

    public List<File> getUnrecognizedPaths() {
        return unrecognizedPaths;
    }

    public static class Builder {
        private final List<CodeLocation> codeLocations = new ArrayList<>();
        private final List<File> relevantFiles = new ArrayList<>();
        private final List<File> unrecognizedPaths = new ArrayList<>();
        private ExtractionResultType result;
        private Exception error;
        private String description;

        private String projectVersion;
        private String projectName;
        private final Map<ExtractionMetadata<?>, Object> metaData = new HashMap<>();

        public Builder projectName(String projectName) {
            this.projectName = projectName;
            return this;
        }

        public Builder projectVersion(String projectVersion) {
            this.projectVersion = projectVersion;
            return this;
        }

        public Builder nameVersion(NameVersion nameVersion) {
            this.projectName(nameVersion.getName());
            this.projectVersion(nameVersion.getVersion());
            return this;
        }

        public Builder nameVersionIfPresent(Optional<NameVersion> nameVersion) {
            nameVersion.ifPresent(this::nameVersion);
            return this;
        }

        public Builder codeLocations(CodeLocation codeLocation) {
            codeLocations.add(codeLocation);
            return this;
        }

        public Builder codeLocations(List<CodeLocation> codeLocation) {
            codeLocations.addAll(codeLocation);
            return this;
        }

        public Builder success(CodeLocation codeLocation) {
            this.codeLocations(codeLocation);
            this.success();
            return this;
        }

        public Builder success(List<CodeLocation> codeLocation) {
            this.codeLocations(codeLocation);
            this.success();
            return this;
        }

        public Builder success() {
            this.result = ExtractionResultType.SUCCESS;
            return this;
        }

        public Builder failure(String description) {
            this.result = ExtractionResultType.FAILURE;
            this.description = description;
            return this;
        }

        public Builder exception(Exception error) {
            this.result = ExtractionResultType.EXCEPTION;
            this.error = error;
            return this;
        }

        public <T> Builder metaData(ExtractionMetadata<T> key, T value) {
            this.metaData.put(key, value);
            return this;
        }

        public Builder relevantFiles(File... files) {
            return this.relevantFiles(Arrays.asList(files));
        }

        public Builder relevantFiles(Collection<File> files) {
            this.relevantFiles.addAll(files);
            return this;
        }

        public Builder unrecognizedPaths(File... files) {
            return this.unrecognizedPaths(Arrays.asList(files));
        }

        public Builder unrecognizedPaths(Collection<File> files) {
            this.unrecognizedPaths.addAll(files);
            return this;
        }

        public Extraction build() {
            return new Extraction(this);
        }

    }

    public enum ExtractionResultType {
        SUCCESS,
        FAILURE,
        EXCEPTION
    }
}
