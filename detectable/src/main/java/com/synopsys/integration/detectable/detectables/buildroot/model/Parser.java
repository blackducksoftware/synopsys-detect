package com.synopsys.integration.detectable.detectables.buildroot.model;

import java.lang.reflect.Type;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Parser {
    
    public Map<String, ShowInfoComponent> parse(String showInfoOutput) {
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, ShowInfoComponent>>() {}.getType();
        return gson.fromJson(showInfoOutput, type);        
    }
}
