package com.bitlove.fetlife.model.api;

import android.content.Context;

import com.bitlove.fetlife.BuildConfig;
import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import okio.Buffer;
import okio.BufferedSource;
import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

public class FetLifeService {

    public static final String BASE_URL = "https://app.fetlife.com";
    public static final String HOST_NAME = "app.fetlife.com";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_TOKEN_REFRESH = "refresh_token";
    public static final String AUTH_HEADER_PREFIX = "Bearer ";

    private final FetLifeApi fetLifeApi;
    private final FetLifeMultipartUploadApi fetLifeMultipartUploadApi;

    private int lastResponseCode = -1;

    public FetLifeService(final FetLifeApplication fetLifeApplication) throws Exception {

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("fetlife", loadCertificate(fetLifeApplication));

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(null, tmf.getTrustManagers(), null);

        OkHttpClient client = new OkHttpClient();
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return hostname.endsWith(HOST_NAME);
            }
        });
//        client.setSslSocketFactory(context.getSocketFactory());
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                //response.body().string();
                lastResponseCode = response.code();
//                if (BuildConfig.DEBUG && lastResponseCode > 299) {
//                    BufferedSource source = response.body().source();
//                    Buffer bufferedCopy = source.buffer().clone();
////                    Crashlytics.log("EXTRA LOG Failed request response" + "\n" + response.body().string());
//                    return new Response.Builder().body(ResponseBody.create(response.body().contentType(), response.body().contentLength(), bufferedCopy)).build();
//                }
                return response;
            }
        });
        client.setConnectTimeout(20, TimeUnit.SECONDS);
        client.setReadTimeout(20, TimeUnit.SECONDS);
        client.setWriteTimeout(20, TimeUnit.SECONDS);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        fetLifeApi = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper)).build()
                .create(FetLifeApi.class);

        OkHttpClient uploadClient = new OkHttpClient();
        uploadClient.setConnectTimeout(5, TimeUnit.MINUTES);
        uploadClient.setReadTimeout(5, TimeUnit.MINUTES);
        uploadClient.setWriteTimeout(5, TimeUnit.MINUTES);
        uploadClient.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return hostname.endsWith(HOST_NAME);
            }
        });
        uploadClient.setSslSocketFactory(context.getSocketFactory());
        uploadClient.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                //response.body().string();
                lastResponseCode = response.code();
                return response;
            }
        });

        fetLifeMultipartUploadApi = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(uploadClient)
                .addConverterFactory(JacksonConverterFactory.create(mapper)).build()
                .create(FetLifeMultipartUploadApi.class);

    }

    public FetLifeApi getFetLifeApi() {
        return fetLifeApi;
    }

    public FetLifeMultipartUploadApi getFetLifeMultipartUploadApi() {
        return fetLifeMultipartUploadApi;
    }

    public int getLastResponseCode() {
        return lastResponseCode;
    }

    private Certificate loadCertificate(Context context) {
        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream inputStream = context.getResources().openRawResource(R.raw.fetlife_fastly_intermediate);
            Certificate cert = cf.generateCertificate(inputStream);
            inputStream.close();
            return cert;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
