package com.hxgz.chuantv;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.hxgz.chuantv.dataobject.*;
import com.hxgz.chuantv.extractors.TVExtractor;
import com.hxgz.chuantv.utils.IntentUtil;
import com.hxgz.chuantv.utils.LogUtil;
import com.hxgz.chuantv.widget.ImageCardView.RecyclerViewPresenter;
import com.hxgz.chuantv.widget.ImageCardView.TestMoviceListPresenter;
import com.hxgz.chuantv.widget.textview.ListPickerTextView;
import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.*;
import com.open.androidtvwidget.leanback.recycle.GridLayoutManagerTV;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.view.MainUpView;
import com.owen.tab.TvTabLayout;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhoujianwu
 * @description：
 */
public class SearchActivity extends Activity implements RecyclerViewTV.OnItemListener {
    TVExtractor tvExtractor;

    String searchText;
    int pageSize = 1;
    boolean noMore = false;

    SearchView searchView;
    RecyclerViewTV mRecyclerView;
    RecyclerViewPresenter mRecyclerViewPresenter;
    RecyclerViewBridge mRecyclerViewBridge;
    GeneralAdapter mGeneralAdapter;
    MainUpView mMainUpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tvExtractor = App.getTVForSearch();
        searchView = findViewById(R.id.tvSearchText);
        searchView.onActionViewExpanded();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.equals(searchText)) {
                    return true;
                }
                searchText = query;
                noMore = false;
                search(query);
                //点击搜索
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        mRecyclerView = (RecyclerViewTV) findViewById(R.id.recyclerView2);

        mMainUpView = (MainUpView) findViewById(R.id.mainUpView2);
        mMainUpView.setEffectBridge(new

                RecyclerViewBridge());
        mRecyclerViewBridge = (RecyclerViewBridge) mMainUpView.getEffectBridge();
        float density = getResources().getDisplayMetrics().density;
        RectF receF = new RectF(
                getDimension(R.dimen.h_45) * density, getDimension(R.dimen.h_40) * density,
                getDimension(R.dimen.h_45) * density, getDimension(R.dimen.h_40) * density);
        mRecyclerViewBridge.setDrawUpRectPadding(receF);

        initImageList(LinearLayoutManager.VERTICAL);

    }

    public float getDimension(int id) {
        return getResources().getDimension(id);
    }

    private void initImageList(int orientation) {
        GridLayoutManagerTV gridlayoutManager = new GridLayoutManagerTV(this, 5); // 解决快速长按焦点丢失问题.
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
                search(searchText);
            }
        });
        mRecyclerView.setOnItemListener(this);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                VideoInfoDO videoInfoDO = mRecyclerViewPresenter.getData(position);
                Intent detailIntent = new Intent(SearchActivity.this, VideoDetailActivity.class);
                IntentUtil.putData(detailIntent, "videoInfoDO", videoInfoDO);
                startActivity(detailIntent);
            }
        });
    }

    private void search(String text) {
        if (noMore) {
            return;
        }

        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                final List<VideoInfoDO> videoInfoDOList = tvExtractor.search(text, pageSize++);
                if (CollectionUtils.isEmpty(videoInfoDOList)) {
                    noMore = true;
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerViewPresenter.addDatas(videoInfoDOList);
                        mRecyclerView.setOnLoadMoreComplete();
                    }
                });
            }
        }).start();
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

    
