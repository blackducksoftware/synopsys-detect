/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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

    public static String shortHashString(final String value) {
        String shortHashString = "";
        final String hashedString = DigestUtils.sha1Hex(value);
        if (hashedString.length() > 15) {
            shortHashString = hashedString.substring(0, 15);
        } else {
            shortHashString = hashedString;
        }
        return shortHashString;
    }

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
                final String shortHashString = shortHashString(filenamePieces.get(i));
                filenamePieces.set(i, shortHashString);
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
