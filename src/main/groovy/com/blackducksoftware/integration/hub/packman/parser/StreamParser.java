/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public abstract class StreamParser<T> {

    public T parse(final InputStream inputStream) {
        return parse(new InputStreamReader(inputStream));
    }

    public T parse(final InputStreamReader inputStreamReader) {
        try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            return parse(bufferedReader);
        } catch (final IOException e) {
            // TODO: Log
            e.printStackTrace();
        }
        return null;
    }

    public abstract T parse(BufferedReader bufferedReader);

    public String processSingleLineComments(String line, final String comment) {
        if (line.contains(comment)) {
            final String[] sections = line.split("#");
            if (sections.length > 0) {
                line = sections[0].trim();
            } else {
                line = "";
            }
        }
        return line;
    }
}
