package com.taiqiwen.gift.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.beyondsw.lib.model.GiftDetailDTO
import com.test.account_api.AccountServiceUtil
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

object GiftApi {

    private const val BASE_URL = "http://cloud.bmob.cn/11efc6547dc1e2f6/"

    private val gson: Gson = GsonBuilder()
        .setLenient() // 设置GSON的非严格模式setLenient()
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val service: IGiftNetWork =
        retrofit.create<IGiftNetWork>(IGiftNetWork::class.java)

    fun fetchFrontPageCards(cb: ((Boolean, List<GiftDetailDTO>) -> Unit)?) {
        service.getFrontPageCards()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<FrontPageCardsResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: FrontPageCardsResponseDTO) {
                    val list = t.result
                    if (list.isNullOrEmpty()) {
                        cb?.invoke(false, emptyList())
                    } else {
                        cb?.invoke(true, list)
                    }
                }

                override fun onError(e: Throwable) {
                    cb?.invoke(false, emptyList())
                }
            })
    }

    fun fetchComment(giftId: String?, cb: ((Boolean, GiftCommentResponseDTO?) -> Unit)?) {
        service.getGiftComment(giftId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<GiftCommentResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: GiftCommentResponseDTO) {
                    cb?.invoke(true, t)
                }

                override fun onError(e: Throwable) {
                    cb?.invoke(false, null)
                }
            })
    }

    fun fetchWantedUsers(giftId: String): Observable<List<String>?> {
        return service.getWantedFriends(giftId)
            .subscribeOn(Schedulers.io())
            .map { it.wantedUsers }
    }

    fun fetchCurUserFriends(userId: String): Observable<List<String>?> {
        return service.getCurUserFriends(userId)
            .subscribeOn(Schedulers.io())
            .map { it.friends }
    }

    fun getWantedFriends(giftId: String, userId: String, cb: ((Boolean, Boolean, List<String>?) -> Unit)?) {
        val wantedUsers = fetchWantedUsers(giftId)
        val friends = fetchCurUserFriends(userId)
        var curUserWanted = false
        Observable.zip(wantedUsers, friends, object : BiFunction<List<String>?, List<String>?, List<String>> {
            override fun apply(wantedUsers: List<String>, friends: List<String>): List<String> {
                val wantedFriends = mutableListOf<String>()
                val curUserId = AccountServiceUtil.getSerVice().getCurUser()?.userId
                if (wantedUsers.contains(curUserId)) {
                    curUserWanted = true
                }
                for (friend in friends) {
                    if (wantedUsers.contains(friend)) {
                        wantedFriends.add(friend)
                    }
                }
                return wantedFriends
            }
        }).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<List<String>> {
            override fun onComplete() { }

            override fun onSubscribe(d: Disposable) { }

            override fun onNext(t: List<String>) {
                cb?.invoke(true, curUserWanted, t)
            }

            override fun onError(e: Throwable) {
                cb?.invoke(false, curUserWanted, null)
            }
        })
    }

    fun collectGift(giftId: String?, userId: String?, cb: ((Boolean) -> Unit)?) {
        service.collectGift(userId, giftId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<CollectResponseDTO?> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: CollectResponseDTO) {
                    if (t.result == "1") {
                        cb?.invoke(true)
                    } else {
                        cb?.invoke(false)
                    }
                }

                override fun onError(e: Throwable) {
                    cb?.invoke(false)
                }
            })
    }

    fun getCollectedGifts(userId: String?, cb: ((List<GiftDetailDTO>?) -> Unit)?) {
        service.getCollectedGifts(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<CollectedGiftsResponseDTO?> {
                override fun onComplete() {}

                override fun onSubscribe(d: Disposable) {}

                override fun onNext(t: CollectedGiftsResponseDTO) {
                    cb?.invoke(t.result)
                }

                override fun onError(e: Throwable) {
                    cb?.invoke(null)
                }
            })
    }

}
interface IGiftNetWork {

    @GET("getFrontCards")
    fun getFrontPageCards(): Observable<FrontPageCardsResponseDTO?>

    @FormUrlEncoded
    @POST("getComment")
    fun getGiftComment(@Field("gift_id") giftId: String?): Observable<GiftCommentResponseDTO?>

    @FormUrlEncoded
    @POST("getWantedUsers")
    fun getWantedFriends(@Field("gift_id") giftId: String?): Observable<WantedUsersResponseDTO?>

    @FormUrlEncoded
    @POST("getCurUserFriends")
    fun getCurUserFriends(@Field("user_id") userId: String?): Observable<FriendsResponseDTO?>

    @FormUrlEncoded
    @POST("collectGift")
    fun collectGift(@Field("user_id") userId: String?, @Field("gift_id") giftId: String?): Observable<CollectResponseDTO?>

    @FormUrlEncoded
    @POST("getCollectedGifts")
    fun getCollectedGifts(@Field("user_id") userId: String?): Observable<CollectedGiftsResponseDTO?>
}

class FrontPageCardsResponseDTO(@SerializedName("results") var result: List<GiftDetailDTO>?)

class GiftCommentResponseDTO(
    @SerializedName("avatar_url") var avatarUrl: String?,
    @SerializedName("user_name") var userName: String?,
    @SerializedName("content") var content: String?
)

class WantedUsersResponseDTO(
    @SerializedName("wanted_users") var wantedUsers: List<String>?
)

class FriendsResponseDTO(
    @SerializedName("cur_friends") var friends: List<String>?
)

class CollectResponseDTO(
    @SerializedName("result") var result: String?
)

class CollectedGiftsResponseDTO(@SerializedName("results") var result: List<GiftDetailDTO>?)
