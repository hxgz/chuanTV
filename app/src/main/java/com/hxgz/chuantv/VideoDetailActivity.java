package com.hxgz.chuantv;

import android.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.hxgz.chuantv.dataobject.*;
import com.hxgz.chuantv.extractors.TVExtractor;
import com.hxgz.chuantv.utils.*;
import com.hxgz.chuantv.widget.ScrollViewList;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class VideoDetailActivity extends BackPressActivity {
    TVExtractor tvExtractor;

    VideoInfoDO videoInfoDO;

    Button btn;
    VideoView videoView = null;

    VideoDetailDO videoDetailDO;
    List<ScrollViewList> fileViewLists = new ArrayList<>();

    final int REQUEST_CODE = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvExtractor = App.getTVForSearch();

        videoInfoDO = (VideoInfoDO) IntentUtil.getData(getIntent(), "videoInfoDO");

        setContentView(R.layout.activity_video_detail);
        videoView = (VideoView) findViewById(R.id.VideoPreview);
        btn = (Button) findViewById(R.id.btnfullScreen);
        btn.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start();
            } else {
                v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
            }
        });
        setBeforeCloseFocusView(btn);
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
        for (int i = 0; i < fileViewLists.size(); i++) {
            ScrollViewList scrollViewList = fileViewLists.get(i);
            for (int j = 0; j < scrollViewList.getAllItems().size(); j++) {
                if (scrollViewList.getAllItems().get(j).isSelected()) {
                    fullScreen(i, j, false);
                }
            }
        }
    }

    private void fullScreen(int platformPosition, int videoPosition, boolean startZero) {
        PlatformVideoFileDO.VideoFileDO videoFileDO = getSelectedVideoFile(platformPosition, videoPosition);

        VideoPlayRuntimeDO videoPlayRuntimeDO = new VideoPlayRuntimeDO();
        videoPlayRuntimeDO.setUrl(videoFileDO.getUrl());
        videoPlayRuntimeDO.setStartTime(startZero ? 0 : videoView.getCurrentPosition());
        videoPlayRuntimeDO.setSelectedPlatformPosition(platformPosition);
        videoPlayRuntimeDO.setSelectedVideoPosition(videoPosition);

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
                            scrollViewList.setMOnClickListen((view, data) -> {
                                fileViewLists.forEach(scvl -> {
                                    scvl.clearItemSelected();
                                });
                                int position = scrollViewList.getItemIndex(view);
                                VideoDetailActivity.this.fullScreen(index, position, !view.isSelected());
                            });
                            showListView.addView(scrollViewList);

                            fileViewLists.add(scrollViewList);
                        }

                        VideoDetailActivity.this.previewVideoPlay(0, 0, 0);
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

    private void previewVideoPlay(int platformPosition, int videoPosition, int startTime) {
        ScrollViewList scrollViewList = fileViewLists.get(platformPosition);
        scrollViewList.selectItem(videoPosition);

        PlatformVideoFileDO.VideoFileDO videoFileDO = getSelectedVideoFile(platformPosition, videoPosition);
        if (null == videoFileDO) {
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
            previewVideoPlay(respPlayDO.getSelectedPlatformPosition(), respPlayDO.getSelectedVideoPosition(), respPlayDO.getStartTime());
        }
    }
}