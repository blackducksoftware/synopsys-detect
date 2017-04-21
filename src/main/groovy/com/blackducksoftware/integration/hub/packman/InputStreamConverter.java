package com.blackducksoftware.integration.hub.packman;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.stereotype.Component;

@Component
public class InputStreamConverter {
    public BufferedReader convertToBufferedReader(final InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }

}
