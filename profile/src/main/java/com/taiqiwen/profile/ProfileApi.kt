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
import io.reactivex.functions.BiFunction
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

    private fun fetchSentGiftStatus(senderUid: String?) : Observable<List<GiftSentStatusDTO?>> {
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

    private fun fetchReceivedGifts(userId: String?): Observable<Map<String, String>?> {
        return service.getReceivedGifts(userId)
            .subscribeOn(Schedulers.io())
            .map { it.receivedGifts }
    }

    private fun fetchOwnedGifts(userId: String?): Observable<List<GiftDetailDTO>?> {
        return service.getOwnedGifts(userId)
            .subscribeOn(Schedulers.io())
            .map { it.ownedGifts }
    }

    fun fetchOwnedGiftsDetail(userId: String?, cb: ((List<Pair<GiftDetailDTO, String?>>) -> Unit)?) {
        val receivedGifts = fetchReceivedGifts(userId)
        val ownedGifts = fetchOwnedGifts(userId)
        Observable.zip(receivedGifts, ownedGifts, object : BiFunction<Map<String, String>?, List<GiftDetailDTO>?, List<Pair<GiftDetailDTO, String?>>> {
            override fun apply(giftsExchanges: Map<String, String>, giftsOwned: List<GiftDetailDTO>): List<Pair<GiftDetailDTO, String?>> {
                val result = mutableListOf<Pair<GiftDetailDTO, String?>>()
                for (gift in giftsOwned) {
                    if (giftsExchanges.containsKey(gift.id)) {
                        result.add(Pair(gift, giftsExchanges[gift.id]))
                    } else {
                        result.add(Pair(gift, null))
                    }
                }
                return result
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(object : Observer<List<Pair<GiftDetailDTO, String?>>> {
            override fun onComplete() { }

            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: List<Pair<GiftDetailDTO, String?>>) {
                cb?.invoke(t)
            }

            override fun onError(e: Throwable) {
                cb?.invoke(emptyList())
            }
        } )
    }

    fun restoreGift2Credit(
        giftObjId: String?,
        userObjId: String?,
        credit: String?,
        curCredit: String?,
        cb: (String?) -> Unit
    ) {
        service.restoreGift2Credit(giftObjId, userObjId, credit, curCredit)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<RestoreGiftResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: RestoreGiftResponseDTO) {
                    cb.invoke(t.result)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

    fun takeGift(giftObjId: String?, cb: (String?) -> Unit){
        service.takeGift(giftObjId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<TakeGiftResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: TakeGiftResponseDTO) {
                    cb.invoke(t.result)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
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

    @FormUrlEncoded
    @POST("getReceivedGifts")
    fun getReceivedGifts(@Field("user_id") userId: String?): Observable<ReceivedGiftsResponseDTO?>

    @FormUrlEncoded
    @POST("getOwnedGifts")
    fun getOwnedGifts(@Field("user_id") userId: String?): Observable<OwnedGiftsResponseDTO?>

    @FormUrlEncoded
    @POST("restoreGift2Credit")
    fun restoreGift2Credit(
        @Field("gift_obj_id") giftObjId: String?,
        @Field("user_obj_id") userObjId: String?,
        @Field("credit") credit: String?,
        @Field("cur_credit") curCredit: String?
    ): Observable<RestoreGiftResponseDTO?>

    @FormUrlEncoded
    @POST("takeGift")
    fun takeGift(@Field("gift_obj_id") giftObjId: String?): Observable<TakeGiftResponseDTO?>

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

class ReceivedGiftsResponseDTO(
    @SerializedName("received_gifts") var receivedGifts: Map<String, String>?
)

class OwnedGiftsResponseDTO(
    @SerializedName("owned_gifts") var ownedGifts: List<GiftDetailDTO>?
)

class RestoreGiftResponseDTO(
    @SerializedName("restore_result") var result: String?
)

class TakeGiftResponseDTO(
    @SerializedName("take_out_result") var result: String?
)