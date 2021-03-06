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

    fun fetchUserDetail(userId: String?, cb: ((GiftUser?) -> Unit)) {
        service.getFriendsDetail(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<FriendsDetailResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: FriendsDetailResponseDTO) {
                    cb.invoke(t.friendsDetail?.get(userId))
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
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

    fun fetchGiftDetail(giftIds: String?, cb: ((GiftDetailDTO?) -> Unit)) {
        service.getCertainGiftDetail(giftIds).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<GiftDetailResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: GiftDetailResponseDTO) {
                    cb.invoke(t.giftDetail[0])
                }

                override fun onError(e: Throwable) {
                   cb.invoke(null)
                }
            })
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

    fun fetchOwnedGiftsWithoutSenderInfo(userId: String?, cb: (List<GiftDetailDTO>?) -> Unit) {
        service.getOwnedGifts(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<OwnedGiftsResponseDTO?>{
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: OwnedGiftsResponseDTO) {
                    cb.invoke(t.ownedGifts)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
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

    fun fetchUnCheckedGifts(userId: String?, cb: (List<GiftSentStatusDTO>?) -> Unit) {
        service.fetchUnCheckedGifts(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<UnCheckedGiftsResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: UnCheckedGiftsResponseDTO) {
                    cb.invoke(t.uncheckedGifts)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

    fun checkoutGift(exchangeObjId: String?, giftObjId: String?, userId: String?, cb: (String?) -> Unit) {
        service.checkoutGift(exchangeObjId, giftObjId, userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<CheckoutGiftResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: CheckoutGiftResponseDTO) {
                    cb.invoke(t.checkoutResult)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

    fun sendGift(senderUid: String?,
                 receiverUid: String?,
                 giftId: String?,
                 words: String?,
                 cb: (Boolean) -> Unit) {
        service.sendGift(senderUid, receiverUid, giftId, words)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SendGiftResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: SendGiftResponseDTO) {
                    cb.invoke(t.sendResult == "1")
                }

                override fun onError(e: Throwable) {
                    cb.invoke(false)
                }
            })
    }

    fun checkoutAllGifts(exchangeObjIds: String?,
                        giftObjIds: String?,
                        owner: String?,
                        cb: (String?) -> Unit) {
        service.checkoutAllGifts(exchangeObjIds, giftObjIds, owner)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<CheckoutAllGiftsResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: CheckoutAllGiftsResponseDTO) {
                    cb.invoke(t.result)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

    fun declineAllGifts(exchangeObjIds: String?,
                        cb: (String?) -> Unit) {
        service.declineAllGifts(exchangeObjIds)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<DeclineAllGiftsResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: DeclineAllGiftsResponseDTO) {
                    cb.invoke(t.result)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

    fun inviteGame(userId: String?, userName: String?, inviteId: String?, cb: ((String?) -> Unit)?) {
        service.inviteGame(userId, userName, inviteId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<InviteGameResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: InviteGameResponseDTO) {
                    cb?.invoke(t.result)
                }

                override fun onError(e: Throwable) {
                    cb?.invoke(null)
                }
            })
    }

    fun enterGameCheck(userId: String?, roomId: String?, cb: ((String?, String?) -> Unit)?) {
        service.enterGameCheck(userId, roomId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<EnterGameResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: EnterGameResponseDTO) {
                    cb?.invoke(t.result, t.beginNow)
                }

                override fun onError(e: Throwable) {
                    cb?.invoke(null, null)
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

    @FormUrlEncoded
    @POST("fetchUnCheckedGifts")
    fun fetchUnCheckedGifts(@Field("user_id") userId: String?): Observable<UnCheckedGiftsResponseDTO?>

    @FormUrlEncoded
    @POST("checkoutGift")
    fun checkoutGift(
        @Field("exchange_obj_id") objId: String?,
        @Field("gift_obj_id")giftObjId: String?,
        @Field("user_id")userId: String?
    ): Observable<CheckoutGiftResponseDTO?>

    @FormUrlEncoded
    @POST("sendGift")
    fun sendGift(
        @Field("sender_uid") senderUid: String?,
        @Field("receiver_uid") receiverUid: String?,
        @Field("gift_id") giftId: String?,
        @Field("words") words: String?
    ): Observable<SendGiftResponseDTO?>

    @FormUrlEncoded
    @POST("checkoutAllGifts")
    fun checkoutAllGifts(
        @Field("exchange_obj_ids") exchangeObjIds: String?,
        @Field("gift_obj_ids") giftObjIds: String?,
        @Field("owner") owner: String?
    ): Observable<CheckoutAllGiftsResponseDTO?>

    @FormUrlEncoded
    @POST("declineAllGifts")
    fun declineAllGifts(
        @Field("exchange_obj_ids") exchangeObjIds: String?
    ): Observable<DeclineAllGiftsResponseDTO?>

    @FormUrlEncoded
    @POST("inviteGame")
    fun inviteGame(
        @Field("user_id") userId: String?,
        @Field("invite_name") userName: String?,
        @Field("invite_id") inviteId: String?
    ): Observable<InviteGameResponseDTO?>

    @FormUrlEncoded
    @POST("enterGameCheck")
    fun enterGameCheck(
        @Field("user_id") userId: String?,
        @Field("room_id") userName: String?
    ): Observable<EnterGameResponseDTO?>

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

class UnCheckedGiftsResponseDTO(
    @SerializedName("unchecked_exchanges") var uncheckedGifts: List<GiftSentStatusDTO>?
)

class CheckoutGiftResponseDTO(
    @SerializedName("checkout_result") var checkoutResult: String?
)

class SendGiftResponseDTO(
    @SerializedName("send_result") var sendResult: String?
)

class CheckoutAllGiftsResponseDTO(
    @SerializedName("checkout_all_result") var result: String?
)

class DeclineAllGiftsResponseDTO(
    @SerializedName("decline_all_result") var result: String?
)

class InviteGameResponseDTO(
    @SerializedName("game_channel_id") var result: String?
)

class EnterGameResponseDTO(
    @SerializedName("result") var result: String?,
    @SerializedName("begin_now") var beginNow: String?)