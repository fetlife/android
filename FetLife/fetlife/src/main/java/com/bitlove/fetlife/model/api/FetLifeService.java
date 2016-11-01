package com.bitlove.fetlife.model.api;

import android.content.Context;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import retrofit.JacksonConverterFactory;
import retrofit.Retrofit;

public class FetLifeService {

    private static final String LOGON_BASE_URL = "https://fetlife.com";
    private static final String HOST_NAME = "fetlife.com";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String GRANT_TYPE_TOKEN_REFRESH = "refresh_token";
    public static final String AUTH_HEADER_PREFIX = "Bearer ";

    private final FetLifeApi fetLifeApi;

    private int lastResponseCode = -1;

    public FetLifeService(final FetLifeApplication fetLifeApplication) throws Exception {

//        String keyStoreType = KeyStore.getDefaultType();
//        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//        keyStore.load(null, null);
//        keyStore.setCertificateEntry("fetlife", loadCertificate(fetLifeApplication));
//
//        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//        tmf.init(keyStore);
//
//        SSLContext context = SSLContext.getInstance("TLS");
//        context.init(null, tmf.getTrustManagers(), null);

        OkHttpClient client = new OkHttpClient();
        client.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return hostname.endsWith(HOST_NAME);
            }
        });
        //client.setSslSocketFactory(context.getSocketFactory());
        client.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                //response.body().string();
                lastResponseCode = response.code();
                return response;
            }
        });

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        fetLifeApi = new Retrofit.Builder()
                .baseUrl(LOGON_BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper)).build()
                .create(FetLifeApi.class);
    }

    public FetLifeApi getFetLifeApi() {
        return fetLifeApi;
    }

    public int getLastResponseCode() {
        return lastResponseCode;
    }

    private Certificate loadCertificate(Context context) {

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream inputStream = context.getResources().openRawResource(R.raw.fetlife);
            Certificate cert = cf.generateCertificate(inputStream);
            inputStream.close();
            return cert;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

}
