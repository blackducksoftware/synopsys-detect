package com.synopsys.integration.detect.fastsca.report;

import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;
import com.synopsys.bd.kb.httpclient.api.BdKbHttpClientFactory;
import com.synopsys.bd.kb.httpclient.api.IBdKbHttpApi;
import com.synopsys.integration.detect.fastsca.model.FastScaEvidence;
import com.synopsys.integration.detect.fastsca.model.FastScaMetadata;
import com.synopsys.integration.detect.fastsca.model.FastScaReport;
import com.synopsys.kb.httpclient.api.HttpClientConfiguration;
import com.synopsys.kb.httpclient.api.HttpClientConfigurationBuilder;
import com.synopsys.kb.httpclient.api.KbConfiguration;

/**
 * fastSCA report API.
 * 
 * @author skatzman
 */
public class FastScaReportApi {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	private final FastScaReportAssembly fastScaReportAssembly;

	private final FastScaReportWriter fastScaReportWriter;
	
	/**
	 * Constructor.
	 * 
	 * @param scheme The KnowledgeBase scheme (e.g. - http or https).
	 * @param host The KnowledgeBase host (e.g. - kb.blackducksoftware.com).
	 * @param port The KnowledgeBase port (e.g. - 443)
	 * @param licenseKey The Black Duck KnowledgeBase product licensing key.
	 * @param userAgentHeaderValue The User-Agent header value.
	 */
	public FastScaReportApi(String scheme, String host, int port, String licenseKey, String userAgentHeaderValue) {
		HttpClientConfiguration httpClientConfiguration = HttpClientConfigurationBuilder.create().userAgent(userAgentHeaderValue).build();
		KbConfiguration kbConfiguration = new KbConfiguration(scheme, host, port, licenseKey);
		IBdKbHttpApi bdKbHttpApi = new BdKbHttpClientFactory().create(httpClientConfiguration, kbConfiguration);
		
		this.fastScaReportAssembly = new FastScaReportAssembly(bdKbHttpApi);
		this.fastScaReportWriter = new FastScaReportWriter();
	}
	
	/**
	 * Creates the fastSCA report.
	 * 
	 * This operations assembles the report and writes it to the target path.
	 * 
	 * @param evidences The evidences to match.
	 * @param meta The execution metadata.
	 * @param destinationFile The destination file to which to write the fastSCA report.
	 */
	public void create(Set<FastScaEvidence> evidences, FastScaMetadata meta, File destinationFile) {
		Objects.requireNonNull(evidences, "Evidences must be initialized.");
		Objects.requireNonNull(meta, "Meta must be initialized.");
		
		Stopwatch stopwatch = Stopwatch.createStarted();
		
		try {
			FastScaReport fastScaReport = fastScaReportAssembly.assemble(evidences, meta);
		
			fastScaReportWriter.write(fastScaReport, destinationFile);
		} finally {
			stopwatch = stopwatch.stop();
			long durationMs = stopwatch.elapsed(TimeUnit.MILLISECONDS);
			logger.info("Finished fastSCA report operations [Duration: {}ms].", durationMs);
		}
	}
}
