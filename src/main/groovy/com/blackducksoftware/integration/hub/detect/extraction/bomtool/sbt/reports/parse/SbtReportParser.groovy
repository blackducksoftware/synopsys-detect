/*
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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.sbt.reports.parse

import com.blackducksoftware.integration.hub.detect.extraction.bomtool.sbt.reports.model.SbtCaller
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.sbt.reports.model.SbtModule
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.sbt.reports.model.SbtReport
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.sbt.reports.model.SbtRevision

import groovy.util.slurpersupport.GPathResult

public class SbtReportParser {
    public SbtReport parseReportFromXml(GPathResult xmlReport) {
        SbtReport report = new SbtReport()
        report.organisation = xmlReport.info.@organisation.toString()
        report.module = xmlReport.info.@module.toString()
        report.revision = xmlReport.info.@revision.toString()
        report.configuration = xmlReport.info.@conf.toString()
        report.dependencies = new ArrayList<SbtModule>()

        xmlReport.dependencies.module.each { xmlModule ->
            SbtModule module = new SbtModule()
            module.name = xmlModule.@name.toString()
            module.organisation = xmlModule.@organisation.toString()
            module.revisions = new ArrayList<SbtRevision>()
            report.dependencies.add(module)

            xmlModule.revision.each { xmlRevision ->
                SbtRevision revision = new SbtRevision()
                revision.name = xmlRevision.@name.toString()
                revision.callers = new ArrayList<SbtCaller>()
                module.revisions.add(revision)

                xmlRevision.caller.each { xmlCaller ->
                    SbtCaller caller = new SbtCaller()
                    caller.callerOrganisation = xmlCaller.@organisation.toString()
                    caller.callerName = xmlCaller.@name.toString()
                    caller.callerRevision = xmlCaller.@callerrev.toString()
                    revision.callers.add(caller)
                }
            }
        }
        report
    }
}
