package com.hxgz.chuantv.widget;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Player;
import com.hxgz.chuantv.R;
import com.hxgz.chuantv.dataobject.LiveTvDO;
import com.hxgz.chuantv.playback.PlaybackService;

import java.util.List;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PlayerControlView extends FrameLayout {
    LayoutInflater inflater;

    private TextView infoTextView;

    private PlayerControlBar playerControls;

    private PlaybackService playbackService;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
            case KeyEvent.KEYCODE_MENU:
                if (isLiveMode()) {
                    showTVList();
                    return true;
                }
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                playerControls.show(true);
                return true;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                playerControls.playOrPause();
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
        tvListLayout = findViewById(R.id.showTvList);
        tvScrollView = (ScrollView) tvListLayout.getParent();
        playingPosition = -1;
        isLiveTv = false;
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

    // ======= 频道列表 ============

    private ScrollView tvScrollView;
    private LinearLayout tvListLayout;
    private boolean isLiveTv;
    private int playingPosition;

    private Runnable hideTVList = new Runnable() {
        @Override
        public void run() {
            tvScrollView.setVisibility(View.INVISIBLE);
            removeCallbacks(hideTVList);
        }
    };

    private boolean isLiveMode() {
        return this.isLiveTv;
    }

    public void setTvList(List<LiveTvDO> tvDOList) {
        for (LiveTvDO liveTvDO : tvDOList) {
            TextView view = (TextView) inflater.inflate(R.layout.widget_text_exoplayer_chanel, this, false);
            view.setTag(liveTvDO);
            view.setText(liveTvDO.getChannel());
            view.setOnClickListener(v -> {
                PlayerControlView.this.playTv(tvListLayout.indexOfChild(v));
            });
            view.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    scrollToCenter(tvScrollView, v);
                    hideTVListAfterTimeout();
                }
            });
            view.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (event.getRepeatCount() == 0 && keyCode == KeyEvent.KEYCODE_BACK) {
                        hideTVList.run();
                        return true;
                    }
                }
                return false;
            });
            tvListLayout.addView(view);
        }
        isLiveTv = true;
    }

    public void showTVList() {
        tvScrollView.setVisibility(View.VISIBLE);

        View currentView = tvListLayout.getChildAt(playingPosition);
        scrollToCenter(tvScrollView, currentView);
        currentView.requestFocus();

        hideTVListAfterTimeout();
    }

    public void hideTVListAfterTimeout() {
        removeCallbacks(hideTVList);
        postDelayed(hideTVList, 5000);
    }

    public void playTv(int position) {
        if (position < 0) {
            return;
        }

        if (playingPosition >= 0) {
            View oldView = tvListLayout.getChildAt(playingPosition);
            oldView.setSelected(false);
        }

        playingPosition = position;
        View currentView = tvListLayout.getChildAt(playingPosition);
        currentView.setSelected(true);

        LiveTvDO liveTvDO = (LiveTvDO) currentView.getTag();
        Uri videoUri = Uri.parse(liveTvDO.getAddress());
        playbackService.loadMedia(videoUri, true, 0);
    }
    // ======= 频道列表 END ============

    public void setPlaybackService(PlaybackService playbackService) {
        this.playbackService = playbackService;
    }

    private void scrollToCenter(ScrollView scrollView, View childView) {
        int scrollViewHeight = scrollView.getHeight();
        int height = (childView.getTop() + childView.getBottom()) / 2;

        int toY = height - scrollViewHeight / 2;
        scrollView.scrollTo(0, toY);
    }
}
