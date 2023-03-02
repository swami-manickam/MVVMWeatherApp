package com.challange.weather.data.di.modules

import android.content.Context
import com.challange.weather.BuildConfig
import com.challange.weather.app.AppController
import com.challange.weather.data.api.WeatherApi
import com.challange.weather.data.preference.AppPreference
import com.challange.weather.data.repo.WeatherRepo
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

private const val CONNECT_TIMEOUT = 2
private const val READ_TIMEOUT = 2
private const val WRITE_TIMEOUT = 2

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {

  @Provides @Singleton fun providesContext(): Context {
    return AppController.getInstance()!!
  }

  @Provides
  @Singleton
  fun providesAppPreference(context: Context): AppPreference {
    return AppPreference(context)
  }

  @Provides
  @Singleton
  fun providesGson(): Gson {
    return GsonBuilder().serializeNulls().setLenient().create()
  }

  @Provides
  @Named("AppBaseUrl")
  fun providesEnvironmentUrl(): String {
    return BuildConfig.base_url
  }

  @Provides
  @Singleton
  fun providesLoggingInterceptor(): HttpLoggingInterceptor {
    val logging = HttpLoggingInterceptor()
    logging.setLevel(
      if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    )
    return logging
  }

  @Provides
  @Singleton
  fun provideOkHttpClient(logger: HttpLoggingInterceptor): OkHttpClient {
    return OkHttpClient.Builder()
      .addInterceptor(logger)
      .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.MINUTES)
      .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.MINUTES)
      .writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.MINUTES)
      .build()
  }

  @Provides
  @Singleton
  fun provideRetrofit(
    okHttpClient: OkHttpClient,
    gson: Gson
  ): Retrofit {
    return Retrofit.Builder()
      .baseUrl(BuildConfig.base_url)
      .addCallAdapterFactory(RxJavaCallAdapterFactory.createAsync())
      .addConverterFactory(GsonConverterFactory.create(gson))
      .client(okHttpClient)
      .build()
  }

  @Provides
  @Singleton
  fun provideUserApiService(retrofit: Retrofit): WeatherApi = retrofit.create(WeatherApi::class.java)

  @Provides
  @Singleton
  fun providesUserRepo(
    httpClient: OkHttpClient,
    gson: Gson,
    @Named("AppBaseUrl") baseUrl: String,
    weatherApi: WeatherApi,
  ): WeatherRepo {
    Retrofit.Builder().client(httpClient)
      .baseUrl(baseUrl)
      .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(gson)).build()
    return WeatherRepo(weatherApi)
  }

}