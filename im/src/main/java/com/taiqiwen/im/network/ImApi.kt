package com.taiqiwen.im.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.taiqiwen.base_framework.model.GiftUser
import com.taiqiwen.im_api.model.Group
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Field

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

    fun sendMessage(channelObjId: String, msg: String, curUserId: String, receiverUid: String, cb: (String?) -> Unit) {
        service.sendMessage(channelObjId, msg, curUserId, receiverUid)
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

    fun getUnReadMessage(userId: String?, cb: (Map<String, List<String>>?) -> Unit) {
        service.getUnReadMessage(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<UnreadMessageResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: UnreadMessageResponseDTO) {
                    cb.invoke(t.unreadMessage)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

    fun getGroupInfo(userId: String?, cb: (List<Group>?) -> Unit) {
        service.getGroupChatInfo(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<GroupInfoResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: GroupInfoResponseDTO) {
                    cb.invoke(t.groupDetail)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

    fun getFriendsDetail(userId: String?, cb: (Map<String, GiftUser>?) -> Unit) {
        service.getFriendsDetail(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<FriendsDetailResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: FriendsDetailResponseDTO) {
                    cb.invoke(t.friendsDetail)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

    fun sendGroupMessage(groupObjId: String,
                         msg: String,
                         sender: String?, cb: (String?) -> Unit) {
        service.sendGroupMessage(groupObjId, msg, sender)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<SendGroupMessageResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: SendGroupMessageResponseDTO) {
                    cb.invoke(t.result)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

    fun getGroupMember(groupObjId: String, cb: (Map<String, GiftUser>?) -> Unit) {
        service.getGroupMember(groupObjId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<GroupMemberResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: GroupMemberResponseDTO) {
                    cb.invoke(t.memberInfo)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

    fun getFriendsUid(userId: String?, cb: (List<String>?) -> Unit) {
        service.getCurUserFriends(userId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<FriendsResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: FriendsResponseDTO) {
                    cb.invoke(t.friends)
                }

                override fun onError(e: Throwable) {
                    cb.invoke(null)
                }
            })
    }

    fun updateCurUserScore(roomId: String?, userId: String?, score: String?) {
        service.updateCurUserScore(roomId, userId, score)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<UpdateScoreResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: UpdateScoreResponseDTO) { }

                override fun onError(e: Throwable) { }
            })
    }

    fun endGame(roomId: String?, cb: (String?) -> Unit) {
        service.endGame(roomId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<GameResponseDTO?> {
                override fun onComplete() { }

                override fun onSubscribe(d: Disposable) { }

                override fun onNext(t: GameResponseDTO) {
                    cb.invoke(t.winnerUserId)
                }

                override fun onError(e: Throwable) { }
            })
    }

}