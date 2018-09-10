package com.synopsys.detect.doctor.logparser;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DetectLogPropertyParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static String EQUALS_SIGN = " = ";
    private static String OPENING_SQUARE = "[";
    private static String CLOSING_SQUARE = "]";

    private static String CALCULATED = "calculated";
    private static String INTERACTIVE = "interactive";
    private static String LATEST = "latest";
    private static String COPIED = "copied";

    public LoggedDetectProperty parseProperty(String line){
        LoggedDetectProperty property = new LoggedDetectProperty();
        String[] pieces  = line.split(EQUALS_SIGN);
        if (pieces.length == 2){
            String left = pieces[0];
            String right = pieces[1];
            property.key = left;

            if (right.contains(OPENING_SQUARE)){
                property.notes = StringUtils.substringBetween(right, OPENING_SQUARE, CLOSING_SQUARE);
                property.value = right.substring(0, right.indexOf(OPENING_SQUARE) - 1);

                if (property.notes.equals(CALCULATED)){
                    property.type = LoggedPropertyType.CALCULATED;
                }else if (property.notes.equals(INTERACTIVE)){
                    property.type = LoggedPropertyType.INTERACTIVE;
                }else if (property.notes.equals(LATEST)){
                    property.type = LoggedPropertyType.LATEST;
                }else if (property.notes.equals(COPIED)){
                    property.type = LoggedPropertyType.COPIED;
                } else {
                    property.type = LoggedPropertyType.OVERRIDE;
                }
            } else {
                property.value = right;
                property.type = LoggedPropertyType.DEFAULT;
            }
        } else {
            logger.error("Unable to parse configuration line: " + line);
        }
        return property;
    }

}
