package com.raheygaay.app.di

import android.content.Context
import com.raheygaay.app.data.remote.RaheyGaayApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        @ApplicationContext context: Context
    ): OkHttpClient {
        val cacheSize = 20L * 1024L * 1024L
        val cache = Cache(File(context.cacheDir, "http_cache"), cacheSize)
        val defaultCacheControl = CacheControl.Builder()
            .maxAge(60, TimeUnit.SECONDS)
            .build()
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addNetworkInterceptor { chain ->
                val response = chain.proceed(chain.request())
                if (response.request.method == "GET" && response.header("Cache-Control").isNullOrBlank()) {
                    response.newBuilder()
                        .header("Cache-Control", defaultCacheControl.toString())
                        .build()
                } else {
                    response
                }
            }
            .cache(cache)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.raheygaay.dev/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideRaheyGaayApi(retrofit: Retrofit): RaheyGaayApi {
        return retrofit.create(RaheyGaayApi::class.java)
    }
}
