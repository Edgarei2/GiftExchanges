package com.taiqiwen.im.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object ImApi {

    private const val BASE_URL = "http://cloud.bmob.cn/11efc6547dc1e2f6/"

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val service: IIMNetWork =
        retrofit.create<IIMNetWork>(IIMNetWork::class.java)

    fun getChannelObjIdList(channelIds: String?, cb: (Map<String, String>?) -> Unit) {
        service.getChannelObjIdMap(channelIds)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SessionObjIdResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: SessionObjIdResponseDTO) {
                    cb.invoke(t.sessionObjMap)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

    fun sendMessage(channelObjId: String, msg: String, curUserId: String, cb: (String?) -> Unit) {
        service.sendMessage(channelObjId, msg, curUserId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SendMessageResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: SendMessageResponseDTO) {
                    cb.invoke(t.result)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

}