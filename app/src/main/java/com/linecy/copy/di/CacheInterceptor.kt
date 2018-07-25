package com.linecy.copy.di

import android.content.Context
import android.util.Log
import com.linecy.eyepetizer.utils.NetworkUtils
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response

/**
 * @author by linecy
 */
class CacheInterceptor(val context: Context) : Interceptor {
  override fun intercept(chain: Interceptor.Chain?): Response? {
    var request = chain?.request()
    if (NetworkUtils.isNetConnected(context)) {
      val response = chain?.proceed(request)
      // read from cache for 60 s
      val maxAge = 60
      val cacheControl = request?.cacheControl().toString()
      Log.e("CacheInterceptor", "6s load cahe$cacheControl")
      return response?.newBuilder()?.removeHeader("Pragma")?.removeHeader("Cache-Control")?.header(
          "Cache-Control", "public, max-age=$maxAge")?.build()
    } else {
      Log.e("CacheInterceptor", " no network load cahe")
      request = request?.newBuilder()?.cacheControl(CacheControl.FORCE_CACHE)?.build()
      val response = chain?.proceed(request)
      //set cahe times is 3 days
      val maxStale = 60 * 60 * 24 * 3
      return response?.newBuilder()?.removeHeader("Pragma")?.removeHeader("Cache-Control")?.header(
          "Cache-Control", "public, only-if-cached, max-stale=$maxStale")?.build()
    }
  }

}