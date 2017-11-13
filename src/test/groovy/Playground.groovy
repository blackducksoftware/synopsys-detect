import org.apache.commons.io.IOUtils
import org.junit.Test

import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.rest.UnauthenticatedRestConnection
import com.blackducksoftware.integration.log.LogLevel
import com.blackducksoftware.integration.log.PrintStreamIntLogger

import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

class Playground {
    public void test() {
        final String latestEncoded = URLEncoder.encode("[RELEASE]", "UTF-8")
        final URL latestDetectJarRestUrl = new URL("https://test-repo.blackducksoftware.com/artifactory/bds-integrations-release/com/blackducksoftware/integration/hub-detect/" + latestEncoded + "/hub-detect-" + latestEncoded + ".jar")
        final URL latestDetectVersionUrl = new URL("https://test-repo.blackducksoftware.com/artifactory/api/search/latestVersion?g=com.blackducksoftware.integration&a=hub-detect&repos=bds-integrations-release")
        final RestConnection restConnection = new UnauthenticatedRestConnection(new PrintStreamIntLogger(System.out, LogLevel.DEBUG), latestDetectVersionUrl, 30)
        final HttpUrl contentHttpUrl = restConnection.createHttpUrl()
        final Request request = restConnection.createGetRequest(contentHttpUrl, "text/plain")
        Response response = null
        try {
            response = restConnection.handleExecuteClientCall(request)
            final ResponseBody responseBody = response.body()
            final String version = responseBody.string()
            println version
        } finally {
            IOUtils.closeQuietly(response)
        }
    }

    @Test
    public void test2() {
        final String MR_DASH = '─'
        List<String> output = '''
===> Verifying dependencies...
===> Fetching eunit_formatters ({git,
                                        "git://github.com/seancribbs/eunit_formatters",
                                        {branch,"master"}})
===> Fetching jsx ({git,"https://github.com/talentdeficit/jsx.git",
                               {tag,"v2.4.0"}})
===> Fetching meck ({git,"https://github.com/eproxus/meck.git",
                                {tag,"0.8.2"}})
===> Fetching qdate ({git,"https://github.com/choptastic/qdate.git",
                                 {branch,"master"}})
===> Fetching webpush_encryption ({git,
                                          "https://github.com/marcelloceschia/webpush_encryption.git",
                                          {branch,"master"}})
===> Fetching base64url ({git,"https://github.com/dvv/base64url.git",
                                     {tag,"v1.0"}})
===> Fetching erlware_commons ({pkg,<<"erlware_commons">>,<<"1.0.1">>})
===> Downloaded package, caching at /Users/rotte/.cache/rebar3/hex/default/packages/erlware_commons-1.0.1.tar
===> Fetching qdate_localtime ({pkg,<<"qdate_localtime">>,<<"1.1.0">>})
===> Downloaded package, caching at /Users/rotte/.cache/rebar3/hex/default/packages/qdate_localtime-1.1.0.tar
===> Fetching cf ({pkg,<<"cf">>,<<"0.2.2">>})
===> Downloaded package, caching at /Users/rotte/.cache/rebar3/hex/default/packages/cf-0.2.2.tar
└─ gcm─1.0.1 (project app)
   ├─ eunit_formatters─0.5.0 (git repo)
   ├─ jsx─2.4.0 (git repo)
   ├─ meck─0.8.2 (git repo)
   ├─ qdate─0.4.2 (git repo)
   │  ├─ erlware_commons─1.0.1 (hex package)
   │  │  └─ cf─0.2.2 (hex package)
   │  └─ qdate_localtime─1.1.0 (hex package)
   └─ webpush_encryption─0.0.1 (git repo)
      └─ base64url─0.0.1 (git repo)
'''.split(System.lineSeparator)
        println('List output: ')
        List<String> cleanedOutput = []
        for (String line: output) {
            if (line.trim().contains(MR_DASH)) {
                cleanedOutput.add(line.trim())
            }
        }
        for (String line: cleanedOutput) {
            println(line)
        }
        []
    }
}
