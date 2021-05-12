package com.taiqiwen.im_api.model

class GameScoreStatus(
    var myScore: String,
    var friendScore: String) {

    fun isGameValid() : Boolean{
        return myScore != "-1" && friendScore != "-1"
    }

}