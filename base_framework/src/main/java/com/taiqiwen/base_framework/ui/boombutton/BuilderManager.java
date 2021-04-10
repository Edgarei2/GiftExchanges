package com.taiqiwen.base_framework.ui.boombutton;

import com.nightonke.boommenu.BoomButtons.HamButton;

public class BuilderManager {

    private static int[] imageResources = null;

    private static int imageResourceIndex = 0;

    static int getImageResource() {
        assert imageResources != null;
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }

    public static HamButton.Builder getHamButtonBuilder(int titleRes, int subTitleRes) {
        return new HamButton.Builder()
                .normalImageRes(getImageResource())
                .normalTextRes(titleRes)
                .subNormalTextRes(subTitleRes);
    }

    public static void setImageResources(int[] resources) {
        imageResources = resources;
    }

    private static BuilderManager ourInstance = new BuilderManager();

    public static BuilderManager getInstance() {
        return ourInstance;
    }

    private BuilderManager() {
    }
}
