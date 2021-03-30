package com.taiqiwen.base_framework.model;

import android.text.TextUtils;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Keep;

@Keep
public class UrlModel implements Serializable {

    @SerializedName("uri")
    protected String uri;

    // for gson 解析和转换  别删
    @SerializedName("url_list")
    protected List<String> urlList;

    @SerializedName("width")
    int width;

    @SerializedName("height")
    int height;

    @SerializedName("url_key")
    String urlKey;

    @SerializedName("data_size")
    long size;// 单位B  视频下载大小


    // 文件md5值，用于校验完整性
    @SerializedName("file_hash")
    String fileHash;

    @SerializedName("player_access_key")
    protected String aK;

    // 文件checksum值，用于防劫持
    @SerializedName("file_cs")
    protected String fileCheckSum;

    @SerializedName("download_url_list")
    protected List<String> downUrlList;// 带有水印的下载图


    public List<String> getDownUrlList() {
        return downUrlList;
    }

    public String getaK() {
        return aK;
    }

    public void setaK(String aK) {
        this.aK = aK;
    }

    public String getFileCheckSum() {
        return fileCheckSum;
    }

    public void setFileCheckSum(String fileCheckSum) {
        this.fileCheckSum = fileCheckSum;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrlKey() {
        return urlKey;
    }

    public void setUrlKey(String urlKey) {
        this.urlKey = urlKey;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public List<String> getUrlList() {
        return urlList;
    }

    public void setUrlList(List<String> urlList) {
        this.urlList = urlList;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UrlModel)) {
            return false;
        }
        UrlModel urlModel = (UrlModel) o;
        if (uri != null ? !uri.equals(urlModel.uri) : urlModel.uri != null) {
            return false;
        }
        if (urlKey != null ? !urlKey.equals(urlModel.urlKey) : urlModel.urlKey != null) {
            return false;
        }
        return urlList != null ? urlList.equals(urlModel.urlList) : urlModel.urlList == null;
    }

    @Override
    public int hashCode() {
        String key = TextUtils.isEmpty(urlKey) ? uri : urlKey;
        int result = key != null ? key.hashCode() : 0;
        result = 31 * result + (urlList != null ? urlList.hashCode() : 0);
        return result;
    }

    // public static String toJsonString(UrlModel urlModel) {
    // if (null == urlModel) {
    // return "";
    // }
    // return JSON.toJSONString(urlModel);
    // }
    //
    // @Nullable
    // public static UrlModel fromJson(JSONObject object) {
    // return JSON.parseObject(object.toString(), UrlModel.class);
    // }
    // <Aweme DTO Generator>
    public UrlModel() {
    }

}
