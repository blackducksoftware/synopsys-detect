package com.synopsys.integration.detect.battery;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.util.ResourceUtil;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class BatteryFiles {
    public static final String DEFAULT_RESOURCE_PREFIX = "/battery";
    public static final String UTIL_RESOURCE_PREFIX = "/battery-util";

    public static String asString(final String relativeResourcePath) {
        return asString(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static String asString(final String relativeResourcePath, String prefix) {
        String path = prefix + relativeResourcePath;
        String data = null;
        try {
            data = ResourceUtil.getResourceAsString(BatteryFiles.class, path, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertNotNull(data, "Unable to find resource: " + path);

        return Arrays.stream(data.split("\r?\n"))
                   .collect(Collectors.joining(System.lineSeparator()));
    }

    public static List<String> asListOfStrings(final String relativeResourcePath){
        return asListOfStrings(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static List<String> asListOfStrings(final String relativeResourcePath, String prefix) {
        final String data = asString(relativeResourcePath, prefix);

        return Arrays.asList(data.split(System.lineSeparator()));
    }

    public static InputStream asInputStream(final String relativeResourcePath) {
        return asInputStream(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static InputStream asInputStream(final String relativeResourcePath, String prefix) {
        return BatteryFiles.class.getResourceAsStream(prefix + relativeResourcePath);
    }

    public static Reader asReader(final String relativeResourcePath) {
        return asReader(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static Reader asReader(final String relativeResourcePath, String prefix) {
        return new StringReader(asString(relativeResourcePath, prefix));
    }

    public static Template asTemplate(final String relativeResourcePath) throws IOException {
        return asTemplate(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static Template asTemplate(final String relativeResourcePath, String prefix) throws IOException {
        final Configuration templates = new Configuration(Configuration.VERSION_2_3_26);
        return new Template(relativeResourcePath, BatteryFiles.asReader(relativeResourcePath, prefix), templates);
    }

    public static void processTemplate(final String relativeResourcePath, final File target, final Object model) throws IOException, TemplateException {
        processTemplate(relativeResourcePath, target, model, DEFAULT_RESOURCE_PREFIX);
    }

    public static void processTemplate(final String relativeResourcePath, final File target, final Object model, String prefix) throws IOException, TemplateException {
        final Template template = BatteryFiles.asTemplate(relativeResourcePath, prefix);
        try (final Writer fileWriter = new FileWriter(target)) {
            template.process(model, fileWriter);
        }
    }

    public static String resolvePath(final String relativeResourcePath) {
        return resolvePath(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static String resolvePath(final String relativeResourcePath, String prefix) {
        return BatteryFiles.asFile(relativeResourcePath, prefix).getAbsolutePath();
    }

    public static File asFile(final String relativeResourcePath){
        return asFile(relativeResourcePath, DEFAULT_RESOURCE_PREFIX);
    }

    public static File asFile(final String relativeResourcePath, String prefix) {
        final URL resource = BatteryFiles.class.getResource(prefix + relativeResourcePath);
        Assertions.assertNotNull(resource, "Could not find resource path: " + prefix + relativeResourcePath);
        final File file = new File(resource.getFile());
        Assertions.assertTrue(file.exists());

        return file;
    }
}
