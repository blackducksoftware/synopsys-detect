package com.synopsys.detect.doctor.run;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class DoctorRun {
    private final String id;

    public static DoctorRun createDefault() {
        return new DoctorRun(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS").withZone(ZoneOffset.UTC).format(Instant.now().atZone(ZoneOffset.UTC)));
    }

    public DoctorRun(String id) {
        this.id = id;
    }

    public String getRunId() {
        return this.id;
    }
}
