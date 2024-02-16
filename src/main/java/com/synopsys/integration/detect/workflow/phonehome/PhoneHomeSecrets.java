package com.synopsys.integration.detect.workflow.phonehome;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.detect.Application;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class PhoneHomeSecrets {
    public static String API_SECRET_NAME = "api_secret";
    public static String MEASUREMENT_ID_NAME = "measurement_id";

    @SerializedName("api_secret")
    private String apiSecret;
    @SerializedName("measurement_id")
    private String measurementId;

    public PhoneHomeSecrets(String apiSecret, String measurementId) {
        this.apiSecret = apiSecret;
        this.measurementId = measurementId;
    }

    public static PhoneHomeSecrets getGa4Credentials() throws NoSuchAlgorithmException, IOException, InterruptedException {
        String detectHash = getDetectJarCheckSum();
//        String modifiedHash = com.synopsys.integration.componentlocator.utils.StringModifier.modify(detectHash); // actual code to be when CLL repo is updated.
        String modifiedHash = detectHash;  // mock code
        return getGa4CredentialsFromGcp(modifiedHash);
    }

    public static String getDetectJarCheckSum() throws NoSuchAlgorithmException, IOException {
        String filePath = formatFilePath(Application.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        return getFileCheckSum(filePath);
    }

    public static String getFileCheckSum(String filePath) throws NoSuchAlgorithmException, IOException {
        byte[] data = Files.readAllBytes(Paths.get(filePath));
        byte[] hash = MessageDigest.getInstance("SHA-256").digest(data);
        return new BigInteger(1, hash).toString(16);
    }

    private static PhoneHomeSecrets getGa4CredentialsFromGcp(String modifiedHash) throws IOException, InterruptedException {
        // Mocking an api call until the GCP service is ready.
        HttpClient httpClient = Mockito.mock(HttpClient.class);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://my.gcp.uri"))
                .build();
        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        String responseBody = "{" +
                "\""+API_SECRET_NAME+"\": \"my_aPi_SEcRet\"," +
                "\""+MEASUREMENT_ID_NAME+"\": \"G-1452334585\"" +
                "}";
        Gson gson = new Gson();
        return gson.fromJson(responseBody, PhoneHomeSecrets.class);
    }

    public static String formatFilePath(String filePath) {
        filePath = filePath.substring(0, filePath.lastIndexOf(".jar") + 4 );

        if (filePath.startsWith("/"))
            filePath = filePath.substring(1);

        if (filePath.startsWith("file:/")) {
            filePath = filePath.substring(5);
            if(System.getProperty("os.name").toLowerCase().contains("windows"))
                filePath = filePath.substring(1);
        }
        return filePath;
    }

    public String getApiSecret() {
        return apiSecret;
    }

    public String getMeasurementId() {
        return measurementId;
    }
}
