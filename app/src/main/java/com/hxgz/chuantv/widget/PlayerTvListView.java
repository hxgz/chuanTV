package com.hxgz.chuantv.widget;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.hxgz.chuantv.R;
import com.hxgz.chuantv.dataobject.LiveTvDO;
import com.hxgz.chuantv.playback.PlaybackService;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhoujianwu
 * @date 2020/10/25
 * @description：
 */
public class PlayerTvListView extends LinearLayout {
    LayoutInflater inflater;

    private LinearLayout categoryListLayout;

    private ScrollView tvScrollView;
    private LinearLayout tvListLayout;

    private PlaybackService playbackService;


    public PlayerTvListView(Context context) {
        this(context, null);
    }

    public PlayerTvListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerTvListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PlayerTvListView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.playback_tvlist_view, this);

        categoryListLayout = findViewById(R.id.showTvCategoryList);
        tvListLayout = findViewById(R.id.showTvList);
        tvScrollView = (ScrollView) tvListLayout.getParent();
    }

    private boolean isLiveTv;
    private int playingPosition;

    private Runnable hideTVList = new Runnable() {
        @Override
        public void run() {
            setVisibility(View.INVISIBLE);
            removeCallbacks(hideTVList);
        }
    };

    public boolean isLiveMode() {
        return this.isLiveTv;
    }

    public void setTvList(List<LiveTvDO> tvDOList) {
        int cateIndex = 0;
        for (Map.Entry<String, Map<String, List<LiveTvDO>>> entry : sorted(tvDOList).entrySet()) {
            String category = entry.getKey();
            final TextView cateView = (TextView) inflater.inflate(R.layout.widget_text_exoplayer_chanel, this, false);
            cateView.setText(category);
            cateView.setTag(cateIndex);
            cateView.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    scrollToCenter(tvScrollView, tvListLayout.getChildAt((int) v.getTag()));
                    hideTVListAfterTimeout();
                }
            });
            cateView.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
                if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            hideTVList.run();
                            return true;
                        case KeyEvent.KEYCODE_DPAD_RIGHT:
                            tvListLayout.getChildAt((int) v.getTag()).requestFocus();
                            return true;
                        case KeyEvent.KEYCODE_DPAD_UP:
                            if (0 == categoryListLayout.indexOfChild(v)) {
                                return true;
                            }
                            return false;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            if (categoryListLayout.getChildCount() - 1 == categoryListLayout.indexOfChild(v)) {
                                return true;
                            }
                            return false;
                    }
                }
                return false;
            });

            categoryListLayout.addView(cateView);

            for (Map.Entry<String, List<LiveTvDO>> entry2 : entry.getValue().entrySet()) {
                cateIndex++;
                String chanel = entry2.getKey();
                LiveTvDO liveTvDO = entry2.getValue().get(0);

                TextView view = (TextView) inflater.inflate(R.layout.widget_text_exoplayer_chanel, this, false);
                view.setTag(liveTvDO);
                view.setText(chanel);
                view.setOnClickListener(v -> {
                    PlayerTvListView.this.playTv(tvListLayout.indexOfChild(v));
                });
                view.setOnFocusChangeListener((v, hasFocus) -> {
                    if (hasFocus) {
                        setSelected(categoryListLayout, cateView);
                        scrollToCenter(tvScrollView, v);
                        hideTVListAfterTimeout();
                    }
                });
                view.setOnKeyListener((View v, int keyCode, KeyEvent event) -> {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && event.getRepeatCount() == 0) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_BACK:
                                hideTVList.run();
                                return true;
                            case KeyEvent.KEYCODE_DPAD_LEFT:
                                cateView.requestFocus();
                                return true;
                        }
                    }
                    return false;
                });
                tvListLayout.addView(view);
            }
        }
        isLiveTv = true;
    }

    public void showTVList() {
        setVisibility(View.VISIBLE);

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

    public void setPlaybackService(PlaybackService playbackService) {
        this.playbackService = playbackService;
    }

    private void scrollToCenter(ScrollView scrollView, View childView) {
        System.out.println(scrollView.toString());
        int scrollViewHeight = scrollView.getHeight();
        int height = (childView.getTop() + childView.getBottom()) / 2;

        int toY = height - scrollViewHeight / 2;
        scrollView.scrollTo(0, toY);
    }

    private Map<String, Map<String, List<LiveTvDO>>> sorted(List<LiveTvDO> tvDOList) {
        Map<String, Map<String, List<LiveTvDO>>> byCate = new LinkedHashMap<>();
        tvDOList.forEach(tvDO -> {
            Map<String, List<LiveTvDO>> byPlatform = byCate.getOrDefault(tvDO.getCategory(), new LinkedHashMap());
            byCate.put(tvDO.getCategory(), byPlatform);

            List<LiveTvDO> liveTvDOList = byPlatform.getOrDefault(tvDO.getChannel(), new ArrayList<>());
            byPlatform.put(tvDO.getChannel(), liveTvDOList);

            liveTvDOList.add(tvDO);
        });

        return byCate;
    }

    private void setSelected(LinearLayout linearLayout, View childView) {
        for (int i = 0; i < linearLayout.getChildCount(); i++) {
            linearLayout.getChildAt(i).setSelected(false);
        }
        childView.setSelected(true);
    }
}

