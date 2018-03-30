package com.blackducksoftware.integration.hub.detect.util;

import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.util.IntegrationEscapeUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Component
public class BdioFileNamer {

    @Autowired
    private IntegrationEscapeUtil integrationEscapeUtil;

    public String generateShortenedFilename(final BomToolType bomToolType, final String finalSourcePathPiece, final ExternalId externalId) {
        final List<String> filenamePieces = new ArrayList<>(Arrays.asList(externalId.getExternalIdPieces()));
        filenamePieces.add(finalSourcePathPiece);
        String filename = generateFilename(bomToolType, filenamePieces);

        if (filename.length() >= 255) {
            filenamePieces.sort(new Comparator<String>() {
                @Override
                public int compare(final String s1, final String s2) {
                    return s1.length() - s2.length();
                }
            });
            for (int i = filenamePieces.size() - 1; (filename.length() >= 255) && (i >= 0); i--) {
                filenamePieces.set(i, DigestUtils.sha1Hex(filenamePieces.get(i)));
                if (filenamePieces.get(i).length() > 15) {
                    filenamePieces.set(i, filenamePieces.get(i).substring(0, 15));
                }
                filename = generateFilename(bomToolType, filenamePieces);
            }
        }

        return filename;
    }

    private String generateFilename(final BomToolType bomToolType, final List<String> pieces) {
        final List<String> rawPieces = new ArrayList<>();
        rawPieces.add(bomToolType.toString());
        rawPieces.addAll(pieces);
        rawPieces.add("bdio");

        final List<String> safePieces = new ArrayList<>();
        for (final String rawPiece : rawPieces) {
            safePieces.add(integrationEscapeUtil.escapeForUri(rawPiece));
        }
        return StringUtils.join(safePieces, "_") + ".jsonld";
    }
}
