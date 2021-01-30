package com.hxgz.chuantv;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.hxgz.chuantv.dataobject.NavItemDO;
import com.hxgz.chuantv.dataobject.SectionItemDO;
import com.hxgz.chuantv.dataobject.VideoInfoDO;
import com.hxgz.chuantv.dataobject.VideoSectionPageDO;
import com.hxgz.chuantv.extractors.TVExtractor;
import com.hxgz.chuantv.utils.IntentUtil;
import com.hxgz.chuantv.utils.LogUtil;
import com.hxgz.chuantv.widget.ImageCardView.TestMoviceListPresenter;
import com.open.androidtvwidget.bridge.RecyclerViewBridge;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.*;
import com.open.androidtvwidget.leanback.recycle.RecyclerViewTV;
import com.open.androidtvwidget.view.MainUpView;
import com.owen.tab.TvTabLayout;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhoujianwu
 * @date 2020/10/18
 * @description：
 */
public class HomeActivity extends BackPressActivity implements RecyclerViewTV.OnItemListener {
    TVExtractor tvExtractor;

    GifImageView splashView;
    View bodyView;

    TvTabLayout mTabLayout;

    List<NavItemDO> navItemDOList;
    List<SectionItemDO> sectionItemDOList;
    String currentNavId;

    MainUpView mMainUpView;
    RecyclerViewTV mRecyclerView;
    RecyclerViewBridge mRecyclerViewBridge;
    List<ListRow> sectionViewList = new ArrayList<>();
    ListRowPresenter mListRowPresenter;

    View oldView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bodyView = findViewById(R.id.body);
        splashView = (GifImageView) findViewById(R.id.spash);
        bodyView.setVisibility(View.GONE);

        splash();

        tvExtractor = App.getTVForSearch();

