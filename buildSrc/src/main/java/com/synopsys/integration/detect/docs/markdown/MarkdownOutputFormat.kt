/**
 * buildSrc
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.docs.markdown

import freemarker.core.CommonMarkupOutputFormat
import java.io.Writer

//This class was made by following the guideline of RTFOutputFormat as proper docs on how to do this could not be found. This may be incorrect, but seems to work.
class MarkdownOutputFormat : CommonMarkupOutputFormat<MarkdownOutputModel>() {
    override fun getName(): String {
        return "Markdown";
    }

    override fun output(textToEsc: String?, out: Writer?) {
        textToEsc?.let { out?.write(MarkdownEscapeUtils.escape(it)) }
    }

    override fun newTemplateMarkupOutputModel(plainTextContent: String?, markupContent: String?): MarkdownOutputModel {
        return MarkdownOutputModel(plainTextContent, markupContent);
    }

    override fun isLegacyBuiltInBypassed(builtInName: String?): Boolean {
        return builtInName == "markdown"
    }

    override fun getMimeType(): String {
        return "text/markdown"
    }

    override fun escapePlainText(plainTextContent: String?): String {
        return MarkdownEscapeUtils.escape(plainTextContent ?: "")
    }

    companion object {
        val INSTANCE = MarkdownOutputFormat()
    }
}