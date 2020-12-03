package com.hxgz.chuantv;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import com.github.ybq.android.spinkit.SpinKitView;
import com.hxgz.chuantv.dataobject.VideoDetailDO;
import com.hxgz.chuantv.dataobject.VideoPlayRuntimeDO;
import com.hxgz.chuantv.utils.DebugUtil;
import com.hxgz.chuantv.utils.IntentUtil;

//https://cloud.tencent.com/developer/article/1384945
public class VideoPlayActivity extends BackPressActivity {
    Button btn;
    VideoView videoView = null;
    SpinKitView loadingProcess;

    private VideoPlayRuntimeDO videoPlayRuntimeDO;
    private VideoDetailDO videoDetailDO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //videoPlayRuntimeDO = (VideoPlayRuntimeDO) IntentUtil.getData(getIntent(), "videoPlayRuntimeDO");

        videoPlayRuntimeDO = new VideoPlayRuntimeDO();
        videoPlayRuntimeDO.setUrl("https://bitdash-a.akamaihd.net/content/sintel/hls/playlist.m3u8");
        videoPlayRuntimeDO.setStartTime(0L);
        videoDetailDO = (VideoDetailDO) IntentUtil.getData(getIntent(), "videoDetailDO");

        setContentView(R.layout.activity_video_play);

        loadingProcess = (SpinKitView) findViewById(R.id.spin_kit);
        loadingProcess.setVisibility(View.VISIBLE);

        videoView = (VideoView) findViewById(R.id.VideoViewfull);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri video = Uri.parse(videoPlayRuntimeDO.getUrl());
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);
        //mediaController.setPrevNextListeners(null, null);
        DebugUtil.whichViewFocusing(this);
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return false;
            }
        });
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                loadingProcess.setVisibility(View.GONE);
                videoView.start();
                videoView.seekTo(videoPlayRuntimeDO.getStartTime().intValue());

                mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer mp, int what, int extra) {
                        switch (what) {
                            case MediaPlayer.MEDIA_INFO_BUFFERING_START: {
                                loadingProcess.setVisibility(View.VISIBLE);
                                return true;
                            }
                            case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                            case MediaPlayer.MEDIA_INFO_BUFFERING_END: {
                                loadingProcess.setVisibility(View.GONE);
                                return true;
                            }
                        }
                        return false;
                    }
                });

            }
        });
    }

    @Override
    public void finish() {
        videoPlayRuntimeDO.setStartTime(Long.valueOf(videoView.getCurrentPosition()));

        Intent data = new Intent();
        IntentUtil.putData(data, "response", videoPlayRuntimeDO);
        setResult(RESULT_OK, data);

        videoView.suspend();
        super.finish();
    }
}