        View searchAction = findViewById(R.id.searchAction);
        searchAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(HomeActivity.this, SearchActivity.class);
                startActivity(detailIntent);
            }
        });

        initTab();

        initSection();

        if (CollectionUtils.isEmpty(navItemDOList)) {
            loadData(null);
        }
    }

    public void splash() {
        GifDrawable gifDrawable = (GifDrawable) splashView.getDrawable();
        gifDrawable.start();
        gifDrawable.setLoopCount(1);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                splashClose();
            }
        }, gifDrawable.getDuration());
    }

    public void splashClose() {
        if (splashView.getVisibility() == View.GONE) {
            return;
        }

        int shortAnimationDuration = 800;

//        splashView.animate()
//                .alpha(0f)
//                .setDuration(shortAnimationDuration)
//                .setListener(new AnimatorListenerAdapter() {
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        splashView.setVisibility(View.GONE);
//                    }
//                });
        splashView.setVisibility(View.GONE);
        bodyView.setAlpha(0f);
        bodyView.setVisibility(View.VISIBLE);
        bodyView.animate()
                .alpha(1f)
                .setDuration(shortAnimationDuration)
                .setListener(null);
    }

    public float getDimension(int id) {
        return getResources().getDimension(id);
    }

    public void drawTabs(int selectPosition) {
        if (null != navItemDOList) {
            for (int i = 0; i < navItemDOList.size(); i++) {
                NavItemDO itemDO = navItemDOList.get(i);
                TvTabLayout.Tab tab = mTabLayout.newTab().setText(itemDO.getTitle());
                mTabLayout.addTab(tab, i == selectPosition);
            }
        }
    }

    public void initTab() {
        mTabLayout = findViewById(R.id.navTab);
        setBeforeCloseFocusView(mTabLayout);

        navItemDOList = tvExtractor.defaultNav();
        drawTabs(0);

        mTabLayout.addOnTabSelectedListener(new TvTabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TvTabLayout.Tab tab) {
                int position = tab.getPosition();
                if (CollectionUtils.isEmpty(navItemDOList)) {
                    return;
                }
                String navId = navItemDOList.get(position).getNavId();

                if (mListRowPresenter != null && !navId.equals(currentNavId)) {
                    currentNavId = navId;
                    loadData(currentNavId);
                }
            }

            @Override
            public void onTabUnselected(TvTabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TvTabLayout.Tab tab) {
            }
        });

        mTabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TvTabLayout tvTabLayout = (TvTabLayout) v;
                NavItemDO itemDO = navItemDOList.get(tvTabLayout.getSelectedTabPosition());

                Intent detailIntent = new Intent(HomeActivity.this, PickerActivity.class);
                IntentUtil.putData(detailIntent, "refer", itemDO.getNavId());
                startActivity(detailIntent);
            }
        });

    }

    public void initSection() {
        mRecyclerView = (RecyclerViewTV) findViewById(R.id.recyclerView);
        mMainUpView = (MainUpView) findViewById(R.id.mainUpView);
        mMainUpView.setEffectBridge(new RecyclerViewBridge());
        // 注意这里，需要使用 RecyclerViewBridge 的移动边框 Bridge.
        mRecyclerViewBridge = (RecyclerViewBridge) mMainUpView.getEffectBridge();
        //mRecyclerViewBridge.setUpRectResource(R.drawable.video_cover_cursor);
        float density = getResources().getDisplayMetrics().density;
        RectF receF = new RectF(getDimension(R.dimen.h_45) * density, getDimension(R.dimen.h_40) * density,
                getDimension(R.dimen.h_45) * density, getDimension(R.dimen.h_40) * density);
        mRecyclerViewBridge.setDrawUpRectPadding(receF);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setSelectedItemAtCentered(true); // 设置item在中间移动.

        mListRowPresenter = new ListRowPresenter(
                sectionViewList, new ItemHeaderPresenter(), new ItemListPresenter());
        GeneralAdapter generalAdapter = new GeneralAdapter(mListRowPresenter);
        mRecyclerView.setAdapter(generalAdapter);

        mRecyclerView.setOnItemListener(this);
    }

    private void loadData(final String navId) {
        new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                final VideoSectionPageDO videoSectionPageDO;

                LogUtil.d("request navId:" + navId);
                videoSectionPageDO = tvExtractor.previewNav(navId);
                sectionItemDOList = videoSectionPageDO.getSectionItemDOList();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 导航变更
                        List<NavItemDO> newNavList = videoSectionPageDO.getNavItemDOList();
                        if (CollectionUtils.isNotEmpty(newNavList) && !newNavList.equals(navItemDOList)) {
                            NavItemDO oldItemDO = navItemDOList.get(mTabLayout.getSelectedTabPosition());

                            navItemDOList = newNavList;
                            mTabLayout.removeAllTabs();
                            drawTabs(newNavList.stream()
                                    .map(NavItemDO::getNavId)
                                    .collect(Collectors.toList())
                                    .indexOf(oldItemDO.getNavId())
                            );
                        }
                        //mTabLayout.selectTab(0);

                        // 榜单数据
                        sectionViewList.clear();
                        for (SectionItemDO sectionItemDO : sectionItemDOList) {
                            ListRow sectionRow = new ListRow(sectionItemDO.getTitle());
                            TestMoviceListPresenter presenter = new TestMoviceListPresenter();
                            presenter.setOnItemClickListener(new RecyclerViewTV.OnItemClickListener() {
                                @Override
                                public void onItemClick(RecyclerViewTV parent, View itemView, int position) {
                                    GeneralAdapter adapter = (GeneralAdapter) parent.getAdapter();
                                    VideoInfoDO videoInfoDO = (VideoInfoDO) ((DefualtListPresenter) adapter.getPresenter()).getItem(position);
                                    Intent detailIntent = new Intent(HomeActivity.this, VideoDetailActivity.class);
                                    IntentUtil.putData(detailIntent, "videoInfoDO", videoInfoDO);
                                    startActivity(detailIntent);
                                }
                            });
                            sectionRow.setOpenPresenter(presenter); // 设置列的item样式.

                            for (VideoInfoDO videoInfoDO : sectionItemDO.getVideoInfoDOList()) {
                                sectionRow.add(videoInfoDO);
                            }
                            sectionViewList.add(sectionRow);
                        }

                        mListRowPresenter.setItems(sectionViewList);
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        //mListRowPresenter.setDefaultPos(0, 0); // 设置默认选中.

                    }
                });
            }
        }).start();
    }

    /**
     * 排除 Leanback demo的RecyclerView.
     */
    private boolean isListRowPresenter() {
        GeneralAdapter generalAdapter = (GeneralAdapter) mRecyclerView.getAdapter();
        OpenPresenter openPresenter = generalAdapter.getPresenter();
        return (openPresenter instanceof ListRowPresenter);
    }

    @Override
    public void onItemPreSelected(RecyclerViewTV parent, View itemView, int position) {
        if (!isListRowPresenter()) {
            mRecyclerViewBridge.setUnFocusView(oldView);
        }
    }

    @Override
    public void onItemSelected(RecyclerViewTV parent, View itemView, int position) {
        if (!isListRowPresenter()) {
            mRecyclerViewBridge.setFocusView(itemView, 1.2f);
            oldView = itemView;
        }
    }

    @Override
    public void onReviseFocusFollow(RecyclerViewTV parent, View itemView, int position) {
        if (position == 0) {
            mRecyclerView.smoothScrollToPosition(0);
        }

        if (!isListRowPresenter()) {
            mRecyclerViewBridge.setFocusView(itemView, 1.2f);
            oldView = itemView;
        }
    }

    @Override
    public void onBackPressed() {
        if (splashView.getVisibility() == View.VISIBLE) {
            splashClose();
            return;
        }

        final View currentFocus = getCurrentFocus();
        if (currentFocus instanceof TvTabLayout
                && ((TvTabLayout) currentFocus).getSelectedTabPosition() != 0) {
            ((TvTabLayout) currentFocus).selectTab(0);
            return;
        }

        super.onBackPressed();
    }
}

    