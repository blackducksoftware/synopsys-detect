package com.synopsys.integration.detect.battery.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.zeroturnaround.zip.commons.FileUtils;

import com.synopsys.integration.detect.commontest.FileUtil;
import com.synopsys.integration.util.ResourceUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class BatteryFiles {
    public static final String DEFAULT_RESOURCE_PREFIX = "/battery";
    public static final String UTIL_RESOURCE_PREFIX = "/battery-util";

    public static String asString(String relativeResourcePath) {
        return asString(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static String asString(String relativeResourcePath, String prefix) {
        String path = prefix + relativeResourcePath;
        String data = null;
        try {
            data = ResourceUtil.getResourceAsString(BatteryFiles.class, path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertNotNull(data, "Unable to find resource: " + path);

        return Arrays.stream(data.split("\r?\n"))
            .collect(Collectors.joining(System.lineSeparator()));
    }

    public static List<String> asListOfStrings(String relativeResourcePath) {
        return asListOfStrings(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static List<String> asListOfStrings(String relativeResourcePath, String prefix) {
        String data = asString(relativeResourcePath, prefix);

        return Arrays.asList(data.split(System.lineSeparator()));
    }

    public static InputStream asInputStream(String relativeResourcePath) {
        return asInputStream(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static InputStream asInputStream(String relativeResourcePath, String prefix) {
        return BatteryFiles.class.getResourceAsStream(prefix + relativeResourcePath);
    }

    public static Reader asReader(String relativeResourcePath) {
        return asReader(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static Reader asReader(String relativeResourcePath, String prefix) {
        return new StringReader(asString(relativeResourcePath, prefix));
    }

    public static Template asTemplate(String relativeResourcePath) throws IOException {
        return asTemplate(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static Template asTemplate(String relativeResourcePath, String prefix) throws IOException {
        return asTemplate(BatteryFiles.asFile(relativeResourcePath, prefix));
    }

    public static Template asTemplate(File fullPath) throws IOException {
        Configuration templates = new Configuration(Configuration.VERSION_2_3_26);
        return new Template(fullPath.getName(), FileUtils.readFileToString(fullPath), templates);
    }

    public static void processTemplate(String relativeResourcePath, File target, Object model) throws IOException, TemplateException {
        processTemplate(relativeResourcePath, target, model, DEFAULT_RESOURCE_PREFIX);
    }

    public static void processTemplate(String relativeResourcePath, File target, Object model, String prefix) throws IOException, TemplateException {
        Template resourceTemplate = BatteryFiles.asTemplate(relativeResourcePath, prefix);
        processTemplate(resourceTemplate, target, model);
    }

    public static void processTemplate(File file, File target, Object model) throws IOException, TemplateException {
        processTemplate(asTemplate(file), target, model);
    }

    public static void processTemplate(Template template, File target, Object model) throws IOException, TemplateException {
        try (Writer fileWriter = new FileWriter(target)) {
            template.process(model, fileWriter);
        }
    }

    public static String resolvePath(String relativeResourcePath) {
        return resolvePath(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static String resolvePath(String relativeResourcePath, String prefix) {
        return BatteryFiles.asFile(relativeResourcePath, prefix).getAbsolutePath();
    }

    public static File asFile(String relativeResourcePath) {
        return asFile(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static File asFile(String relativeResourcePath, String prefix) {
        return FileUtil.asFile(DetectorBatteryTestRunner.class, relativeResourcePath, prefix);
    }
}
