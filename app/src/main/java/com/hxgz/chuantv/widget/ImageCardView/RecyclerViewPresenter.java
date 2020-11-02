package com.hxgz.chuantv.widget.ImageCardView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.hxgz.chuantv.R;
import com.hxgz.chuantv.dataobject.VideoInfoDO;
import com.hxgz.chuantv.utils.ImageUtil;
import com.open.androidtvwidget.leanback.adapter.GeneralAdapter;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试.
 * Created by hailongqiu on 2016/8/24.
 */
public class RecyclerViewPresenter extends OpenPresenter {

    private List<VideoInfoDO> labels;
    private GeneralAdapter mAdapter;

    public RecyclerViewPresenter() {
        labels = new ArrayList<>();
    }

    @Override
    public void setAdapter(GeneralAdapter adapter) {
        this.mAdapter = adapter;
    }

    /**
     * 用于数据加载更多测试.
     */
    public void addDatas(List<VideoInfoDO> videoInfoDOS) {
        labels.addAll(videoInfoDOS);
        this.mAdapter.notifyDataSetChanged();
    }

    public void clearData() {
        labels.clear();
        this.mAdapter.notifyDataSetChanged();
    }

    public VideoInfoDO getData(int position) {
        return labels.get(position);
    }

    @Override
    public int getItemCount() {
        return labels.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recyclerview_view, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        VideoInfoDO videoInfo = labels.get(position);

        GridViewHolder gridViewHolder = (GridViewHolder) viewHolder;

        ImageView imageView = gridViewHolder.iv;
        TextView textView = (TextView) gridViewHolder.tv;

        ImageUtil.displayImage(imageView, videoInfo.getImgUrl());
        textView.setText(videoInfo.getTitle());
    }

}
