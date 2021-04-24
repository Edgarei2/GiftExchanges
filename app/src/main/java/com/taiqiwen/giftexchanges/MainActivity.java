package com.taiqiwen.giftexchanges;

import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;

import com.andexert.library.RippleView;
import com.beyondsw.lib.GiftServiceUtil;
import com.isanwenyu.tabview.TabGroup;
import com.isanwenyu.tabview.TabView;
import com.taiqiwen.base_framework.EventBusWrapper;
import com.taiqiwen.base_framework.LocalStorageHelper;
import com.taiqiwen.base_framework.ShareViewModel;
import com.taiqiwen.base_framework.model.GiftChangedEvent;
import com.taiqiwen.base_framework.model.GiftUser;
import com.taiqiwen.base_framework.ui.titlebar.CommonTitleBar;
import com.taiqiwen.im_api.ChatServiceUtil;
import com.taiqiwen.profile_api.ProfileServiceUtil;
import com.taiqiwen.testlibrary.TestUtil;
import com.test.account_api.AccountServiceUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

public class MainActivity extends AppCompatActivity {

    public static final int TAB_CHAT = 0x00;
    public static final int TAB_APP = 0x01;
    public static final int TAB_PIC = 0x02;
    public static final int TAB_USER = 0x03;
    private static final String TAG = MainActivity.class.getSimpleName();
    ViewPager mViewPager;
    TabGroup mTabGroup;
    TabView mChatTabView;
    CommonTitleBar titleBar;
    ShareViewModel shareViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        initView();
        EventBusWrapper.register(this);
        shareViewModel = new ViewModelProvider(this).get(ShareViewModel.class);
        shareViewModel.getMsgUnreadTotal().observe(this, new Observer<Integer>() {
            @Override public void onChanged(Integer integer) {
                if (integer > 0) {
                    ((TabView) mTabGroup.getChildAt(1)).setBadgeCount(integer)
                            .setmDefaultTopPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                    2,
                                    getResources().getDisplayMetrics()))
                            .setBadgeShown(true);
                } else {
                    ((TabView) mTabGroup.getChildAt(1)).setBadgeShown(false);
                }
            }
        });
        AccountServiceUtil.getSerVice().setCurUser(LocalStorageHelper.loadUserInfo(this));
    }

    private void initView() {
        mViewPager = findViewById(R.id.vp_main);
        mTabGroup = findViewById(R.id.tg_tab);
        mChatTabView = findViewById(R.id.tab_chat);
        titleBar = findViewById(R.id.titlebar);
        titleBar.setBackgroundResource(R.drawable.shape_gradient);
        titleBar.setCenterText("礼物展示");
        initViewPager();
        mTabGroup.setOnCheckedChangeListener(new TabGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(TabGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.tab_chat:
                        setCurrentFragment(TAB_CHAT);
                        break;
                    case R.id.tb_pic:
                        setCurrentFragment(TAB_PIC);
                        break;
                    case R.id.tb_app:
                        TestUtil.INSTANCE.getDebug();
                        setCurrentFragment(TAB_APP);
                        break;
                    case R.id.tb_user:
                        setCurrentFragment(TAB_USER);
                        break;
                }
            }
        });


        //init tab badge view && ripple view,the others setted in activity_main.xml
        mChatTabView
                .setBadgeColor(getResources().getColor(android.R.color.holo_blue_dark))
                .setmDefaultTopPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()))
                .setBadgeShown(true)
                .setTabRippleCentered(false)
                .setTabRippleColor(android.R.color.holo_blue_dark)
                .setTabRippleDuration(100)
                //override setOnRippleCompleteListener method in rippleView
                .setOnRippleCompleteListener(new RippleView.OnRippleCompleteListener() {
                    @Override
                    public void onComplete(RippleView rippleView) {
                        mChatTabView.setChecked(true);
                    }
                });
/*        ((TabView) mTabGroup.getChildAt(1)).setBadgeCount(999)
                .setmDefaultTopPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()))
                .setBadgeShown(true)
                .setTabRippleEnable(false);*/

    }


    private void initViewPager() {

        //缓存3页避免切换时出现空指针
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(new ContentFragmentAdapter.Holder(getSupportFragmentManager())
                //.add(MainTabFragment.newInstance(android.R.color.holo_blue_dark))
                .add(GiftServiceUtil.getSerVice().getGiftGalleryFragment())
                //.add(MainTabFragment.newInstance(android.R.color.holo_red_dark))
                .add(ChatServiceUtil.getSerVice().getSessionFragment())
                //.add(MainTabFragment.newInstance(android.R.color.holo_green_dark))
                .add(ProfileServiceUtil.getSerVice().getGiftSessionFragment())
                //.add(MainTabFragment.newInstance(android.R.color.holo_orange_dark))
                .add(ProfileServiceUtil.getSerVice().getProfileFragment())
                .set());
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d("tttest", "onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                ((TabView) mTabGroup.getChildAt(position)).setChecked(true);
                switch (position) {
                    case TAB_CHAT: titleBar.setCenterText("礼物展示"); break;
                    case TAB_APP: titleBar.setCenterText("社区"); break;
                    case TAB_PIC: titleBar.setCenterText("收礼"); break;
                    case TAB_USER: {
                        titleBar.setCenterText("我");
                        if(!AccountServiceUtil.getSerVice().isLogged()) {
                            ProfileServiceUtil.getSerVice().startLoginActivity(MainActivity.this);
                            //GiftActivity.start(MainActivity.this, new GiftParams("vf"));
                        }
                    } break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setCurrentFragment(final int position) {
        Log.i(TAG, "position:" + position);
        //不使用切换动画 避免与自定义动画冲突
        mViewPager.setCurrentItem(position, false);
    }

    @Override protected void onStop() {
        GiftUser user = AccountServiceUtil.getSerVice().getCurUser();
        if (user != null) {
            LocalStorageHelper.saveUserInfo(this, user);
        }
        AccountServiceUtil.getSerVice().updateLastOnLineTime();
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void logCheckoutEvent(GiftChangedEvent event) {
        int newNumber = event.getUnchecked();
        if (newNumber > 0) {
            ((TabView) mTabGroup.getChildAt(2)).setBadgeCount(event.getUnchecked())
                    .setmDefaultTopPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                            2,
                            getResources().getDisplayMetrics()))
                    .setBadgeShown(true);
        } else {
            ((TabView) mTabGroup.getChildAt(2)).setBadgeShown(false);
        }
    }

    @Override protected void onDestroy() {
        EventBusWrapper.unregister(this);
        super.onDestroy();
    }
}