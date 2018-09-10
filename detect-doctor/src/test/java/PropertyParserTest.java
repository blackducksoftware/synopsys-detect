import org.junit.Assert;
import org.junit.Test;

import com.synopsys.detect.doctor.logparser.DetectLogPropertyParser;
import com.synopsys.detect.doctor.logparser.LoggedDetectProperty;
import com.synopsys.detect.doctor.logparser.LoggedPropertyType;

public class PropertyParserTest {

    @Test
    public void defaultPropertyType() {
        propertyTest("blackduck.hub.offline.mode = true", "blackduck.hub.offline.mode", "true", null, LoggedPropertyType.DEFAULT);
    }

    @Test
    public void calculatedPropertyType() {
        propertyTest("detect.bdio.output.path = C:\\Users\\admin\\blackduck\\bdio [calculated]", "detect.bdio.output.path", "C:\\Users\\admin\\blackduck\\bdio", "calculated", LoggedPropertyType.CALCULATED);
    }

    @Test
    public void overridePropertyType() {
        propertyTest("detect.bom.tool.search.exclusion = node_modules,debug,bin,build,.git,.gradle,node_modules,out,packages,target [node_modules,debug]", "detect.bom.tool.search.exclusion", "node_modules,debug,bin,build,.git,.gradle,node_modules,out,packages,target", "node_modules,debug", LoggedPropertyType.OVERRIDE);
    }

    private void propertyTest(String line, String key, String value, String notes, LoggedPropertyType type) {
        DetectLogPropertyParser parser = new DetectLogPropertyParser();

        LoggedDetectProperty property = parser.parseProperty(line);

        Assert.assertEquals(key, property.key);
        Assert.assertEquals(value, property.value);
        Assert.assertEquals(notes, property.notes);
        Assert.assertEquals(type, property.type);
    }
}
