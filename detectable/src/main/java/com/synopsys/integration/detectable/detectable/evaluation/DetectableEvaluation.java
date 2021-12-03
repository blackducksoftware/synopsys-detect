package com.synopsys.integration.detectable.detectable.evaluation;

import java.util.Optional;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

public class DetectableEvaluation {
    public static final String NO_MESSAGE = "Unknown";

    private final Detectable detectable;
    private final DetectableEnvironment environment;

    private DetectableResult searchable;
    private DetectableResult applicable;
    private DetectableResult extractable;

    private ExtractionEnvironment extractionEnvironment;
    private Extraction extraction;

    public DetectableEvaluation(Detectable detectable, DetectableEnvironment environment) {
        this.detectable = detectable;
        this.environment = environment;
    }

    public void setExtraction(Extraction extraction) {
        this.extraction = extraction;
    }

    public Extraction getExtraction() {
        return extraction;
    }

    public void setExtractionEnvironment(ExtractionEnvironment extractionEnvironment) {
        this.extractionEnvironment = extractionEnvironment;
    }

    public ExtractionEnvironment getExtractionEnvironment() {
        return extractionEnvironment;
    }

    public boolean wasExtractionSuccessful() {
        return isExtractable() && this.extraction != null && this.extraction.isSuccess();
    }

    public Detectable getDetectable() {
        return detectable;
    }

    public DetectableEnvironment getEnvironment() {
        return environment;
    }

    public void setSearchable(DetectableResult searchable) {
        this.searchable = searchable;
    }

    public boolean isSearchable() {
        return this.searchable != null && this.searchable.getPassed();
    }

    public String getSearchabilityMessage() {
        return getBomToolResultDescription(searchable).orElse(NO_MESSAGE);
    }

    public void setApplicable(DetectableResult applicable) {
        this.applicable = applicable;
    }

    public boolean isApplicable() {
        return isSearchable() && this.applicable != null && this.applicable.getPassed();
    }

    public String getApplicabilityMessage() {
        return getBomToolResultDescription(applicable).orElse(NO_MESSAGE);
    }

    public void setExtractable(DetectableResult extractable) {
        this.extractable = extractable;
    }

    public boolean isExtractable() {
        return isApplicable() && this.extractable != null && this.extractable.getPassed();
    }

    public String getExtractabilityMessage() {
        return getBomToolResultDescription(extractable).orElse(NO_MESSAGE);
    }

    private Optional<String> getBomToolResultDescription(DetectableResult detectableResult) {
        String description = null;

        if (detectableResult != null) {
            description = detectableResult.toDescription();
        }

        return Optional.ofNullable(description);
    }

}
