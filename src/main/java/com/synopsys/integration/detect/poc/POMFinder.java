package com.synopsys.integration.detect.poc;

import java.util.ArrayList;
import java.util.List;

public class POMFinder {

    public List<String> findAllProjectPOMs(String sourceDirectory){
        ArrayList<String> pomFilePaths = new ArrayList<String>();
        pomFilePaths.add(sourceDirectory);
        return pomFilePaths;
    }
}
