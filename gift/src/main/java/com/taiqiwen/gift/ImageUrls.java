package com.taiqiwen.gift;

/**
 * Created by wensefu on 2017/2/12.
 */

public class ImageUrls {

    public static String[] images = {
            "http://p3.pstatp.com/temai/c2293c4907cfe74e712c77e19e62bdbbwww800-800~tplv-obj.webp",
            "http://static.fdc.com.cn/avatar/sns/1486263782969.png",
            "http://static.fdc.com.cn/avatar/sns/1485055822651.png",
            "http://static.fdc.com.cn/avatar/sns/1486194909983.png",
            "http://static.fdc.com.cn/avatar/sns/1486194996586.png",
            "http://static.fdc.com.cn/avatar/sns/1486195059137.png",
            "http://static.fdc.com.cn/avatar/sns/1486195059137.png",
            "http://static.fdc.com.cn/avatar/sns/1486173526402.png",
            "http://static.fdc.com.cn/avatar/sns/1486173639603.png",
            "http://static.fdc.com.cn/avatar/sns/1486172566083.png"
    };

    public static String[] labels;


    static {
        labels = new String[images.length];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = "礼物 " + (i + 1);
        }
    }
}
