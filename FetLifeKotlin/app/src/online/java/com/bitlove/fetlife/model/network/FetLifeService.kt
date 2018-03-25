package com.bitlove.fetlife.model.network

import android.content.Context
import android.media.session.MediaSession
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.network.networkobject.AuthBody
import com.bitlove.fetlife.model.network.networkobject.Token
import org.jetbrains.anko.coroutines.experimental.bg
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


//import com.bitlove.fetlife.FetLifeApplication
//import com.bitlove.fetlife.R
//import com.crashlytics.android.Crashlytics
//import com.fasterxml.jackson.databind.DeserializationFeature
//import com.fasterxml.jackson.databind.ObjectMapper
//import com.squareup.okhttp.Interceptor
//import com.squareup.okhttp.OkHttpClient
//import com.squareup.okhttp.Request
//import com.squareup.okhttp.Response
//import com.squareup.okhttp.ResponseBody
//
//import java.io.IOException
//import java.io.InputStream
//import java.security.KeyStore
//import java.security.cert.Certificate
//import java.security.cert.CertificateFactory
//import java.util.concurrent.TimeUnit
//
//import javax.net.ssl.HostnameVerifier
//import javax.net.ssl.SSLContext
//import javax.net.ssl.SSLSession
//import javax.net.ssl.TrustManagerFactory
//
//import okio.Buffer
//import okio.BufferedSource
//import retrofit.JacksonConverterFactory
//import retrofit.Retrofit
//import retrofit2.Retrofit

class FetLifeService {

    //TODO implement

    var authToken : String? = null

    var fetLifeApi: FetLifeApi

    init {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://app.fetlife.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        fetLifeApi = retrofit.create(FetLifeApi::class.java)
    }

    fun login() {
        val tokenCall : Call<Token> = fetLifeApi.login(
                "d8f8ebd522bf5123c3f29db3c8faf09029a032b44f0d1739d4325cd3ccf11570",
                "47273306a9a3a3448a908748eff13a21a477cc46f6a3968b5c7d05611c4f2f26",
                "urn:ietf:wg:oauth:2.0:oob",
                AuthBody("dreamlite", "isThisMyPassword?"))
        val result = tokenCall.execute()
        authToken = "Bearer " + result.body()?.accessToken
    }

//    val fetLifeMultipartUploadApi: FetLifeMultipartUploadApi
//
//    var lastResponseCode = -1
//        private set
//
//    init {
//
//        val keyStoreType = KeyStore.getDefaultType()
//        val keyStore = KeyStore.getInstance(keyStoreType)
//        keyStore.load(null, null)
//        keyStore.setCertificateEntry("fetlife", loadCertificate(fetLifeApplication))
//
//        val tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm()
//        val tmf = TrustManagerFactory.getInstance(tmfAlgorithm)
//        tmf.init(keyStore)
//
//        val context = SSLContext.getInstance("TLS")
//        context.init(null, tmf.trustManagers, null)
//
//        val client = OkHttpClient()
//        client.setHostnameVerifier(HostnameVerifier { hostname, session -> hostname.endsWith(HOST_NAME) })
//        client.setSslSocketFactory(context.socketFactory)
//        client.interceptors().add(object : Interceptor() {
//            @Throws(IOException::class)
//            fun intercept(chain: Chain): Response {
//                val request = chain.request()
//                val response = chain.proceed(request)
//                //response.body().string();
//                lastResponseCode = response.code()
//                if (lastResponseCode > 299) {
//                    val source = response.body().source()
//                    val bufferedCopy = source.buffer().clone()
//                    Crashlytics.log("EXTRA LOG Failed request response" + "\n" + response.body().string())
//                    return Response.Builder().body(ResponseBody.create(response.body().contentType(), response.body().contentLength(), bufferedCopy)).build()
//                }
//                return response
//            }
//        })
//        client.setConnectTimeout(20, TimeUnit.SECONDS)
//        client.setReadTimeout(20, TimeUnit.SECONDS)
//        client.setWriteTimeout(20, TimeUnit.SECONDS)
//
//        val mapper = ObjectMapper()
//        mapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false)
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
//        fetLifeApi = Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .client(client)
//                .addConverterFactory(JacksonConverterFactory.create(mapper)).build()
//                .create(FetLifeApi::class.java)
//
//        val uploadClient = OkHttpClient()
//        uploadClient.setConnectTimeout(5, TimeUnit.MINUTES)
//        uploadClient.setReadTimeout(5, TimeUnit.MINUTES)
//        uploadClient.setWriteTimeout(5, TimeUnit.MINUTES)
//        uploadClient.setHostnameVerifier(HostnameVerifier { hostname, session -> hostname.endsWith(HOST_NAME) })
//        uploadClient.setSslSocketFactory(context.socketFactory)
//        uploadClient.interceptors().add(object : Interceptor() {
//            @Throws(IOException::class)
//            fun intercept(chain: Chain): Response {
//                val request = chain.request()
//                val response = chain.proceed(request)
//                //response.body().string();
//                lastResponseCode = response.code()
//                return response
//            }
//        })
//
//        fetLifeMultipartUploadApi = Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .client(uploadClient)
//                .addConverterFactory(JacksonConverterFactory.create(mapper)).build()
//                .create(FetLifeMultipartUploadApi::class.java)
//
//    }
//
//    private fun loadCertificate(context: Context): Certificate {
//        try {
//            val cf = CertificateFactory.getInstance("X.509")
//            val inputStream = context.resources.openRawResource(R.raw.fetlife_fastly_intermediate)
//            val cert = cf.generateCertificate(inputStream)
//            inputStream.close()
//            return cert
//        } catch (t: Throwable) {
//            throw RuntimeException(t)
//        }
//
//    }
//
//    companion object {
//
//        val BASE_URL = "https://app.fetlife.com"
//        val HOST_NAME = "app.fetlife.com"
//        val GRANT_TYPE_PASSWORD = "password"
//        val GRANT_TYPE_TOKEN_REFRESH = "refresh_token"
//        val AUTH_HEADER_PREFIX = "Bearer "
//    }

}
