package com.challange.weather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class NetworkUtils {

  private static final int CONNECTION_TIMEOUT = 1;

  /**
   * Check the Internet connection available status
   *
   * @param context - Context environment passed by this parameter
   * @return boolean true if the Internet Connection is Available and false otherwise
   */
  public static boolean isConnected(Context context) {
    //Connectivity manager instance
    ConnectivityManager manager =
        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    // Fetch Active internet connection from network info
    NetworkInfo netInfo = manager.getActiveNetworkInfo();
    // return the network connection is active or not.
    return netInfo != null && netInfo.isConnectedOrConnecting();
  }

  public static OkHttpClient getUnsafeOkHttpClient() {
    try {
      // Create a trust manager that does not validate certificate chains
      final TrustManager[] trustAllCerts = new TrustManager[] {
              new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
                                               String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
                                               String authType) {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                  return new java.security.cert.X509Certificate[] {};
                }
              }
      };

      HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
      logging.setLevel(HttpLoggingInterceptor.Level.BODY);

      // Install the all-trusting trust manager
      final SSLContext sslContext = SSLContext.getInstance("SSL");
      sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

      // Create an ssl socket factory with our all-trusting manager
      final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

      OkHttpClient.Builder builder = new OkHttpClient.Builder();
      builder.sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0]);
      builder.hostnameVerifier((hostname, session) -> true);
      builder.connectTimeout(CONNECTION_TIMEOUT, TimeUnit.MINUTES)
              .readTimeout(CONNECTION_TIMEOUT, TimeUnit.MINUTES)
              .writeTimeout(CONNECTION_TIMEOUT, TimeUnit.MINUTES)
              .addInterceptor(logging);

      return builder.build();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
