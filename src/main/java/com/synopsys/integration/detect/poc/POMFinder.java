package com.synopsys.integration.detect.poc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class POMFinder {


    public List<String> findAllProjectPOMs(File sourceDirectory){
        ArrayList<String> pomFilePaths = new ArrayList<String>();

        // TODO properties from parent pom can be referenced in child poms but this case is not covered atm
        try (Stream<Path> walkStream = Files.walk(Paths.get(sourceDirectory.toString()))) {
            walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                if (f.toString().endsWith("pom.xml")) {
                    System.out.println(f + " found!");
                    pomFilePaths.add(f.toString());
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // before adding, check to make sure file exists ...
        pomFilePaths.add(sourceDirectory.toString() + "/pom.xml");
        return pomFilePaths;
    }
}
