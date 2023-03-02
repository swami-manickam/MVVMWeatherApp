package com.challange.weather.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import retrofit2.HttpException
import retrofit2.Response
import rx.Completable
import rx.Observable
import rx.Observable.Transformer
import rx.Single
import rx.android.schedulers.AndroidSchedulers
import rx.exceptions.OnErrorNotImplementedException
import rx.schedulers.Schedulers
import java.io.IOException
import java.util.Locale

object RxJavaUtils {
  const val SERVICE_UNAVAILABLE_MESSAGE = "Service Unavailable"
  @JvmStatic fun <T> applyObserverSchedulers(): Transformer<T, T> {
    return Transformer { observable: Observable<T> ->
      observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }
  }

  @JvmStatic fun applyCompletableSchedulers(): Completable.Transformer {
    return Completable.Transformer { completable: Completable ->
      completable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }
  }

  @JvmStatic fun <T> applyErrorTransformer(isForOslc: Boolean): Transformer<T, T> {
    return Transformer { observable: Observable<T> ->
      observable.onErrorResumeNext { throwable: Throwable ->
        Observable.error(
          transformThrowable(isForOslc, throwable)
        )
      }
    }
  }

  @JvmStatic fun applyCompletableErrorTransformer(isForOslc: Boolean): Completable.Transformer {
    return Completable.Transformer { completable: Completable ->
      completable.onErrorResumeNext { throwable: Throwable ->
        Completable.error(
          transformThrowable(isForOslc, throwable)
        )
      }
    }
  }

  @JvmStatic fun <T> applySingleErrorTransformer(isForOslc: Boolean): Single.Transformer<T, T> {
    return Single.Transformer { single: Single<T> ->
      single.onErrorResumeNext { throwable: Throwable ->
        Single.error(
          transformThrowable(isForOslc, throwable)
        )
      }
    }
  }

  private fun transformThrowable(
    isOslcApi: Boolean,
    throwable: Throwable
  ): Throwable {
    if (throwable is HttpException) {
      return throwable.wrapIfMaxException()
    }
    return throwable
  }

  fun <T> applyOnErrorCrasher(): Transformer<T, T> {
    return Transformer { observable: Observable<T> ->
      observable.doOnError { throwable: Throwable? ->
        val checkpoint = Throwable()
        val stackTrace = checkpoint.stackTrace
        val element =
          stackTrace[1] // First element after `crashOnError()`
        val msg = String.format(
          "onError() crash from subscribe() in %s.%s(%s:%s)",
          element.className,
          element.methodName,
          element.fileName,
          element.lineNumber
        )
        throw OnErrorNotImplementedException(msg, throwable)
      }
    }
  }

  @JvmStatic fun <T> applySingleSchedulers(): Single.Transformer<T, T> {
    return Single.Transformer { observable: Single<T> ->
      observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
    }
  }

  fun convertSuspendFunToCompletable(block: suspend CoroutineScope.() -> Unit): Completable {
    return Completable.fromCallable {
      runBlocking {
        block()
      }
    }
  }
}

fun <T> Single<T>.applySchedulers(): Single<T> {
  return compose(RxJavaUtils.applySingleSchedulers())
}

fun <T> Observable<T>.applySchedulers(): Observable<T> {
  return compose(RxJavaUtils.applyObserverSchedulers())
}

fun Completable.applySchedulers(): Completable {
  return compose(RxJavaUtils.applyCompletableSchedulers())
}

fun Response<*>.toException(
  parent: Throwable? = null
): Exception {
  return IOException(
    java.lang.String.format(
      Locale.getDefault(), "Invalid response from Server {Code: %d Body: %s}",
      code(), errorBody()?.string()
    )
  )
}

fun HttpException.wrapIfMaxException(): Exception {
  return response()?.toException(this) ?: this
}