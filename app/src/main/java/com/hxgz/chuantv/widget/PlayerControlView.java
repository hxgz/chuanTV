package com.hxgz.chuantv.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.Player;
import com.hxgz.chuantv.R;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class PlayerControlView extends FrameLayout {
    private PlayerControlBar playerControls;

    private TextView infoTextView;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_MENU:
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
        inflate(getContext(), R.layout.playback_control_view, this);
        setFocusable(true);
        setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

        infoTextView = findViewById(R.id.ctl_info_text);
        playerControls = findViewById(R.id.ctl_playercontrols);
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
}
