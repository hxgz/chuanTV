package com.hxgz.chuantv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.hxgz.chuantv.dataobject.*;
import com.hxgz.chuantv.extractors.TVExtractor;
import com.hxgz.chuantv.utils.IntentUtil;
import com.hxgz.chuantv.utils.LogUtil;
import com.hxgz.chuantv.utils.NoticeUtil;
import com.hxgz.chuantv.widget.ImageCardView.RecyclerViewPresenter;
import com.hxgz.chuantv.widget.TextPickerViewList;
import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.recycle.GridLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.view.MainUpView;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoujianwu
 * @description：
 */
public class PickerActivity extends Activity implements RecyclerViewTV.OnItemListener {
    TVExtractor tvExtractor;

    String refer;

    List<TextPickerViewList> pickerViewList = new ArrayList<>();

    List<TVPickerDO> pickerDOList;
    Integer pickerPage;
    boolean noMore;
    RecyclerViewTV mRecyclerView;
    RecyclerViewPresenter mRecyclerViewPresenter;
    RecyclerViewBridge mRecyclerViewBridge;
    GeneralAdapter mGeneralAdapter;
    MainUpView mMainUpView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tvExtractor = App.getTVForSearch();
        setContentView(R.layout.activity_picker);

        refer = (String) IntentUtil.getData(getIntent(), "refer");

        mRecyclerView = (RecyclerViewTV) findViewById(R.id.recyclerView2);
        mMainUpView = (MainUpView) findViewById(R.id.mainUpView2);
        mMainUpView.setEffectBridge(new RecyclerViewBridge());
        mRecyclerViewBridge = (RecyclerViewBridge) mMainUpView.getEffectBridge();
        float density = getResources().getDisplayMetrics().density;
        RectF receF = new RectF(
                getDimension(R.dimen.h_45) * density, getDimension(R.dimen.h_40) * density,
                getDimension(R.dimen.h_45) * density, getDimension(R.dimen.h_40) * density);
        mRecyclerViewBridge.setDrawUpRectPadding(receF);

        initPicker();

        initImageList(LinearLayoutManager.VERTICAL);

        loadData();
    }

    public void initPicker() {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                pickerDOList = tvExtractor.getPicker(refer);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        LinearLayout pickerHolder = (LinearLayout) findViewById(R.id.tvPicker);
                        for (int i = 0; i < pickerDOList.size(); i++) {
                            TVPickerDO pickerDO = pickerDOList.get(i);

                            TextPickerViewList listView = new TextPickerViewList(pickerHolder);
                            listView.setTvPickerDO(pickerDO);
                            listView.setMOnClickListen(new TextPickerViewList.onClickListen() {
                                @Override
                                public void onclick(View view, Object data) {
                                    PickerActivity.this.pickerPage = 1;
                                    PickerActivity.this.noMore = false;
                                    mRecyclerViewPresenter.clearData();
                                    loadData();
                                }
                            });
                            listView.asView();

                            pickerViewList.add(listView);
                        }
                    }
                });
            }
        }).start();
    }

    public float getDimension(int id) {
        return getResources().getDimension(id);
    }

    private void loadData() {
        if (noMore) {
            return;
        }

        List<TVPickParam> tvPickParamList = new ArrayList<>();
        for (TextPickerViewList pickerViewList : pickerViewList) {
            TVPickParam pickerData = pickerViewList.getPickerData();
            if (null != pickerData) tvPickParamList.add(pickerData);
        }
        LogUtil.e(tvPickParamList.toString());
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                try {
                    final List<VideoInfoDO> videoInfoDOList = tvExtractor.pickTV(tvPickParamList, pickerPage++);
                    if (CollectionUtils.isEmpty(videoInfoDOList)) {
                        PickerActivity.this.noMore = true;
                        return;
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerViewPresenter.addDatas(videoInfoDOList);
                            mRecyclerView.setOnLoadMoreComplete();
                        }
                    });
                } catch (Exception e) {
                    NoticeUtil.show(PickerActivity.this, "发生异常,请重试");
                    return;
                }
            }
        }).start();
    }

    private void initImageList(int orientation) {
        noMore = false;
        pickerPage = 1;

        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, 6); // 解决快速长按焦点丢失问题.
        gridlayoutManager.setOrientation(orientation);
        mRecyclerView.setLayoutManager(gridlayoutManager);
        mRecyclerView.setFocusable(false);
        mRecyclerView.setSelectedItemAtCentered(true); // 设置item在中间移动.
        mRecyclerViewPresenter = new RecyclerViewPresenter();
        mGeneralAdapter = new GeneralAdapter(mRecyclerViewPresenter);
        mGeneralAdapter.setHasStableIds(true);
        mRecyclerView.setAdapter(mGeneralAdapter);
        mRecyclerView.setPagingableListener(new RecyclerViewTV.PagingableListener() {
            @Override
            public void onLoadMoreItems() {
                loadData();
            }
        });
        mRecyclerView.setOnItemListener(this);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                VideoInfoDO videoInfoDO = mRecyclerViewPresenter.getData(position);
                Intent detailIntent = new Intent(PickerActivity.this, VideoDetailActivity.class);
                IntentUtil.putData(detailIntent, "videoInfoDO", videoInfoDO);
                startActivity(detailIntent);
            }
        });
    }

    @Override
    public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
        mRecyclerViewBridge.setUnFocusView(itemView);
    }

    @Override
    public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
        mRecyclerViewBridge.setFocusView(itemView, 1.2f);
    }

    @Override
    public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
        mRecyclerViewBridge.setFocusView(itemView, 1.2f);
    }

}

    