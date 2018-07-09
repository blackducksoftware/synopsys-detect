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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.blackducksoftware.integration.hub.detect.util.XmlUtil;

public class SbtReportParser {
    public SbtReport parseReportFromXml(final Document xmlReport) {
        final SbtReport report = new SbtReport();
        final Node infoNode = XmlUtil.getNode("info", xmlReport);
        report.organisation = XmlUtil.getAttribute("organisation", infoNode);
        report.module = XmlUtil.getAttribute("module", infoNode);
        report.revision = XmlUtil.getAttribute("revision", infoNode);
        report.configuration = XmlUtil.getAttribute("conf", infoNode);
        report.dependencies = new ArrayList<>();

        final Node dependencies = XmlUtil.getNode("dependencies", xmlReport);
        final List<Node> modules = XmlUtil.getNodeList("module", dependencies);

        modules.forEach(xmlModule -> {
            final SbtModule module = new SbtModule();
            module.name = XmlUtil.getAttribute("name", xmlModule);
            module.organisation = XmlUtil.getAttribute("organisation", xmlModule);
            module.revisions = new ArrayList<>();
            report.dependencies.add(module);

            final List<Node> revisions = XmlUtil.getNodeList("revision", xmlModule);
            revisions.forEach(xmlRevision -> {
                final SbtRevision revision = new SbtRevision();
                revision.name = XmlUtil.getAttribute("name", xmlRevision);
                revision.callers = new ArrayList<>();
                module.revisions.add(revision);

                final List<Node> callers = XmlUtil.getNodeList("caller", xmlRevision);
                callers.forEach(xmlCaller -> {
                    final SbtCaller caller = new SbtCaller();
                    caller.callerOrganisation = XmlUtil.getAttribute("organisation", xmlCaller);
                    caller.callerName = XmlUtil.getAttribute("name", xmlCaller);
                    caller.callerRevision = XmlUtil.getAttribute("callerrev", xmlCaller);
                    revision.callers.add(caller);
                });
            });
        });
        return report;
    }

}
