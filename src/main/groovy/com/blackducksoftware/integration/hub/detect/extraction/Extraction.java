package com.blackducksoftware.integration.hub.detect.extraction;

import java.util.ArrayList;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

public class Extraction {

    public List<DetectCodeLocation> codeLocations;
    public ExtractionResult result;
    public Exception error;
    public String description;

    public String projectVersion;
    public String projectName;

    private Extraction(final Builder builder) {
        this.codeLocations = builder.codeLocations;
        this.result = builder.result;
        this.error = builder.error;
        this.description = builder.description;

        this.projectVersion = builder.projectVersion;
        this.projectName = builder.projectName;
    }

    public static class Builder {
        private final List<DetectCodeLocation> codeLocations = new ArrayList<>();
        private ExtractionResult result;
        private Exception error;
        private String description;

        private String projectVersion;
        private String projectName;

        public Builder codeLocations(final DetectCodeLocation codeLocation) {
            codeLocations.add(codeLocation);
            return this;
        }
        public Builder codeLocations(final List<DetectCodeLocation> codeLocation) {
            codeLocations.addAll(codeLocation);
            return this;
        }
        public Builder success(final DetectCodeLocation codeLocation) {
            this.codeLocations(codeLocation);
            this.success();
            return this;
        }
        public Builder success(final List<DetectCodeLocation> codeLocation) {
            this.codeLocations(codeLocation);
            this.success();
            return this;
        }
        public Builder success() {
            this.result = ExtractionResult.Success;
            return this;
        }
        public Builder failure(final String description) {
            this.result = ExtractionResult.Success;
            this.description = description;
            return this;
        }
        public Builder exception(final Exception error) {
            this.result = ExtractionResult.Exception;
            this.error = error;
            return this;
        }
        public Extraction build() {
            return new Extraction(this);
        }

    }

    public enum ExtractionResult {
        Success,
        Failure,
        Exception
    }

}
