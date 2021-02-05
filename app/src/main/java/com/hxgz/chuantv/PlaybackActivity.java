package com.hxgz.chuantv;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.hxgz.chuantv.dataobject.LiveTvDO;
import com.hxgz.chuantv.extractors.LiveTV;
import com.hxgz.chuantv.playback.PlaybackService;
import com.hxgz.chuantv.playback.PlaybackServiceListener;
import com.hxgz.chuantv.utils.DebugUtil;
import com.hxgz.chuantv.widget.PlayerControlView;

import java.util.List;

public class PlaybackActivity extends BackPressActivity {
    PlayerView playerView;
    PlayerControlView playerControlView;
    SpinKitView loadingProcess;

    private PlaybackService playbackService;
    private PlaybackServiceConnection playbackServiceConnection;

    List<LiveTvDO> channelList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playback_activity);

        playerView = findViewById(R.id.liveVideoView);
        playerView.setUseController(false);

        DebugUtil.whichViewFocusing(this);

        playerControlView = findViewById(R.id.pb_playerControlView);
        loadingProcess = findViewById(R.id.loading_progress);

        channelList = LiveTV.getChannelList();
        playerControlView.setTvList(channelList);
    }

    private class PlaybackServiceConnection implements ServiceConnection {
        boolean isConnected = false;

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            if (binder instanceof PlaybackService.PlaybackServiceBinder) {
                isConnected = true;

                PlaybackService.PlaybackServiceBinder serviceBinder = (PlaybackService.PlaybackServiceBinder) binder;
                playbackService = serviceBinder.getServiceInstance();
                playerControlView.setPlaybackService(playbackService);

                playbackService.setListener(new PlaybackCallbackListener());

                //load the media
                playerControlView.playTv(0);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isConnected = false;
        }
    }

    private class PlaybackCallbackListener implements PlaybackServiceListener {
        @Override
        public void onPlayerInitialized() {
            playerView.setPlayer(playbackService.getPlayer());
            // 去掉默认的Controller

            if (playerControlView.getPlayer() != playbackService.getPlayer()) {
                playerControlView.setPlayer(playbackService.getPlayer());
            }
            loadingProcess.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPlayerPlay() {
            loadingProcess.setVisibility(View.GONE);
        }

        @Override
        public void onPlayerBuffering() {
            loadingProcess.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPlayerPause() {

        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        playbackServiceConnection = new PlaybackServiceConnection();
        bindService(new Intent(this, PlaybackService.class), playbackServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        unbindService(playbackServiceConnection);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, PlaybackService.class));
    }

}