package com.hxgz.chuantv;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ui.PlayerView;
import com.hxgz.chuantv.consts.TvConst;
import com.hxgz.chuantv.dataobject.PlatformVideoFileDO;
import com.hxgz.chuantv.dataobject.VideoDetailDO;
import com.hxgz.chuantv.dataobject.VideoInfoDO;
import com.hxgz.chuantv.dataobject.VideoMoreInfoDO;
import com.hxgz.chuantv.extractors.TVExtractor;
import com.hxgz.chuantv.playback.PlaybackService;
import com.hxgz.chuantv.playback.PlaybackServiceListener;
import com.hxgz.chuantv.utils.DebugUtil;
import com.hxgz.chuantv.utils.IntentUtil;
import com.hxgz.chuantv.utils.LogUtil;
import com.hxgz.chuantv.utils.NoticeUtil;
import com.hxgz.chuantv.widget.PlayerControlView;
import com.hxgz.chuantv.widget.ScrollViewList;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class VideoDetailActivity extends BackPressActivity {
    TVExtractor tvExtractor;

    VideoInfoDO videoInfoDO;

    Button btn;

    VideoDetailDO videoDetailDO;
    List<ScrollViewList> fileViewLists = new ArrayList<>();

    boolean hasPlay = false;
    long playbackStartTime = 0;

    PlayerView playerView;
    PlayerControlView playerControlView;
    SpinKitView loadingProcess;

    private Dialog mFullScreenDialog;
    private boolean mExoPlayerFullscreen = false;

    private PlaybackService playbackService;
    private VideoDetailActivity.PlaybackServiceConnection playbackServiceConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvExtractor = App.getTVForSearch();

        // TODO: 待删除
        videoInfoDO = (VideoInfoDO) IntentUtil.getData(getIntent(), "videoInfoDO");
//        videoInfoDO = new VideoInfoDO();
//        videoInfoDO.setTitle("我哦哦");
//        videoInfoDO.setImgUrl("DDDD");
//        videoInfoDO.setVideoId("DD");
        // END

        setContentView(R.layout.activity_video_detail);

        playerView = findViewById(R.id.VideoViewfull3);
        playerView.setUseController(false);

        playerControlView = findViewById(R.id.pb_playerControlView);
        loadingProcess = findViewById(R.id.loading_progress);
        initFullscreenDialog();

        btn = (Button) findViewById(R.id.btnfullScreen);
        btn.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start();
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
            }
        });
        setBeforeCloseFocusView(btn);

        DebugUtil.whichViewFocusing(this);

        playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFullscreenDialog();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFullscreenDialog();
            }
        });

        loadData();
    }

    // 视频全屏
    FrameLayout playerViewLayout;

    private void initFullscreenDialog() {
        playerViewLayout = findViewById(R.id.PlayerViewLayout);

        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            private long mBackPressed;

            @Override
            public void onBackPressed() {
                if (mExoPlayerFullscreen && mBackPressed <= System.currentTimeMillis()) {
                    mBackPressed = TvConst.TIME_INTERVAL + System.currentTimeMillis();
                    NoticeUtil.show(getBaseContext(), "按【返回键】退出");
                    return;
                }

                closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {
        mLastWidth = playerViewLayout.getWidth();
        mLastHeight = playerViewLayout.getHeight();

        mFullScreenDialog.show();

        ((ViewGroup) playerViewLayout.getParent()).removeView(playerViewLayout);
        mFullScreenDialog.setContentView(playerViewLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        playerView.setPadding(0, 0, 0, 0);
        playerView.setFocusable(false);

        mExoPlayerFullscreen = true;
        playerControlView.setVisibility(View.VISIBLE);
        playerControlView.requestFocus();
    }

    private int mLastWidth;
    private int mLastHeight;

    private void closeFullscreenDialog() {
        ((ViewGroup) playerViewLayout.getParent()).removeView(playerViewLayout);
        ((LinearLayout) findViewById(R.id.PlayerContainLayout)).addView(
                playerViewLayout, 0, new ViewGroup.LayoutParams(mLastWidth, mLastHeight));

        final int dp = getResources().getDimensionPixelSize(R.dimen.border_small);
        playerView.setPadding(dp, dp, dp, dp);
        playerView.setFocusable(true);

        playerControlView.setVisibility(View.GONE);
        mFullScreenDialog.dismiss();

        mExoPlayerFullscreen = false;
    }
    // 全屏 END

    private void loadData() {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                try {
                    videoDetailDO = tvExtractor.getVideo(videoInfoDO.getVideoId());
                } catch (Exception e) {
                    NoticeUtil.show(VideoDetailActivity.this, "发生异常,请重试");
                    return;
                }
                LogUtil.e(videoDetailDO.toString());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        VideoMoreInfoDO infoDO = videoDetailDO.getInfoDO();
                        ((TextView) findViewById(R.id.videoTitle)).setText(infoDO.getTitle());
                        if (StringUtils.isEmpty(infoDO.getAliasTitle())) {
                            findViewById(R.id.videoAliasTitle).setVisibility(View.GONE);
                        } else {
                            ((TextView) findViewById(R.id.videoAliasTitle)).setText(infoDO.getAliasTitle());
                        }

                        ((TextView) findViewById(R.id.videoNotify)).setText(
                                infoDO.getYear() + "·" + infoDO.getStatus());

                        ((TextView) findViewById(R.id.videoAuthor)).setText(
                                String.join("  ", infoDO.getActorList())
                        );

                        ((TextView) findViewById(R.id.videoDesc)).setText("简介：" + infoDO.getDesc());

                        ((TextView) findViewById(R.id.VideoTag)).setText(
                                String.join("  ", infoDO.getTagList())
                        );

                        // 剧集列表
                        LinearLayout showListView = (LinearLayout) findViewById(R.id.showList);
                        for (int i = 0; i < videoDetailDO.getFileDOList().size(); i++) {
                            PlatformVideoFileDO videoFileDO = videoDetailDO.getFileDOList().get(i);
                            ScrollViewList scrollViewList = ScrollViewList.newInstance(VideoDetailActivity.this);
                            scrollViewList.setItemResId(R.layout.widget_text_view);
                            scrollViewList.setShowItemNum(10);

                            scrollViewList.setTitle(videoFileDO.getShow());
                            videoFileDO.getVideoFileDOList().forEach(videoFileDO1 -> {
                                TextView textView = (TextView) scrollViewList.newItem(videoFileDO1);
                                textView.setText(videoFileDO1.getTitle());
                            });

                            final int index = i;
                            scrollViewList.setMOnClickListen((view, data, repeatClick) -> {
                                int position = scrollViewList.getItemIndex(view);

                                if (!repeatClick) {
                                    playbackStartTime = 0;
                                    VideoDetailActivity.this.previewVideoPlay(index, position, 0);
                                }
                                VideoDetailActivity.this.openFullscreenDialog();

                                for (int j = 0; j < fileViewLists.size(); j++) {
                                    if (j != index) {
                                        fileViewLists.get(j).clearItemSelected();
                                    }
                                }
                            });
                            showListView.addView(scrollViewList);

                            fileViewLists.add(scrollViewList);
                        }

                        VideoDetailActivity.this.previewVideoPlay();
                    }
                });
            }
        }).start();

    }

    private PlatformVideoFileDO.VideoFileDO getSelectedVideoFile(int platformPosition, int videoPosition) {
        try {
            return videoDetailDO.getFileDOList()
                    .get(platformPosition)
                    .getVideoFileDOList()
                    .get(videoPosition);
        } catch (Exception e) {
            return null;
        }
    }

    private void previewVideoPlay() {
        previewVideoPlay(0, 0, playbackStartTime);
    }

    private void previewVideoPlay(int platformPosition, int videoPosition, long startTime) {
        if (CollectionUtils.isEmpty(fileViewLists)) {
            return;
        }

        ScrollViewList scrollViewList = fileViewLists.get(platformPosition);
        scrollViewList.selectItem(videoPosition);

        PlatformVideoFileDO.VideoFileDO videoFileDO = getSelectedVideoFile(platformPosition, videoPosition);
        if (null == videoFileDO) {
            return;
        }

        playerControlView.setTitle(videoDetailDO.getInfoDO().getTitle() + " - " + videoFileDO.getTitle());
        if (null != playbackService) {
            playbackService.loadMedia(Uri.parse(videoFileDO.getUrl()), true, startTime);
            hasPlay = true;
        }
    }

    private void savePlaybackPosition() {
        if (playbackService == null || !playbackService.isPlayerValid()) {
            return;
        }

        playbackStartTime = playbackService.getCurrentPosition();
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
//            VideoPlayRuntimeDO respPlayDO = (VideoPlayRuntimeDO) IntentUtil.getData(data, "response");
//            previewVideoPlay(respPlayDO.getSelectedPlatformPosition(), respPlayDO.getSelectedVideoPosition(), respPlayDO.getStartTime());
//        }
//    }

    private class PlaybackServiceConnection implements ServiceConnection {
        boolean isConnected = false;

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder binder) {
            if (binder instanceof PlaybackService.PlaybackServiceBinder) {
                isConnected = true;

                PlaybackService.PlaybackServiceBinder serviceBinder = (PlaybackService.PlaybackServiceBinder) binder;
                playbackService = serviceBinder.getServiceInstance();

                playbackService.setListener(new VideoDetailActivity.PlaybackCallbackListener());

                //load the media
                if (!hasPlay) {
                    previewVideoPlay();
                }
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

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            savePlaybackPosition();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        playbackServiceConnection = new VideoDetailActivity.PlaybackServiceConnection();
        bindService(new Intent(this, PlaybackService.class), playbackServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        hasPlay = false;
        savePlaybackPosition();
        unbindService(playbackServiceConnection);
    }

    @Override
    protected void onResume() {
        super.onResume();
        btn.requestFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(this, PlaybackService.class));
    }
}