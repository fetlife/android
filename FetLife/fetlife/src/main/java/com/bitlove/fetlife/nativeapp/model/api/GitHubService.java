package com.bitlove.fetlife.nativeapp.model.api;

import android.os.Build;

import com.bitlove.fetlife.FetLifeApplication;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.KeyStore;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class GitHubService {

    private static final String GITHUB_BASE_URL = "https://api.github.com";
    private static final String HOST_NAME = "github.com";

    private final GitHubApi gitHubApi;

    private int lastResponseCode = -1;

    public GitHubService(final FetLifeApplication fetLifeApplication) throws Exception {

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

        clientBuilder
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return hostname.endsWith(HOST_NAME);
                    }
                });


        clientBuilder.interceptors().add(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();
                Response response = chain.proceed(request);
                //response.body().string();
                lastResponseCode = response.code();
                return response;
            }
        });

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            SSLContext context = SSLContext.getInstance("TLS");
            TrustManagerFactory tmf = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init((KeyStore) null);
            TrustManager[] trustManagers = tmf.getTrustManagers();
            context.init(null,trustManagers,null);
            clientBuilder.sslSocketFactory(new TLSSocketFactory(context.getSocketFactory()));
        }

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        gitHubApi = new Retrofit.Builder()
                .baseUrl(GITHUB_BASE_URL)
                .client(clientBuilder.build())
                .addConverterFactory(JacksonConverterFactory.create(mapper)).build()
                .create(GitHubApi.class);

    }

    public GitHubApi getGitHubApi() {
        return gitHubApi;
    }

    public int getLastResponseCode() {
        return lastResponseCode;
    }
}
