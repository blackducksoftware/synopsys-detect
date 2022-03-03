package com.synopsys.integration.detect.workflow.blackduck.developer.blackduck;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.compress.utils.FileNameUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.blackduck.api.generated.discovery.ApiDiscovery;
import com.synopsys.integration.blackduck.bdio2.Bdio2StreamUploader;
import com.synopsys.integration.blackduck.bdio2.model.BdioFileContent;
import com.synopsys.integration.blackduck.bdio2.util.Bdio2ContentExtractor;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.blackduck.service.BlackDuckApiClient;
import com.synopsys.integration.blackduck.service.DataService;
import com.synopsys.integration.blackduck.service.request.BlackDuckRequestBuilderEditor;
import com.synopsys.integration.detect.configuration.enumeration.RapidCompareMode;
import com.synopsys.integration.detect.util.DetectZipUtil;
import com.synopsys.integration.detect.workflow.blackduck.developer.RapidScanOptions;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.NameVersion;

public class RapidScanUploadService extends DataService {
    private static final String FILE_NAME_BDIO_HEADER_JSONLD = "bdio-header.jsonld";

    private final Bdio2ContentExtractor bdio2Extractor;
    private final RapidScanConfigBdio2StreamUploader bdio2Uploader;

    public RapidScanUploadService(
        BlackDuckApiClient blackDuckApiClient,
        ApiDiscovery apiDiscovery,
        IntLogger logger,
        Bdio2ContentExtractor bdio2Extractor,
        RapidScanConfigBdio2StreamUploader bdio2Uploader
    ) {
        super(blackDuckApiClient, apiDiscovery, logger);
        this.bdio2Extractor = bdio2Extractor;
        this.bdio2Uploader = bdio2Uploader;
    }

    public HttpUrl uploadFile(File workingDirectory, UploadTarget uploadTarget, RapidScanOptions rapidScanOptions, @Nullable File rapidScanConfig)
        throws IntegrationException, IOException {
        logger.debug(String.format("Uploading BDIO file %s", uploadTarget.getUploadFile()));
        List<BdioFileContent> bdioFileContentList = bdio2Extractor.extractContent(uploadTarget.getUploadFile());
        NameVersion projectNameVersion = uploadTarget.getProjectAndVersion().orElse(null);
        return uploadFiles(uploadTarget, bdioFileContentList, rapidScanOptions, projectNameVersion, rapidScanConfig, workingDirectory);
    }

    private HttpUrl uploadFiles(
        UploadTarget uploadTarget,
        List<BdioFileContent> bdioFiles,
        RapidScanOptions rapidScanOptions,
        @Nullable NameVersion nameVersion,
        @Nullable File rapidScanConfig,
        @Nullable File rapidScanWorkingDirectory
    )
        throws IntegrationException, IOException {
        if (bdioFiles.isEmpty()) {
            throw new IllegalArgumentException("BDIO files cannot be empty.");
        }
        BdioFileContent header = bdioFiles.stream()
            .filter(content -> content.getFileName().equals(FILE_NAME_BDIO_HEADER_JSONLD))
            .findFirst()
            .orElseThrow(() -> new BlackDuckIntegrationException("Cannot find BDIO header file" + FILE_NAME_BDIO_HEADER_JSONLD + "."));

        List<BdioFileContent> remainingFiles = bdioFiles.stream()
            .filter(content -> !content.getFileName().equals(FILE_NAME_BDIO_HEADER_JSONLD))
            .collect(Collectors.toList());
        int count = remainingFiles.size();
        logger.debug("BDIO upload file count = " + count);

        BlackDuckRequestBuilderEditor editor = builder -> {
            builder.addHeader(RapidCompareMode.HEADER_NAME, rapidScanOptions.getCompareMode().getHeaderValue());
            if (nameVersion != null) {
                builder
                    .addHeader(Bdio2StreamUploader.PROJECT_NAME_HEADER, nameVersion.getName())
                    .addHeader(Bdio2StreamUploader.VERSION_NAME_HEADER, nameVersion.getVersion());
            }
        };

        HttpUrl url;
        if (rapidScanConfig != null) {
            url = bdio2Uploader.startWithConfig(zip(uploadTarget, rapidScanConfig, header, rapidScanWorkingDirectory), editor);
        } else {
            url = bdio2Uploader.start(header, editor);
        }
        for (BdioFileContent content : remainingFiles) {
            bdio2Uploader.append(url, count, content, editor);
        }
        bdio2Uploader.finish(url, count, editor);

        return url;
    }

    private File zip(UploadTarget uploadTarget, File config, BdioFileContent header, File rapidScanWorkingDirectory) throws IOException {
        File target = new File(rapidScanWorkingDirectory, FileNameUtils.getBaseName(uploadTarget.getUploadFile().getName()) + ".zip");
        File bdioHeader = new File(rapidScanWorkingDirectory, FileNameUtils.getBaseName(uploadTarget.getUploadFile().getName()) + ".jsonld");

        FileUtils.writeStringToFile(bdioHeader, header.getContent(), Charset.defaultCharset());

        Map<String, Path> zipEntries = new HashMap<>();
        zipEntries.put(config.getName(), config.toPath());
        zipEntries.put(header.getFileName(), bdioHeader.toPath());
        DetectZipUtil.zip(target, zipEntries);

        return target;
    }

}
