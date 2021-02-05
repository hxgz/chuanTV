package com.hxgz.chuantv.playback;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.exoplayer2.*;
import com.google.android.exoplayer2.source.BehindLiveWindowException;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;

/**
 * @author zhoujianwu
 * @date 2020/11/17
 * @description：
 */
public class PlaybackService extends Service {

    private UniversalMediaSourceFactory mediaFactory;

    private SimpleExoPlayer player;

    private PlaybackServiceListener eventListener;
    private MediaSource mediaSource;

    public class PlaybackServiceBinder extends Binder {
        public PlaybackService getServiceInstance() {
            return PlaybackService.this;
        }
    }

    public void loadMedia(@NonNull Uri mediaUri, boolean playWhenReady, long startPosition) {
        mediaSource = mediaFactory.createMediaSource(mediaUri);
        if (!isPlayerValid()) return;

        player.prepare(mediaSource, true, true);

        seekTo(startPosition);

        player.setPlayWhenReady(playWhenReady);
        if (isEventListenerValid())
            eventListener.onPlayerInitialized();
    }

    public void playMedia() {
        player.prepare(mediaSource, true, true);
        player.setPlayWhenReady(true);
    }

    public void seekTo(long pos) {
        if (!isPlayerValid()) return;
        player.seekTo(pos);
    }

    public SimpleExoPlayer getPlayer() {
        return player;
    }

    public void setListener(PlaybackServiceListener listener) {
        eventListener = listener;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        initializePlayer();
        return new PlaybackServiceBinder();
    }

    @Override
    public void onDestroy() {
        releasePlayerAndMedia();
        super.onDestroy();
    }

    private void initializePlayer() {
        mediaFactory = new UniversalMediaSourceFactory(this, "TV");

        DefaultTrackSelector trackSelector = new DefaultTrackSelector(new AdaptiveTrackSelection.Factory());
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(this).build();
        DefaultRenderersFactory renderersFactory = new DefaultRenderersFactory(this);
        DefaultLoadControl loadControl = new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, 16))
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true)
                .setBufferDurationsMs(60000, 60000, 1500, 1500)
                .createDefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(this, renderersFactory, trackSelector, loadControl, null, bandwidthMeter);

        player.addListener(new PlayerEventListener());
    }

    private void releasePlayerAndMedia() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    public boolean isPlayerValid() {
        return player != null;
    }

    private boolean isEventListenerValid() {
        return eventListener != null;
    }

    public long getCurrentPosition() {
        if (isPlayerValid()) {
            return player.getCurrentPosition();
        }
        return 0;
    }

    private class PlayerEventListener implements Player.EventListener {
        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            // TODO: 不同状态，不同的keepScreenOn
            switch (playbackState) {
                case Player.STATE_ENDED:
                    if (isEventListenerValid())
                        eventListener.onPlayerEnd();
                    break;
                case Player.STATE_READY:
                    if (isEventListenerValid())
                        if (playWhenReady) {
                            eventListener.onPlayerPlay();
                        } else {
                            eventListener.onPlayerPause();
                        }
                    break;
                case Player.STATE_BUFFERING:
                    if (isEventListenerValid())
                        eventListener.onPlayerBuffering();
                    break;
                case Player.STATE_IDLE:
                default:
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            //HttpDataSource$InvalidResponseCodeException Response code: 404
            if (error instanceof ExoPlaybackException
                    && error.getCause() instanceof BehindLiveWindowException) {
                playMedia();
            }
            eventListener.onPlayerError(error);
        }
    }
}

    