package com.synopsys.integration.detect.util.bdio.protobuf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.zip.ZipOutputStream;

import com.blackducksoftware.bdio.proto.ProtobufBdioWriter;
import com.blackducksoftware.bdio.proto.api.BdioHeader;
import com.synopsys.integration.detect.util.DetectZipUtil;
import com.synopsys.integration.util.NameVersion;

public class DetectProtobufBdioHeaderUtil {
    private final String scanId;
    private final String scanType;
    private final NameVersion projectNameVersion;
    private final String projectGroupName;
    private final String codeLocationName;
    private static final String CREATOR_NAME = "SYNOPSYS_DETECT";

    public DetectProtobufBdioHeaderUtil(String scanId, String scanType, NameVersion projectNameVersion, String projectGroupName, String codeLocationName) {
        this.scanId = scanId;
        this.scanType = scanType;
        this.projectNameVersion = projectNameVersion;
        this.projectGroupName = projectGroupName;
        this.codeLocationName = codeLocationName;
    }

    public File createProtobufBdioHeader(File targetDirectory) throws IOException {
        BdioHeader bdioHeader = new BdioHeader(
            scanId,
            scanType,
            codeLocationName,
            projectNameVersion.getName(),
            projectNameVersion.getVersion(),
            "",
            "",
            "",
            CREATOR_NAME,
            Instant.now(),
            null,
            null,
            projectGroupName,
            null,
            1L,
            "/",
            true,
            true,
            null,
            null)
            ;

        String tempBdioArchivePath = targetDirectory.toPath() + "/bdio-protobuf.zip";
        try (
            FileOutputStream outputStream = new FileOutputStream(tempBdioArchivePath);
            ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
            ProtobufBdioWriter protobufBdioWriter = new ProtobufBdioWriter(zipOutputStream)
        ) {
            protobufBdioWriter.writeHeader(bdioHeader);
        }
        File bdioZipFile = new File(tempBdioArchivePath);
        DetectZipUtil.unzip(bdioZipFile, targetDirectory);
        return new File(targetDirectory.toPath() + "/bdio-header.pb");
    }
}
