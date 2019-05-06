package com.bitlove.fetlife.nativeapp.model.api;

import android.content.Context;
import android.os.Build;

import com.bitlove.fetlife.FetLifeApplication;
import com.bitlove.fetlife.R;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

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

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class FetLifeService {

    public static final String BASE_URL = "https://fetlife.com";
    public static final String HOST_NAME = "fetlife.com";
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
        context.init(null, /*tmf.getTrustManagers()*/ null, null);

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        clientBuilder
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return hostname.endsWith(HOST_NAME);
                    }
                }).sslSocketFactory(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? new TLSSocketFactory(context.getSocketFactory()) : context.getSocketFactory());


        clientBuilder.interceptors().add(new Interceptor() {
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

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        fetLifeApi = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(JacksonConverterFactory.create(mapper)).build()
                .create(FetLifeApi.class);

        OkHttpClient.Builder uploadClientBuilder = new OkHttpClient.Builder();

        uploadClientBuilder
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return hostname.endsWith(HOST_NAME);
                    }
                }).sslSocketFactory(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP ? new TLSSocketFactory(context.getSocketFactory()) : context.getSocketFactory());

        uploadClientBuilder.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                //response.body().string();
                lastResponseCode = response.code();
//                lastResponseBody = response.body() != null ? response.body().string() : "null";
                return response;
            }
        });

        fetLifeMultipartUploadApi = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(uploadClientBuilder.build())
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
