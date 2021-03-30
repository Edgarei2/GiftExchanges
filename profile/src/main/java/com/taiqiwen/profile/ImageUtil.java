package com.taiqiwen.profile;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.SimpleCacheKey;
import com.facebook.drawee.backends.pipeline.Fresco;

import androidx.annotation.Nullable;

public class ImageUtil {

    @Nullable
    public static Bitmap getBitmapFromFresco(String url) {
        FileBinaryResource resource =
                (FileBinaryResource) Fresco.getImagePipelineFactory().getMainFileCache().getResource(new SimpleCacheKey(url));
        if (resource != null && resource.getFile() != null) {
            return BitmapFactory.decodeFile(resource.getFile().getAbsolutePath());
        } else return null;
    }

    public static String avatarUrl = null;
}
