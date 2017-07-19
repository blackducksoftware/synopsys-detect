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
package com.blackducksoftware.integration.hub.detect.bomtool.sbt;

import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtCaller
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtModule
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtReport
import com.blackducksoftware.integration.hub.detect.bomtool.sbt.models.SbtRevision

import groovy.util.slurpersupport.GPathResult;


public class SbtReportParser {
    public SbtReport parseReportFromXml(GPathResult xmlReport) {
        SbtReport report = new SbtReport();
        report.organisation = xmlReport.info.@organisation.toString();
        report.module = xmlReport.info.@module.toString();
        report.revision = xmlReport.info.@revision.toString();
        report.configuration = xmlReport.info.@conf.toString();
        report.dependencies = new ArrayList<SbtModule>();

        xmlReport.dependencies.module.each{xmlModule ->
            SbtModule module = new SbtModule();
            module.name = xmlModule.@name.toString()
            module.organisation = xmlModule.@organisation.toString()
            module.revisions = new ArrayList<SbtRevision>();
            report.dependencies.add(module);

            xmlModule.revision.each{  xmlRevision ->
                SbtRevision revision = new SbtRevision();
                revision.name = xmlRevision.@name.toString()
                revision.callers = new ArrayList<SbtCaller>();
                module.revisions.add(revision);

                xmlRevision.caller.each { xmlCaller ->
                    SbtCaller caller = new SbtCaller();
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
