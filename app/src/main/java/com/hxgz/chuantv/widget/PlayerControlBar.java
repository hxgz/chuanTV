package com.hxgz.chuantv.widget;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.TimeBar;
import com.hxgz.chuantv.R;

/**
 * 播放控制条
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PlayerControlBar extends PlayerControlView implements Player.EventListener {
    TextView titleTextView;
    TextView timeTextView;

    public PlayerControlBar(Context context) {
        this(context, null);
    }

    public PlayerControlBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerControlBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, attrs);
    }

    public PlayerControlBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, @Nullable AttributeSet playbackAttrs) {
        super(context, attrs, defStyleAttr, playbackAttrs);
        setFocusable(false);

        // 进度条每次快进，后退时间
        TimeBar timeBar = findViewById(R.id.exo_progress);
        timeBar.setKeyTimeIncrement(10000);
        titleTextView = findViewById(R.id.pb_streamTitle);
        timeTextView = findViewById(R.id.pb_streamTime);
    }

    @Override
    public boolean dispatchMediaKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getRepeatCount() == 0) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        return false;
                    case KeyEvent.KEYCODE_DPAD_UP:
                    case KeyEvent.KEYCODE_MENU:
                        show(true);
                        return true;
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                        playOrPause();
                        return true;
                    case KeyEvent.KEYCODE_BACK:
                        hide();
                        return true;
                }
            }
        }
        return super.dispatchMediaKeyEvent(event);
    }

    //player callback
    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (playbackState == Player.STATE_READY) {
            if (playWhenReady) {
                // 开始播放
                hide();
            } else {
                // 暂停
                show(false);
            }
        }
    }

    public void playOrPause() {
        getPlayer().setPlayWhenReady(!getPlayWhenReady());
    }

    public boolean getPlayWhenReady() {
        return getPlayer().getPlayWhenReady();
    }

    @Override
    public void setPlayer(@Nullable Player player) {
        super.setPlayer(player);
        if (player != null) {
            player.addListener(this);
        }
    }

    public void show(boolean autoHide) {
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        String formattedDate = df.format(Calendar.getInstance().getTime());
        timeTextView.setText(formattedDate);

        setShowTimeoutMs(autoHide ? 5000 : 0);
        super.show();
    }

    public void setTitle(String text) {
        titleTextView.setText(text);
    }
}
