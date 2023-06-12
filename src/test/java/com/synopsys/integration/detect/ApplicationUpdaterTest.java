package com.synopsys.integration.detect;

import com.synopsys.integration.rest.proxy.ProxyInfo;
import freemarker.template.Version;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class ApplicationUpdaterTest {
    
    private final String fakeUrl = "https://synopsys.com";
    
    private final String[] successArgs = new String[] {
            "-jar",
            "/fake/path/to/synopsys-detect-n.n.n.jar",
            "--blackduck.url=".concat(fakeUrl), 
            "--blackduck.trust.cert=true", 
            "--blackduck.api.token=dummyToken",
            "--detect.tools=DETECTOR"};
    
    private final String[] failureArgs = new String[] {
            "-jar",
            "/fake/path/to/synopsys-detect-n.n.n.jar",
            "--blackduck.trust.cert=true", 
            "--blackduck.api.token=dummyToken",
            "--detect.tools=DETECTOR"};
    
    private final String[] successArgsWithValidProxy = new String[] {
            "-jar",
            "/fake/path/to/synopsys-detect-n.n.n.jar",
            "--blackduck.url=".concat(fakeUrl),
            "--blackduck.trust.cert=true", 
            "--blackduck.api.token=dummyToken",
            "--blackduck.proxy.host=".concat(fakeUrl),
            "--blackduck.proxy.port=8080",
            "--detect.tools=DETECTOR"};
    
    private final String[] successArgsWithInvalidProxy = new String[] {
            "-jar",
            "/fake/path/to/synopsys-detect-n.n.n.jar",
            "--blackduck.url=".concat(fakeUrl),
            "--blackduck.trust.cert=true", 
            "--blackduck.api.token=dummyToken",
            "--blackduck.proxy.host=".concat(fakeUrl),
            "--detect.tools=DETECTOR"};
    
    private final String[] alreadySelfUpdatedArgs = new String[] {
            "-jar",
            "/fake/path/to/synopsys-detect-n.n.n.jar",
            "--blackduck.url=".concat(fakeUrl), 
            "--blackduck.trust.cert=true", 
            "--blackduck.api.token=dummyToken",
            "--detect.tools=DETECTOR",
            "--selfUpdated"};
    
    private final String[] successArgsWithSpringBootCertValue = new String[] {
            "-jar",
            "/fake/path/to/synopsys-detect-n.n.n.jar",
            "--blackduck.url=".concat(fakeUrl), 
            "--blackduck.api.token=dummyToken",
            "--detect.tools=DETECTOR"};
    
    @Test
    public void testCanSelfUpdate() {
        Assertions.assertTrue(new ApplicationUpdater(new ApplicationUpdaterUtility(), successArgs).canSelfUpdate());
    }
    
    @Test
    public void testCanSelfUpdateWithoutBdUrl() {
        ApplicationUpdaterUtility mockedUtility = Mockito.mock(ApplicationUpdaterUtility.class);
        Mockito.when(mockedUtility.getSysEnvProperty(ApplicationUpdater.SYS_ENV_PROP_BLACKDUCK_URL)).thenReturn(null);
        Assertions.assertFalse(new ApplicationUpdater(mockedUtility, failureArgs).canSelfUpdate());
    }
    
    @Test
    public void testCanSelfUpdateWithSystemEnvBdUrl() {
        ApplicationUpdaterUtility mockedUtility = Mockito.mock(ApplicationUpdaterUtility.class);
        Mockito.when(mockedUtility.getSysEnvProperty(ApplicationUpdater.SYS_ENV_PROP_BLACKDUCK_URL)).thenReturn(fakeUrl);
        Assertions.assertTrue(new ApplicationUpdater(mockedUtility, failureArgs).canSelfUpdate());
    }
    
    @Test
    public void testCanSelfUpdateWithSystemEnvDetectSrc() {
        ApplicationUpdaterUtility mockedUtility = Mockito.mock(ApplicationUpdaterUtility.class);
        Mockito.when(mockedUtility.getSysEnvProperty(ApplicationUpdater.SYS_ENV_PROP_DETECT_SOURCE)).thenReturn(fakeUrl);
        Assertions.assertFalse(new ApplicationUpdater(mockedUtility, successArgs).canSelfUpdate());
    }
    
    @Test
    public void testCanSelfUpdateWithSystemEnvLatestRelease() {
        ApplicationUpdaterUtility mockedUtility = Mockito.mock(ApplicationUpdaterUtility.class);
        Mockito.when(mockedUtility.getSysEnvProperty(ApplicationUpdater.SYS_ENV_PROP_DETECT_LATEST_RELEASE_VERSION)).thenReturn(fakeUrl);
        Assertions.assertFalse(new ApplicationUpdater(mockedUtility, successArgs).canSelfUpdate());
    }
    
    @Test
    public void testCanSelfUpdateWithSystemEnvDetectVerKey() {
        ApplicationUpdaterUtility mockedUtility = Mockito.mock(ApplicationUpdaterUtility.class);
        Mockito.when(mockedUtility.getSysEnvProperty(ApplicationUpdater.SYS_ENV_PROP_DETECT_VERSION_KEY)).thenReturn(fakeUrl);
        Assertions.assertFalse(new ApplicationUpdater(mockedUtility, successArgs).canSelfUpdate());
    }
    
    @Test
    public void testNoProxyFoundOne() {
        ApplicationUpdaterUtility mockedUtility = Mockito.mock(ApplicationUpdaterUtility.class);
        Mockito.when(mockedUtility.getSysEnvProperty(ApplicationUpdater.SYS_ENV_PROP_PROXY_HTTP_HOST)).thenReturn(fakeUrl);
        Assertions.assertEquals(ProxyInfo.NO_PROXY_INFO, new ApplicationUpdater(mockedUtility, failureArgs).getProxyInfo());
    }
    
    @Test
    public void testNoProxyFoundTwo() {
        ApplicationUpdaterUtility mockedUtility = Mockito.mock(ApplicationUpdaterUtility.class);
        Mockito.when(mockedUtility.getSysEnvProperty(ApplicationUpdater.SYS_ENV_PROP_PROXY_HTTP_PORT)).thenReturn(fakeUrl);
        Assertions.assertEquals(ProxyInfo.NO_PROXY_INFO, new ApplicationUpdater(mockedUtility, failureArgs).getProxyInfo());
    }
    
    @Test
    public void testProxyFoundOne() {
        ApplicationUpdaterUtility mockedUtility = Mockito.mock(ApplicationUpdaterUtility.class);
        Mockito.when(mockedUtility.getSysEnvProperty(ApplicationUpdater.SYS_ENV_PROP_PROXY_HTTP_HOST)).thenReturn(fakeUrl);
        Mockito.when(mockedUtility.getSysEnvProperty(ApplicationUpdater.SYS_ENV_PROP_PROXY_HTTP_PORT)).thenReturn("8080");
        Assertions.assertNotEquals(ProxyInfo.NO_PROXY_INFO, new ApplicationUpdater(mockedUtility, failureArgs).getProxyInfo());
    }
    
    @Test
    public void testProxyFoundTwo() {
        ApplicationUpdaterUtility mockedUtility = Mockito.mock(ApplicationUpdaterUtility.class);
        Mockito.when(mockedUtility.getSysEnvProperty(ApplicationUpdater.SYS_ENV_PROP_PROXY_HTTPS_HOST)).thenReturn(fakeUrl);
        Mockito.when(mockedUtility.getSysEnvProperty(ApplicationUpdater.SYS_ENV_PROP_PROXY_HTTPS_PORT)).thenReturn("8080");
        Assertions.assertNotEquals(ProxyInfo.NO_PROXY_INFO, new ApplicationUpdater(mockedUtility, failureArgs).getProxyInfo());
    }
    
    @Test
    public void testProxyFoundThree() {
        Assertions.assertEquals(ProxyInfo.NO_PROXY_INFO, new ApplicationUpdater(new ApplicationUpdaterUtility(), successArgsWithInvalidProxy).getProxyInfo());
    }
    
    @Test
    public void testProxyFoundFour() {
        Assertions.assertNotEquals(ProxyInfo.NO_PROXY_INFO, new ApplicationUpdater(new ApplicationUpdaterUtility(), successArgsWithValidProxy).getProxyInfo());
    }
    
    @Test
    public void testValidDetectFileName() {
        Assertions.assertTrue(new ApplicationUpdater(new ApplicationUpdaterUtility(), successArgs).isValidDetectFileName("synopsys-detect-8.9.0-SIGQA6-SNAPSHOT.jar"));
    }
    
    @Test
    public void testVersionFromDetectFileName() {
        Assertions.assertEquals("8.9.0", new ApplicationUpdater(new ApplicationUpdaterUtility(), successArgs).getVersionFromDetectFileName("synopsys-detect-8.9.0-SIGQA6-SNAPSHOT.jar"));
    }
    
    @Test
    public void testVersionConvert() {
        Assertions.assertEquals(new Version(8, 9, 0), new ApplicationUpdater(new ApplicationUpdaterUtility(), successArgs).convert("8.9.0"));
    }
    
    @Test
    public void testTooOldDownloadVersion() {
        Assertions.assertTrue(new ApplicationUpdater(new ApplicationUpdaterUtility(), successArgs).isDownloadVersionTooOld("8.9.0", "8.8.0"));
    }
    
    @Test
    public void testNotSoOldDownloadVersion() {
        Assertions.assertFalse(new ApplicationUpdater(new ApplicationUpdaterUtility(), successArgs).isDownloadVersionTooOld("8.10.0", "8.9.0"));
    }
    
    @Test
    public void testNewDownloadVersion() {
        Assertions.assertFalse(new ApplicationUpdater(new ApplicationUpdaterUtility(), successArgs).isDownloadVersionTooOld("8.9.0", "8.9.0"));
    }
    
    @Test
    public void testCanSelfUpdateIfAlreadyUpdated() {
        Assertions.assertFalse(new ApplicationUpdater(new ApplicationUpdaterUtility(), alreadySelfUpdatedArgs).canSelfUpdate());
    }
    
    @Test
    public void testCanSelfUpdateWithSpringBootProperty() {
        ApplicationUpdaterUtility mockedUtility = Mockito.mock(ApplicationUpdaterUtility.class);
        Mockito.when(mockedUtility.getSysEnvProperty(ApplicationUpdater.SYS_ENV_PROP_SPRING_BOOT)).thenReturn("{\"blackduck.trust.cert\":\"true\"}");
        Assertions.assertTrue(new ApplicationUpdater(mockedUtility, successArgsWithSpringBootCertValue).isTrustCertificate());
    }
}
