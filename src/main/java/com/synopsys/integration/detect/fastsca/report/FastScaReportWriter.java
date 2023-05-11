package com.synopsys.integration.detect.fastsca.report;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.synopsys.integration.detect.fastsca.model.FastScaReport;

public class FastScaReportWriter {
	private final ObjectMapper objectMapper;
	
	public FastScaReportWriter() {
		this.objectMapper = constructObjectMapper();
	}
	
	/**
	 * Writes the fastSCA report to a string.
	 * 
	 * This operation can result in a RuntimeException if the fastSCA report cannot be serialized.
	 * 
	 * @param fastScaReport The fastSCA report.
	 * @return Returns the fastSCA report as serialized to a string.
	 */
	public String write(FastScaReport fastScaReport) {
		Objects.requireNonNull(fastScaReport, "fastSCA report must be initialized.");
		
		try {
			return objectMapper.writeValueAsString(fastScaReport);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Unable to write fastSCA report to string.", e);
		}
	}
	
	/**
	 * Writes the fastSCA report to the target destination file.
	 * 
	 * This operation can result in a RuntimeException if the fastSCA report cannot be serialized or if the 
	 * fastSCA report cannot be written to the destination file path.
	 * 
	 * @param fastScaReport The fastSCA report.
	 * @param destinationFile The destination file.
	 */
	public void write(FastScaReport fastScaReport, File destinationFile) {
		Objects.requireNonNull(fastScaReport, "fastSCA report must be initialized.");
		Objects.requireNonNull(destinationFile, "Destination file must be initialized.");
		
		try {
			objectMapper.writeValue(destinationFile, fastScaReport);
		} catch (IOException e) {
			String absolutePath = destinationFile.getAbsolutePath();
			
			throw new RuntimeException("Unable to write fastSCA report to destination file: " + absolutePath, e);
		}
	}
	
    private ObjectMapper constructObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        /* Serialization */

        // Instead of serializing to milliseconds, print out formatted UTC string
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_WITH_ZONE_ID, false);
        objectMapper.setSerializationInclusion(Include.NON_ABSENT);

        /* Deserialization */

        // Convert Date and DateTimes to UTC

        objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        // Modules
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(new Jdk8Module());

        return objectMapper;
    }
}
