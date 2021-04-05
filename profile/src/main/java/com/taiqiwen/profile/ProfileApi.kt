package com.taiqiwen.profile

import android.content.BroadcastReceiver
import android.os.Build
import androidx.annotation.RequiresApi
import com.beyondsw.lib.model.GiftDetailDTO
import com.beyondsw.lib.model.GiftSentStatusDTO
import com.beyondsw.lib.model.GiftSentStatusDetailDTO
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.taiqiwen.base_framework.model.GiftUser
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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

    fun fetchCurUserFriendsDetail(userIds: String?, cb: ((FriendsDetailResponseDTO?) -> Unit)?) {
        service.getFriendsDetail(userIds)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<FriendsDetailResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: FriendsDetailResponseDTO) {
                    cb?.invoke(t)
                }

                override fun onError(e: Throwable) {
                    cb?.invoke(null)
                }
            })
    }

    fun fetchSentGiftStatus(senderUid: String?) : Observable<List<GiftSentStatusDTO?>> {
        return service.getSentGiftsStatus(senderUid).map { it.sendStatus }
    }

    fun fetchCertainGiftDetail(giftIds: String?): Observable<List<GiftDetailDTO?>> {
        return service.getCertainGiftDetail(giftIds).map { it.giftDetail }
    }

    fun fetchGiftSentInfo(senderUid: String?, cb: ((List<GiftSentStatusDetailDTO>?) -> Unit)?) {
        val sentStatus = fetchSentGiftStatus(senderUid)
        var sendStatusList = emptyList<GiftSentStatusDTO?>()
        sentStatus.subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMap(
                object : Function<List<GiftSentStatusDTO?>, ObservableSource<List<GiftDetailDTO?>>> {
                    override fun apply(t: List<GiftSentStatusDTO?>): ObservableSource<List<GiftDetailDTO?>> {
                        sendStatusList = t
                        val giftIds = t.map { it?.giftId }.joinToString(",")
                        return fetchCertainGiftDetail(giftIds)
                    }
                }
            )
            .observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<List<GiftDetailDTO?>> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: List<GiftDetailDTO?>) {
                    val list = mutableListOf<GiftSentStatusDetailDTO>()
                    val len = t.size
                    for (index in 0 until len) {
                        list.add(GiftSentStatusDetailDTO(
                            sendStatusList[index]?.receiver,
                            sendStatusList[index]?.status,
                            t[index]
                        ))
                    }
                    cb?.invoke(list)
                }

                override fun onError(e: Throwable) {
                    cb?.invoke(emptyList())
                }
            })
    }

}

interface IProfileNetWork {

    @FormUrlEncoded
    @POST("getCurUserFriends")
    fun getCurUserFriends(@Field("user_id") userId: String?): Observable<FriendsResponseDTO?>

    @FormUrlEncoded
    @POST("getFriendsDetail")
    fun getFriendsDetail(@Field("user_ids") userIds: String?): Observable<FriendsDetailResponseDTO?>

    @FormUrlEncoded
    @POST("getSentGifts")
    fun getSentGiftsStatus(@Field("sender_uid") senderUid: String?): Observable<GiftSentStatusResponseDTO?>

    @FormUrlEncoded
    @POST("getCertainGiftDetail")
    fun getCertainGiftDetail(@Field("gift_ids") giftIds: String?): Observable<GiftDetailResponseDTO?>

}

class FriendsResponseDTO(
    @SerializedName("cur_friends") var friends: List<String>?
)

class FriendsDetailResponseDTO(
    @SerializedName("friends_detail") var friendsDetail: Map<String, GiftUser>?
)

class GiftSentStatusResponseDTO(
    @SerializedName("gifts_sent") var sendStatus: List<GiftSentStatusDTO?>
)

class GiftDetailResponseDTO(
    @SerializedName("gift_detail") var giftDetail: List<GiftDetailDTO?>
)