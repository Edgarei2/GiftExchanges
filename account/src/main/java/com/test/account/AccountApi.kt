package com.test.account

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.taiqiwen.base_framework.model.GiftUser
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import io.reactivex.schedulers.Schedulers
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory


object AccountApi {

    private const val BASE_URL = "http://cloud.bmob.cn/11efc6547dc1e2f6/"

    val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    val service: IServiceNetWork =
        retrofit.create<IServiceNetWork>(IServiceNetWork::class.java)

    fun checkLogin(userName: String,
                   password: String,
                   cb: ((Boolean) -> Unit)?,
                   accountStatusCb: ((GiftUser?) -> Unit)?) {

        service.login(userName, password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<LogInResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: LogInResponseDTO) {
                    val list = t.result
                    if (list?.isNullOrEmpty() == true) {
                        cb?.invoke(false)
                        accountStatusCb?.invoke(null)
                    } else {
                        cb?.invoke(true)
                        accountStatusCb?.invoke(list[0])
                    }
                }

                override fun onError(e: Throwable) {
                    cb?.invoke(false)
                    accountStatusCb?.invoke(null)
                }
            })
    }

    fun refreshCurUser(userId: String?, accountStatusCb: ((GiftUser?) -> Unit)?) {
        service.refreshCurUser(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<RefreshResponseDTO?> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: RefreshResponseDTO) {
                    val list = t.result
                    if (list.isNullOrEmpty()) {
                        accountStatusCb?.invoke(null)
                    } else {
                        accountStatusCb?.invoke(list[0])
                    }
                }

                override fun onError(e: Throwable) {
                    accountStatusCb?.invoke(null)
                }
            })
    }

}

interface IServiceNetWork {
    @FormUrlEncoded
    @POST("logIn")
    fun login(@Field("userName") userName: String,
              @Field("password") password: String): Observable<LogInResponseDTO?>


    @FormUrlEncoded
    @POST("refreshUserInfo")
    fun refreshCurUser(@Field("user_id") userId: String?): Observable<RefreshResponseDTO?>
}

class LogInResponseDTO(@SerializedName("results") var result: List<GiftUser>?)

class RefreshResponseDTO(@SerializedName("cur_user_info") var result: List<GiftUser>?)