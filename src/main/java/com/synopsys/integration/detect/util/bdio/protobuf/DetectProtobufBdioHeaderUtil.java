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

    public DetectProtobufBdioHeaderUtil(String scanId, String scanType, NameVersion projectNameVersion) {
        this.scanId = scanId;
        this.scanType = scanType;
        this.projectNameVersion = projectNameVersion;
    }

    public File createProtobufBdioHeader(File targetDirectory) throws IOException {
        BdioHeader bdioHeader = new BdioHeader(
            scanId,
            scanType,
            "codeLocation name",
            projectNameVersion.getName(),
            projectNameVersion.getVersion(),
            "publisher name",
            "publisher version",
            "publisher comment",
            "creator",
            Instant.now(),
            null,
            null,
            null,
            null,
            1L,
            "/baseDir",
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
