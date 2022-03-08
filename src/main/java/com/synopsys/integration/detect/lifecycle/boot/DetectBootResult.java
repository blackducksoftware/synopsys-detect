package com.synopsys.integration.detect.lifecycle.boot;

import java.io.File;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;

public class DetectBootResult {
    private final BootType bootType;

    //No matter what type of boot occurs, the following should be provided to the best effort of the Boot.
    //Shutdown and cleanup will do best effort with the following dependencies.
    @Nullable
    private final PropertyConfiguration detectConfiguration;
    @Nullable
    private final DirectoryManager directoryManager;
    @Nullable
    private final ProductRunData productRunData;
    @Nullable
    private final File airGapZip;
    @Nullable
    private final DiagnosticSystem diagnosticSystem;

    //This should be populated if a run will be performed
    @Nullable
    private final BootSingletons bootSingletons;

    //And in the case of an exception, this should be populated so the proper exit code can be thrown.
    private final Exception exception;

    private DetectBootResult(
        BootType bootType,
        @Nullable PropertyConfiguration detectConfiguration,
        @Nullable DirectoryManager directoryManager,
        @Nullable File airGapZip,
        @Nullable DiagnosticSystem diagnosticSystem,
        @Nullable ProductRunData productRunData,
        @Nullable BootSingletons bootSingletons, @Nullable Exception exception
    ) {
        this.bootType = bootType;
        this.detectConfiguration = detectConfiguration;
        this.directoryManager = directoryManager;
        this.airGapZip = airGapZip;
        this.diagnosticSystem = diagnosticSystem;
        this.productRunData = productRunData;
        this.bootSingletons = bootSingletons;
        this.exception = exception;
    }

    public Optional<PropertyConfiguration> getDetectConfiguration() {
        return Optional.ofNullable(detectConfiguration);
    }

    public Optional<DirectoryManager> getDirectoryManager() {
        return Optional.ofNullable(directoryManager);
    }

    public Optional<DiagnosticSystem> getDiagnosticSystem() {
        return Optional.ofNullable(diagnosticSystem);
    }

    public Optional<ProductRunData> getProductRunData() {
        return Optional.ofNullable(productRunData);
    }

    public Optional<BootSingletons> getBootSingletons() {
        return Optional.ofNullable(bootSingletons);
    }

    public Optional<File> getAirGapZip() {
        return Optional.ofNullable(airGapZip);
    }

    public BootType getBootType() {
        return bootType;
    }

    public Optional<Exception> getException() {
        return Optional.ofNullable(exception);
    }

    @NotNull
    public Boolean shouldForceSuccess() {
        return getDetectConfiguration()
            .map(configuration -> configuration.getValueOrDefault(DetectProperties.DETECT_FORCE_SUCCESS))
            .orElse(Boolean.FALSE);
    }

    public enum BootType {
        EXIT,
        RUN,
        EXCEPTION
    }

    public static DetectBootResult run(
        BootSingletons bootSingletons,
        PropertyConfiguration detectConfiguration,
        ProductRunData productRunData,
        DirectoryManager directoryManager,
        @Nullable DiagnosticSystem diagnosticSystem
    ) {
        return new DetectBootResult(BootType.RUN, detectConfiguration, directoryManager, null, diagnosticSystem, productRunData, bootSingletons, null);
    }

    public static DetectBootResult exit(PropertyConfiguration detectConfiguration) {
        return new DetectBootResult(BootType.EXIT, detectConfiguration, null, null, null, null, null, null);
    }

    public static DetectBootResult exit(PropertyConfiguration detectConfiguration, DirectoryManager directoryManager, @Nullable DiagnosticSystem diagnosticSystem) {
        return new DetectBootResult(BootType.EXIT, detectConfiguration, directoryManager, null, diagnosticSystem, null, null, null);
    }

    public static DetectBootResult exit(PropertyConfiguration detectConfiguration, File airGapZip, DirectoryManager directoryManager, @Nullable DiagnosticSystem diagnosticSystem) {
        return new DetectBootResult(BootType.EXIT, detectConfiguration, directoryManager, airGapZip, diagnosticSystem, null, null, null);
    }

    public static DetectBootResult exception(Exception exception, PropertyConfiguration detectConfiguration) {
        return new DetectBootResult(BootType.EXCEPTION, detectConfiguration, null, null, null, null, null, exception);
    }

    public static DetectBootResult exception(
        Exception exception,
        PropertyConfiguration detectConfiguration,
        DirectoryManager directoryManager,
        @Nullable DiagnosticSystem diagnosticSystem
    ) {
        return new DetectBootResult(BootType.EXCEPTION, detectConfiguration, directoryManager, null, diagnosticSystem, null, null, exception);
    }

}
