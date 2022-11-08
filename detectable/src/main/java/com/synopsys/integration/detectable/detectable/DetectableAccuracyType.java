package com.synopsys.integration.detectable.detectable;

//TODO: Future would be to add "RUNTIME" and have the Extraction report it as well, so if you KNOW you are HIGH keep high, if you MIGHT be HIGH put RUNTIME and let extraction report, otherwise put LOW.
public enum DetectableAccuracyType {
    HIGH,
    LOW
}
