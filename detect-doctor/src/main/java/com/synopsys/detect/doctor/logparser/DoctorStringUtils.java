package com.synopsys.detect.doctor.logparser;

public class DoctorStringUtils {
    public  static  String substringAfter(String original, String target){
        return original.substring(original.indexOf(target) + target.length());
    }
}
