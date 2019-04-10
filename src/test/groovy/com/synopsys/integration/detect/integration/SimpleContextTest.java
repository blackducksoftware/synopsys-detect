package com.synopsys.integration.detect.integration;

import com.synopsys.integration.detect.Application;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Tag("integration")
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class)
public class SimpleContextTest {
    @Test
    public void testContextLoads() {

    }

}
