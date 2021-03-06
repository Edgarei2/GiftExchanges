package com.taiqiwen.im.game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import cn.bmob.v3.BmobRealTimeData;
import cn.bmob.v3.listener.ValueEventListener;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.taiqiwen.base_framework.ToastHelper;
import com.taiqiwen.base_framework.model.GiftUser;
import com.taiqiwen.base_framework.ui.countdown.CountDownTextView;
import com.taiqiwen.base_framework.ui.countdown.CustomerViewUtils;
import com.taiqiwen.im.R;
import com.taiqiwen.im.game.game2048.app.Config;
import com.taiqiwen.im.game.game2048.app.ConfigManager;
import com.taiqiwen.im.game.game2048.app.Constant;
import com.taiqiwen.im.game.game2048.db.CellEntity;
import com.taiqiwen.im.game.game2048.db.GameDatabaseHelper;
import com.taiqiwen.im.game.game2048.view.CommonDialog;
import com.taiqiwen.im.game.game2048.view.ConfigDialog;
import com.taiqiwen.im.game.game2048.view.GameOverDialog;
import com.taiqiwen.im.game.game2048.view.GameView;
import com.taiqiwen.im.network.ImApi;
import com.taiqiwen.im_api.model.GameScoreStatus;
import com.taiqiwen.im_api.model.GamerDTO;
import com.taiqiwen.profile_api.ProfileServiceUtil;
import com.test.account_api.AccountServiceUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Game2048Activity extends AppCompatActivity {

    //private List<GiftUser> gamerList;
    private String roomId;


    public static final String KEY_CHEAT = "??????";
    public static final int KEY_MATCH_SCORE = 3;
    public static final String label = "????????? ";
    public static final Long GAME_LENGTH = 180L;

    private TextView currentScores;
    private TextView bestScores;
    private TextView bestScoresRank;
    private TextView titleDescribe;
    private MaterialButton reset;
    private MaterialButton menu;
    private ImageView cheatStar;
    private GameView gameView;

    private BroadcastReceiver myReceiver;
    private ConfigDialog configDialog;
    private GestureOverlayView mGestureOverlayView;

    private GameDatabaseHelper gameDatabaseHelper;
    private SQLiteDatabase db;

    private boolean isNeedSave = true;

    private CountDownTextView mCountDownTextView;
    private TextView friendComeHint;
    private TextView friendScore;
    private Boolean longLinkEstablished = false;
    private BmobRealTimeData bmobRealTimeData = new BmobRealTimeData();
    private GameScoreStatus mGameScoreStatus = new GameScoreStatus("-1", "-1");
    private Boolean isGameStarted = false;
    private Boolean beginNow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2048);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        //gamerList = ((GamerDTO)intent.getSerializableExtra(KEY_GAMER_DATA)).getGamers();
        roomId = intent.getStringExtra(KEY_ROOM_ID);
        if (intent.getStringExtra(KEY_BEGIN_NOW).equals("1")) {
            beginNow = true;
        }

        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        initView();
        initData();
        initGesture();

        // ???????????????
        if (!longLinkEstablished) {
            longLinkEstablished = true;
            establishLongLink();
        }
        ImApi.INSTANCE.updateCurUserScore(roomId, AccountServiceUtil.getSerVice().getCurUserId(), "0");

        enterInfiniteMode();

        initResetGame();

        if (beginNow && !isGameStarted) {
            mCountDownTextView.startCountDown(GAME_LENGTH);
            isGameStarted = true;
            friendComeHint.setText("?????????");
        }
    }


    private Timer timer;

    @Override
    protected void onResume() {
        super.onResume();

        if (null == timer) {
            timer = new Timer();
            startTiming();
        }
    }

    private void startTiming() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isNeedSave) {
                    // 5S????????????????????????
                    saveGameProgress();
                }
            }
        }, 5 * 1_000L, 5 * 1_000L);
    }

    @Override
    protected void onDestroy() {
        // ??????????????????
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
            myReceiver = null;
        }
        // ???????????????
        mGestureOverlayView.removeAllOnGestureListeners();

        if (null != timer) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
        if (null != gameDatabaseHelper) {
            gameDatabaseHelper.close();
            gameDatabaseHelper = null;
        }

        //saveGameProgress();

        if (bmobRealTimeData.isConnected()) {
            bmobRealTimeData.unsubRowUpdate("Group", roomId);
        }

        super.onDestroy();
    }

    /**
     * ???????????????
     */
    private void initView() {
        // ?????????????????????????????????
        if (isLightColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))) {
            // ???????????????????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }

        titleDescribe = findViewById(R.id.tv_title_describe);
        currentScores = findViewById(R.id.tv_current_score);
        bestScores = findViewById(R.id.tv_best_score);
        bestScoresRank = findViewById(R.id.tv_best_score_rank);
        reset = findViewById(R.id.btn_reset);
        menu = findViewById(R.id.btn_option);
        mGestureOverlayView = findViewById(R.id.gesture_overlay_view);
        cheatStar = findViewById(R.id.iv_show_cheat);
        gameView = findViewById(R.id.game_view);
        friendComeHint = findViewById(R.id.friend_come_hint);
        friendScore = findViewById(R.id.tv_current_score_friend);
        mCountDownTextView = findViewById(R.id.game_time_count_down);
        mCountDownTextView.setNormalText("???????????????3??????")
                .setBeforeIndex(label.length())
                .setCountDownClickable(false)
                .setIsShowComplete(true)
                .setShowFormatTime(true)
                .setOnCountDownTickListener(new CountDownTextView.OnCountDownTickListener() {
                    @Override
                    public void onTick(long untilFinished, String showTime, CountDownTextView tv) {
                        tv.setText(CustomerViewUtils.getMixedText(label + showTime, tv.getTimeIndexes(), true));
                    }
                })
                .setOnCountDownFinishListener(new CountDownTextView.OnCountDownFinishListener() {
                    @Override
                    public void onFinish() {
                        mCountDownTextView.setText("????????????");
                        isGameStarted = false;
                        ImApi.INSTANCE.endGame(roomId, new Function1<String, Unit>() {
                            @Override public Unit invoke(String s) {
                                if (s.equals(AccountServiceUtil.getSerVice().getCurUserId())) {
                                    ToastHelper.showToast("????????????????????????20????????????");
                                } else {
                                    ToastHelper.showToast("??????????????????");
                                }
                                return null;
                            }
                        });
                    }
                });
        //mCountDownTextView.startCountDown(GAME_LENGTH);

        // ??????????????????
        if (Config.CurrentGameMode == Constant.MODE_CLASSIC) {
            // ????????????????????????
            bestScores.setText(String.valueOf(Config.BestScore));
            bestScoresRank.setText(getString(R.string.best_score_rank, Config.GRIDColumnCount));
            currentScores.setText(String.valueOf(ConfigManager.getCurrentScore(this)));
            gameView.initView(Constant.MODE_CLASSIC);
        } else {
            // ??????????????????
            enterInfiniteMode();
        }
        setTextStyle(titleDescribe);
    }

    /**
     * ???????????????
     */
    private void initData() {
        // ????????????
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(GameView.ACTION_RECORD_SCORE);
        filter.addAction(GameView.ACTION_WIN);
        filter.addAction(GameView.ACTION_LOSE);
        registerReceiver(myReceiver, filter);

        // ?????????????????????????????????
        reset.setOnClickListener(v -> showConfirmDialog());
        // ????????????
        menu.setOnClickListener(v -> showConfigDialog());
        // ????????????
        titleDescribe.setOnClickListener(v -> showChangeModeDialog());

        gameDatabaseHelper = new GameDatabaseHelper(this, Constant.DB_NAME, null, 1);
        db = gameDatabaseHelper.getWritableDatabase();

    }

    /**
     * ???????????????
     */
    private void initGesture() {
        if (Config.haveCheat) {
            return;
        }

        // ???????????????
        GestureLibrary library = GestureLibraries.fromRawResource(this, R.raw.gestures);
        // ????????????????????????????????????????????????
        mGestureOverlayView.addOnGesturePerformedListener((overlay, gesture) -> {
            // ?????????????????????
            if (library.load()) {
                // ??????????????????????????????????????????
                ArrayList<Prediction> predictions = library.recognize(gesture);
                if (!predictions.isEmpty()) {
                    // ??????????????????
                    Prediction prediction = predictions.get(0);
                    // ??????????????????3
                    if (prediction.score > KEY_MATCH_SCORE) {
                        // ??????????????????
                        if (KEY_CHEAT.equals(prediction.name)) {
                            cheatStar.setVisibility(View.VISIBLE);
                            // ??????????????????????????????????????????????????????10%????????????????????????
                            ScaleAnimation animation = new ScaleAnimation(
                                    0.1f, 1, 0.1f, 1,
                                    Animation.RELATIVE_TO_SELF, 0.5f,
                                    Animation.RELATIVE_TO_SELF, 0.5f);
                            animation.setDuration(999);
                            cheatStar.startAnimation(animation);
                            animation.setAnimationListener(new Animation.AnimationListener() {
                                @Override
                                public void onAnimationStart(Animation animation) {
                                }

                                @Override
                                public void onAnimationEnd(Animation animation) {
                                    showCheatModeDialog();
                                }

                                @Override
                                public void onAnimationRepeat(Animation animation) {
                                }
                            });
                        }
                    }
                }
            }
        });
    }

    /**
     * ???????????????????????????
     */
    private void showCheatModeDialog() {
        CommonDialog dialog = new CommonDialog(this, R.style.CustomDialog);
        dialog.setCancelable(false);
        dialog.setTitle("????????????")
                .setMessage("????????????????????????????????????~???????????????????????????1024??????????????????????????????")
                .setOnNegativeClickListener("", v -> dialog.cancel())
                .setOnPositiveClickedListener("", v -> {
                    Config.haveCheat = true;
                    gameView.addDigital(true);
                    Toast.makeText(Game2048Activity.this, "???????????????????????????...", Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                }).show();
        cheatStar.setVisibility(View.INVISIBLE);
    }

    /**
     * ???????????????????????????
     */
    private void showChangeModeDialog() {
        String subject = "";
        if (Config.CurrentGameMode == Constant.MODE_CLASSIC) {
            subject = "????????????";
        } else if (Config.CurrentGameMode == Constant.MODE_INFINITE) {
            subject = "????????????";
        }
        CommonDialog dialog = new CommonDialog(this, R.style.CustomDialog);
        dialog.setCancelable(true);
        dialog.setTitle(getResources().getString(R.string.tip))
                .setMessage("??????????????????" + subject)
                .setOnPositiveClickedListener("", v -> {
                    if (Config.CurrentGameMode == Constant.MODE_CLASSIC) {
                        Toast.makeText(Game2048Activity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                        enterInfiniteMode();
                    } else {
                        Toast.makeText(Game2048Activity.this, "?????????????????????", Toast.LENGTH_SHORT).show();
                        enterClassicsMode();
                    }
                    dialog.cancel();
                })
                .setOnNegativeClickListener("", v -> dialog.cancel())
                .show();
    }

    /**
     * ??????????????????
     */
    private void enterInfiniteMode() {
        Config.haveCheat = false;
        Config.CurrentGameMode = Constant.MODE_INFINITE;
        // ??????????????????
        ConfigManager.putCurrentGameMode(this, Constant.MODE_INFINITE);
        titleDescribe.setText(getResources().getString(R.string.game_mode_infinite));
        bestScores.setText(String.valueOf(ConfigManager.getBestScoreWithinInfinite(this)));
        bestScoresRank.setText(getResources().getText(R.string.tv_best_score_infinite));
        currentScores.setText(String.valueOf(ConfigManager.getCurrentScoreWithinInfinite(this)));
        setTextStyle(titleDescribe);
        gameView.initView(Constant.MODE_INFINITE);
    }

    /**
     * ??????????????????
     */
    private void enterClassicsMode() {
        Config.haveCheat = false;
        Config.CurrentGameMode = Constant.MODE_CLASSIC;
        // ??????????????????
        ConfigManager.putCurrentGameMode(this, Constant.MODE_CLASSIC);
        titleDescribe.setText(getResources().getString(R.string.game_mode_classics));
        // ????????????????????????
        bestScores.setText(String.valueOf(Config.BestScore));
        bestScoresRank.setText(getString(R.string.best_score_rank, Config.GRIDColumnCount));
        currentScores.setText(String.valueOf(ConfigManager.getCurrentScore(this)));
        setTextStyle(titleDescribe);
        gameView.initView(Constant.MODE_CLASSIC);

    }

    /**
     * ????????????????????????????????????
     */
    private void setTextStyle(TextView textView) {
        SpannableString spannableString = new SpannableString(textView.getText().toString());
        StyleSpan styleSpan = new StyleSpan(Typeface.BOLD);
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#FFFFFF"));
        AbsoluteSizeSpan absoluteSizeSpan = new AbsoluteSizeSpan(36);
        spannableString.setSpan(styleSpan, 0, 4, Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(foregroundColorSpan, 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(absoluteSizeSpan, 0, 4, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        textView.setText(spannableString);
    }

    /**
     * ???????????????????????????
     */
    private void showConfirmDialog() {
        CommonDialog dialog = new CommonDialog(this, R.style.CustomDialog);
        dialog.setCancelable(false);
        dialog.setTitle(getResources().getString(R.string.tip))
                .setMessage(getResources().getString(R.string.tip_reset_btn))
                .setOnNegativeClickListener("", v -> dialog.cancel())
                .setOnPositiveClickedListener("", v -> {
                    Config.haveCheat = false;
                    gameView.resetGame();
                    // ????????????
                    currentScores.setText("0");
                    saveCurrentScore(0);
                    deleteCache(Config.getTableName());
                    dialog.cancel();
                }).show();
    }

    private void initResetGame() {
        Config.haveCheat = false;
        gameView.resetGame();
        // ????????????
        currentScores.setText("0");
        saveCurrentScore(0);
        deleteCache(Config.getTableName());
    }

    /**
     * ?????????????????????
     */
    private void showConfigDialog() {
        configDialog = new ConfigDialog(this, R.style.CustomDialog);
        configDialog.setOnPositiveClickListener(v -> onDialogConfirm()).show();
    }

    /**
     * ????????????????????????????????????
     */
    private void onDialogConfirm() {
        // ????????????????????????????????????
        int difficulty = configDialog.getSelectDifficulty();
        boolean volumeState = configDialog.getVolumeState();
        // ???????????????????????????????????????
        if (difficulty == Config.GRIDColumnCount) {
            // ??????????????????????????????
            if (volumeState != Config.VolumeState) {
                // ??????????????????
                ConfigManager.putGameVolume(this, volumeState);
                Config.VolumeState = volumeState;
            }
            configDialog.cancel();
            return;
        }

        changeConfiguration(configDialog, difficulty, volumeState);
        Config.haveCheat = false;
    }

    /**
     * ??????????????????
     */
    private void changeConfiguration(ConfigDialog dialog, int difficulty, boolean volumeState) {
        Config.GRIDColumnCount = difficulty;
        Config.VolumeState = volumeState;
        gameView.initView(Constant.MODE_CLASSIC);
        // ????????????
        currentScores.setText(String.valueOf(ConfigManager.getCurrentScore(this)));
        bestScoresRank.setText(getString(R.string.best_score_rank, difficulty));
        bestScores.setText(String.valueOf(ConfigManager.getBestScore(this)));
        // ??????????????????
        ConfigManager.putGameDifficulty(Game2048Activity.this, difficulty);
        // ??????????????????
        ConfigManager.putGameVolume(this, volumeState);
        dialog.cancel();
    }

    /**
     * ????????????
     */
    private void recordScore(int score) {
        if (!isGameStarted) {
            return;
        }
        currentScores.setText(String.valueOf(score));
        ImApi.INSTANCE.updateCurUserScore(roomId, AccountServiceUtil.getSerVice().getCurUserId(), String.valueOf(score));
        // ???????????????????????????
        if (Config.CurrentGameMode == Constant.MODE_CLASSIC) {
            if (score > ConfigManager.getBestScore(this)) {
                updateBestScore(score);
            }
        } else if (Config.CurrentGameMode == Constant.MODE_INFINITE) {
            if (score > ConfigManager.getBestScoreWithinInfinite(this)) {
                updateBestScore(score);
            }
        }

    }

    /**
     * ???????????????
     */
    private void updateBestScore(int newScore) {
        bestScores.setText(String.valueOf(newScore));
        if (Config.CurrentGameMode == Constant.MODE_CLASSIC) {
            Config.BestScore = newScore;
            ConfigManager.putBestScore(this, newScore);
        } else if (Config.CurrentGameMode == Constant.MODE_INFINITE) {
            Config.BestScoreWithinInfinite = newScore;
            ConfigManager.putBestScoreWithinInfinite(this, newScore);
        }
    }


    /**
     * ??????????????????
     */
    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if (action.equals(GameView.ACTION_RECORD_SCORE)) {
                // ????????????
                int score = intent.getIntExtra(GameView.KEY_SCORE, 0);
                // ??????????????????
                int historyScore = Integer.parseInt(currentScores.getText().toString());
                saveCurrentScore(score + historyScore);
                recordScore(score + historyScore);
                // ????????????
            } else if (action.equals(GameView.ACTION_WIN)
                    || action.equals(GameView.ACTION_LOSE)) {
                // ????????????
                isNeedSave = false;
                deleteCache(Config.getTableName());
                saveCurrentScore(0);

                String result = intent.getStringExtra(GameView.KEY_RESULT);
                GameOverDialog dialog = new GameOverDialog(Game2048Activity.this, R.style.CustomDialog);
                new Handler().postDelayed(() ->
                        dialog.setFinalScore(currentScores.getText().toString())
                                .setTitle(result)
                                .setOnShareClickListener(v -> share())
                                .setOnGoOnClickListener(v -> {
                                    // ????????????
                                    isNeedSave = true;
                                    gameView.reset();
                                    deleteCache(Config.getTableName());
                                    saveCurrentScore(0);

                                    gameView.initView(Config.CurrentGameMode);
                                    currentScores.setText("0");
                                    dialog.cancel();
                                }).show(), 666);
            }
        }
    }

    private void saveCurrentScore(int score) {
        if (Config.CurrentGameMode == Constant.MODE_CLASSIC) {
            ConfigManager.putCurrentScore(Game2048Activity.this, score);
        } else {
            ConfigManager.putCurrentScoreWithinInfinite(Game2048Activity.this, score);
        }
    }

    /**
     * ???????????????????????????
     */
    private boolean isLightColor(int color) {
        return ColorUtils.calculateLuminance(color) >= 0.5;
    }

    /**
     * ??????
     */
    private void share() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                getResources().getString(R.string.share, currentScores.getText().toString()));
        shareIntent = Intent.createChooser(shareIntent, "?????????");
        startActivity(shareIntent);
    }

    private void saveGameProgress() {
        String tableName = Config.getTableName();
        deleteCache(tableName);
        // ??????????????????
        ArrayList<CellEntity> data = gameView.getCurrentProcess();
        if (data.size() > 2) {
            ContentValues values = new ContentValues();
            for (CellEntity cell : data) {
                values.put("x", cell.getX());
                values.put("y", cell.getY());
                values.put("num", cell.getNum());
                db.insert(tableName, null, values);
                values.clear();
            }
        }
    }

    /**
     * ????????????
     */
    private void deleteCache(String tableName) {
        db.execSQL("delete from " + tableName);
    }

    private void establishLongLink() {
        bmobRealTimeData.start(new ValueEventListener() {
            @Override public void onConnectCompleted(Exception e) {
                if (e == null) {
                    if (bmobRealTimeData.isConnected()) {
                        bmobRealTimeData.subRowUpdate("Group", roomId);
                        ImApi.INSTANCE.updateCurUserScore(roomId, AccountServiceUtil.getSerVice().getCurUserId(), "0");
                    }
                }
            }

            @Override public void onDataChange(JSONObject jsonObject) {
                Gson gson = new Gson();
                String action = jsonObject.optString("action");
                String jsonString = gson.toJson(jsonObject);
                if (action.equals(BmobRealTimeData.ACTION_UPDATEROW)) {
                    JSONObject data = jsonObject.optJSONObject("data");
                    String message = data.optString("last_message");
                    try {
                        Map<String,String> map = new Gson().fromJson(message, new TypeToken<HashMap<String,String>>(){}.getType());
                        for(Map.Entry<String,String> entry : map.entrySet()) {
                            if (entry.getKey().equals(AccountServiceUtil.getSerVice().getCurUserId())) {
                                mGameScoreStatus.setMyScore(entry.getValue());
                            } else {
                                mGameScoreStatus.setFriendScore(entry.getValue());
                            }
                        }
                        if (mGameScoreStatus.isGameValid()) {
                            if (!isGameStarted) {
                                mCountDownTextView.startCountDown(GAME_LENGTH);
                                initResetGame();
                                isGameStarted = true;
                                friendComeHint.setText("?????????");
                            } else {
                                //currentScores.setText(mGameScoreStatus.getMyScore());
                                friendScore.setText(mGameScoreStatus.getFriendScore());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //saveGameProgress();
        super.onBackPressed();
    }

    private static final String KEY_GAMER_DATA = "key_friend_data";
    private static final String KEY_ROOM_ID = "key_room_id";
    private static final String KEY_BEGIN_NOW = "key_begin_now";

    public static void start(Context context, String gameRoomId, String beginNow) {
        if (context == null || gameRoomId == null) return;
        else if (!AccountServiceUtil.getSerVice().isLogged()) {
            ProfileServiceUtil.getSerVice().startLoginActivity(context);
        } else {
            Intent intent = new Intent(context, Game2048Activity.class);
            intent.putExtra(KEY_ROOM_ID, gameRoomId);
            intent.putExtra(KEY_BEGIN_NOW, beginNow);
            context.startActivity(intent);
        }
    }

}