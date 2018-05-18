package com.bitlove.fetlife.view.widget

import android.content.Context
import android.net.Uri
import com.bitlove.fetlife.FetLifeApplication
import com.bitlove.fetlife.model.network.FetLifeService
import com.facebook.cache.common.CacheKey
import com.facebook.common.logging.FLog
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory
import com.facebook.imagepipeline.cache.CacheKeyFactory
import com.facebook.imagepipeline.listener.RequestListener
import com.facebook.imagepipeline.request.ImageRequest
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.util.regex.Pattern
import com.facebook.imagepipeline.listener.RequestLoggingListener



//TODO consider removing and using glide
class FrescoImageLoader {

    companion object {
        private const val IMAGE_TOKEN_MIDFIX = "?token="

        fun initFrescoImageLibrary(applicationContext: Context) {

            val okHttpClientBuilder = OkHttpClient.Builder()
                    .addInterceptor(Interceptor { chain ->
                        if (!chain.request().url().host().endsWith("fetlife.com")) {
                            return@Interceptor chain.proceed(chain.request())
                        }
                        val requestBuilder = chain.request().newBuilder()
                        requestBuilder.addHeader("Referer", "fetlife.com")
                        chain.proceed(requestBuilder.build())
                    })

            val requestListeners = HashSet<RequestListener>()
            requestListeners.add(RequestLoggingListener())

            val imagePipelineConfig = OkHttpImagePipelineConfigFactory.newBuilder(applicationContext, okHttpClientBuilder.build()).setRequestListeners(requestListeners).setCacheKeyFactory(object : CacheKeyFactory {
                override fun getBitmapCacheKey(request: ImageRequest?, callerContext: Any?): CacheKey {
                    val uri = request!!.sourceUri
                    return getCacheKey(uri)
                }

                override fun getPostprocessedBitmapCacheKey(request: ImageRequest?, callerContext: Any?): CacheKey {
                    val uri = request!!.sourceUri
                    return getCacheKey(uri)
                }

                override fun getEncodedCacheKey(request: ImageRequest?, callerContext: Any?): CacheKey {
                    val uri = request!!.sourceUri
                    return getCacheKey(uri)
                }

                override fun getEncodedCacheKey(request: ImageRequest?, sourceUri: Uri?, callerContext: Any?): CacheKey {
                    return getCacheKey(sourceUri!!)
                }

                private fun getCacheKey(uri: Uri): CacheKey {
                    val imageUrl = uri.toString()
                    val cacheUrl: String

                    val imageUrlParts = imageUrl.split(Pattern.quote(IMAGE_TOKEN_MIDFIX).toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (imageUrlParts.size >= 2) {
                        cacheUrl = imageUrlParts[0]
                        val token = imageUrlParts[1]
                    } else {
                        cacheUrl = imageUrl
                    }

                    return FrescoTokenLessCacheKey(cacheUrl)

                }
            }).build()
            FLog.setMinimumLoggingLevel(FLog.VERBOSE)

            Fresco.initialize(applicationContext, imagePipelineConfig)
        }
    }

    internal class FrescoTokenLessCacheKey(private val uriAsString: String) : CacheKey {

        override fun getUriString(): String {
            return uriAsString
        }

        override fun hashCode(): Int {
            return uriAsString.hashCode()
        }

        override fun containsUri(uri: Uri): Boolean {
            return uri.toString().startsWith(uriAsString)
        }

        override fun equals(obj: Any?): Boolean {
            if (obj is FrescoTokenLessCacheKey) {
                val otherKey = obj as FrescoTokenLessCacheKey?
                return uriAsString == otherKey!!.uriAsString
            }
            return super.equals(obj)
        }

        override fun toString(): String {
            return uriAsString
        }
    }
}