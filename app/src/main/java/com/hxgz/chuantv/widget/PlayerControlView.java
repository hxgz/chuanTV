package com.hxgz.chuantv.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Player;
import com.hxgz.chuantv.R;
import com.hxgz.chuantv.dataobject.LiveTvDO;
import com.hxgz.chuantv.playback.PlaybackService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PlayerControlView extends FrameLayout {
    LayoutInflater inflater;

    private TextView infoTextView;

    private PlayerTvListView playerTvListView;

    private PlayerControlBar playerControls;

    private PlaybackService playbackService;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_MENU:
                if (playerTvListView.isLiveMode()) {
                    playerTvListView.showTVList();
                    return true;
                }
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                playerControls.show(true);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                if (playerTvListView.isLiveMode()) {
                    playerTvListView.showTVList();
                } else {
                    playerControls.playOrPause();
                }
                return true;
            default:
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public PlayerControlView(Context context) {
        this(context, null);
    }

    public PlayerControlView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PlayerControlView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        inflater = LayoutInflater.from(context);

        inflater.inflate(R.layout.playback_control_view, this);
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        infoTextView = findViewById(R.id.ctl_info_text);
        playerControls = findViewById(R.id.ctl_playercontrols);
        playerTvListView = findViewById(R.id.showTvListView);
    }

    public PlayerControlView setPlayer(Player player) {
        playerControls.setPlayer(player);
        return this;
    }

    public Player getPlayer() {
        return playerControls.getPlayer();
    }

    public PlayerControlBar getPlayerControls() {
        return playerControls;
    }

    public void showInfoText(String text, Object... format) {
        if (infoTextView == null) return;

        infoTextView.setText(String.format(text, format));
        infoTextView.setAnimation(null);
        infoTextView.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            infoTextView.animate().alpha(0f).setDuration(500).withEndAction(new Runnable() {
                @Override
                public void run() {
                    hideInfoText();
                }
            });
        }, 750);
    }

    public void hideInfoText() {
        infoTextView.setVisibility(View.INVISIBLE);
        infoTextView.setAnimation(null);
        infoTextView.setAlpha(1f);
    }

    public void setTitle(String text) {
        playerControls.setTitle(text);
    }

    public void setTvList(List<LiveTvDO> tvDOList) {
        playerTvListView.setTvList(tvDOList);
    }

    public void playTv(int position) {
        playerTvListView.playTv(position);
    }

    private Map<String, Map<String, List<LiveTvDO>>> sorted(List<LiveTvDO> tvDOList) {
        Map<String, Map<String, List<LiveTvDO>>> byCate = new LinkedHashMap<>();
        tvDOList.forEach(tvDO -> {
            Map<String, List<LiveTvDO>> byPlatform = byCate.getOrDefault(tvDO.getCategory(), new LinkedHashMap());
            byCate.put(tvDO.getCategory(), byPlatform);

            List<LiveTvDO> liveTvDOList = byPlatform.getOrDefault(tvDO.getChannel(), new ArrayList<>());
            byPlatform.put(tvDO.getChannel(), liveTvDOList);

            liveTvDOList.add(tvDO);
        });

        return byCate;
    }
    // ======= 频道列表 END ============

    public void setPlaybackService(PlaybackService playbackService) {
        playerTvListView.setPlaybackService(playbackService);
        this.playbackService = playbackService;
    }

    private void scrollToCenter(ScrollView scrollView, View childView) {
        int scrollViewHeight = scrollView.getHeight();
        int height = (childView.getTop() + childView.getBottom()) / 2;

        int toY = height - scrollViewHeight / 2;
        scrollView.scrollTo(0, toY);
    }
}
