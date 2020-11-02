package com.hxgz.chuantv;

import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.alibaba.fastjson.JSON;
import com.hxgz.chuantv.dataobject.*;
import com.hxgz.chuantv.extractors.TVExtractor;
import com.hxgz.chuantv.utils.DebugUtil;
import com.hxgz.chuantv.utils.IntentUtil;
import com.hxgz.chuantv.utils.LogUtil;
import com.hxgz.chuantv.utils.ViewUtil;
import com.hxgz.chuantv.widget.textview.ListTextView;
import com.hxgz.chuantv.widget.textview.ObjectTextView;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class VideoDetailActivity extends Activity {
    TVExtractor tvExtractor;

    VideoInfoDO videoInfoDO;

    Button btn;
    VideoView videoView = null;

    VideoDetailDO videoDetailDO;

    List<ListTextView> fileListView = new ArrayList<>();
    int selectedPlatformPosition = -1; // 平台位置
    int selectedVideoPosition = -1; // 平台对应的视频位置

    final int REQUEST_CODE = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvExtractor = App.getTVForSearch();

        videoInfoDO = (VideoInfoDO) IntentUtil.getData(getIntent(), "videoInfoDO");

        setContentView(R.layout.activity_video_detail);
        videoView = (VideoView) findViewById(R.id.VideoPreview);
        btn = (Button) findViewById(R.id.btnfullScreen);
        btn.requestFocus();

        DebugUtil.whichViewFocusing(this);

        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullScreen();
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullScreen();
            }
        });
        loadData();
    }

    private void fullScreen() {
        if (selectedPlatformPosition < 0 || selectedVideoPosition < 0) {
            // TODO: 提示
            return;
        }
        PlatformVideoFileDO.VideoFileDO videoFileDO = getSelectedVideoFile();

        VideoPlayRuntimeDO videoPlayRuntimeDO = new VideoPlayRuntimeDO();
        videoPlayRuntimeDO.setUrl(videoFileDO.getUrl());
        videoPlayRuntimeDO.setStartTime(videoView.getCurrentPosition());
        videoPlayRuntimeDO.setSelectedPlatformPosition(selectedPlatformPosition);
        videoPlayRuntimeDO.setSelectedVideoPosition(selectedVideoPosition);

        Intent videointent = new Intent(VideoDetailActivity.this, VideoPlayActivity.class);
        IntentUtil.putData(videointent, "videoPlayRuntimeDO", videoPlayRuntimeDO);
        IntentUtil.putData(videointent, "videoDetailDO", videoDetailDO);

        startActivityForResult(videointent, REQUEST_CODE);
    }

    private void loadData() {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                videoDetailDO = tvExtractor.getVideo(videoInfoDO.getVideoId());

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

                        LinearLayout anthorView = (LinearLayout) findViewById(R.id.videoAuthor);
                        for (String actor : infoDO.getActorList()) {
                            TextView textView = ViewUtil.getTextViewForShow(VideoDetailActivity.this);
                            textView.setText(actor);
                            anthorView.addView(textView);
                        }

                        ((TextView) findViewById(R.id.videoDesc)).setText("简介：" + infoDO.getDesc());

                        LinearLayout tagView = (LinearLayout) findViewById(R.id.VideoTag);
                        for (String tag : infoDO.getTagList()) {
                            TextView textView = ViewUtil.getTextViewForShow(VideoDetailActivity.this);
                            textView.setText(tag);
                            tagView.addView(textView);
                        }

                        // 剧集列表
                        LinearLayout showListView = (LinearLayout) findViewById(R.id.showList);
                        for (int i = 0; i < videoDetailDO.getFileDOList().size(); i++) {
                            PlatformVideoFileDO videoFileDO = videoDetailDO.getFileDOList().get(i);

                            ListTextView listView = new ListTextView();
                            listView.setTag(videoFileDO.getShow());
                            listView.setMItems(videoFileDO.getVideoFileDOList());
                            fileListView.add(listView);
                            final int index = i;
                            listView.setMOnClickListen(new ListTextView.onClickListen() {
                                @Override
                                public void onclick(ObjectTextView view) {
                                    ListTextView oldListView = VideoDetailActivity.this.getSelectedListView();
                                    if (null != oldListView)
                                        oldListView.unSelectedPosition();

                                    VideoDetailActivity.this.startVideoPlay(index, view.getnIndex(), 0);
                                }
                            });

                            listView.addToView(showListView);
                        }
                        VideoDetailActivity.this.startVideoPlay(0, 0, 0);
                    }
                });
            }
        }).start();
    }

    private ListTextView getSelectedListView() {
        try {
            return fileListView.get(selectedPlatformPosition);
        } catch (Exception e) {
            return null;
        }
    }

    private PlatformVideoFileDO.VideoFileDO getSelectedVideoFile() {
        try {
            return videoDetailDO.getFileDOList()
                    .get(this.selectedPlatformPosition)
                    .getVideoFileDOList()
                    .get(this.selectedVideoPosition);
        } catch (Exception e) {
            return null;
        }
    }

    private void startVideoPlay(int platformPosition, int videoPosition, int startTime) {
        this.selectedPlatformPosition = platformPosition;
        this.selectedVideoPosition = videoPosition;

        ListTextView listView = getSelectedListView();
        listView.setSelectedPosition(this.selectedVideoPosition);

        PlatformVideoFileDO.VideoFileDO videoFileDO = getSelectedVideoFile();
        if (null == videoFileDO) {
            LogUtil.e("plt:" + platformPosition + " vd:" + videoPosition + " tm:" + startTime);
            // TODO:
            return;
        }
        if (null != videoView) {
            videoView.setVideoURI(Uri.parse(videoFileDO.getUrl()));
            videoView.start();
            videoView.seekTo(startTime);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            VideoPlayRuntimeDO respPlayDO = (VideoPlayRuntimeDO) IntentUtil.getData(data, "response");
            startVideoPlay(respPlayDO.getSelectedPlatformPosition(), respPlayDO.getSelectedVideoPosition(), respPlayDO.getStartTime());
        }
    }
}