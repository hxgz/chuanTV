package com.hxgz.chuantv;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;
import com.hxgz.chuantv.dataobject.VideoDetailDO;
import com.hxgz.chuantv.dataobject.VideoPlayRuntimeDO;
import com.hxgz.chuantv.utils.IntentUtil;

//https://cloud.tencent.com/developer/article/1384945
public class VideoPlayActivity extends Activity {
    Button btn;
    VideoView videoView = null;
    private static ProgressDialog progressDialog;

    private VideoPlayRuntimeDO videoPlayRuntimeDO;
    private VideoDetailDO videoDetailDO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        videoPlayRuntimeDO = (VideoPlayRuntimeDO) IntentUtil.getData(getIntent(), "videoPlayRuntimeDO");
        videoDetailDO = (VideoDetailDO) IntentUtil.getData(getIntent(), "videoDetailDO");

        setContentView(R.layout.activity_video_play);
        progressDialog = ProgressDialog.show(this, "", "Loading...", true, true);
        videoView = (VideoView) findViewById(R.id.VideoViewfull);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        Uri video = Uri.parse(videoPlayRuntimeDO.getUrl());
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(video);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer arg0) {
                progressDialog.dismiss();
                videoView.start();
                videoView.seekTo(videoPlayRuntimeDO.getStartTime());
            }
        });
    }

    @Override
    public void finish() {
        videoPlayRuntimeDO.setStartTime(videoView.getCurrentPosition());

        Intent data = new Intent();
        IntentUtil.putData(data, "response", videoPlayRuntimeDO);

        setResult(RESULT_OK, data);
        super.finish();
    }
}