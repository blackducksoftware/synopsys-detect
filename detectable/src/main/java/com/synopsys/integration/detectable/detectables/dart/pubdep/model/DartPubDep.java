package com.synopsys.integration.detectable.detectables.dart.pubdep.model;

import java.util.ArrayList;
import java.util.List;

public class DartPubDep {
    public String name;
    public String version;
    public List<DartPubDep> dependencies = new ArrayList<>();
}
