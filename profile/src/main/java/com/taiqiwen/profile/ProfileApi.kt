package com.taiqiwen.profile

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

object ProfileApi {

    private const val BASE_URL = "http://cloud.bmob.cn/11efc6547dc1e2f6/"

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val service: IProfileNetWork =
        retrofit.create<IProfileNetWork>(IProfileNetWork::class.java)

    fun fetchCurUserFriends(userId: String, cb: ((Boolean, FriendsResponseDTO?) -> Unit)?) {
        service.getCurUserFriends(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<FriendsResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: FriendsResponseDTO) {
                    cb?.invoke(true, t)
                }

                override fun onError(e: Throwable) {
                    cb?.invoke(false, null)

                }
            })
    }

}

interface IProfileNetWork {

    @FormUrlEncoded
    @POST("getCurUserFriends")
    fun getCurUserFriends(@Field("user_id") userId: String?): Observable<FriendsResponseDTO?>
}

class FriendsResponseDTO(
    @SerializedName("cur_friends") var friends: List<String>?
)