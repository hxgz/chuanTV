package com.hxgz.chuantv.widget.ImageCardView;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.hxgz.chuantv.R;
import com.hxgz.chuantv.dataobject.VideoInfoDO;
import com.hxgz.chuantv.utils.ImageUtil;
import com.open.androidtvwidget.leanback.mode.DefualtListPresenter;
import org.apache.commons.lang3.StringUtils;

/**
 * Leanback 横向item demo.
 * 如果你想改变标题头的样式，那就写自己的吧.
 * Created by hailongqiu on 2016/8/25.
 */
public class TestMoviceListPresenter extends DefualtListPresenter {
    Integer num = 6;

    /**
     * 你可以重写这里，传入AutoGridViewLayoutManger.
     */
    @Override
    public RecyclerView.LayoutManager getLayoutManger(Context context) {
        return super.getLayoutManger(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_card_view, parent, false);

        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) itemView.getLayoutParams();
        int paddingWidth = lp.leftMargin + lp.rightMargin;
        int screenWidth = itemView.getContext().getResources().getDisplayMetrics().widthPixels;
        int width = (screenWidth - paddingWidth) / num - paddingWidth;

        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = (int) (width * 1.5);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        VideoInfoDO videoInfoDO = ((VideoInfoDO) getItem(position));
        final OpenCardView openCardView = (OpenCardView) viewHolder.view;

        ImageUtil.displayImage(openCardView, videoInfoDO.getImgUrl());
        Drawable d = viewHolder.view.getResources().getDrawable(R.drawable.ic_sp_block_focus);
        openCardView.setShadowDrawable(d);
        //
        TextView tv = (TextView) openCardView.findViewById(R.id.title_tv);
        tv.setText(videoInfoDO.getTitle());
        TextView statusV = (TextView) openCardView.findViewById(R.id.status_tv);
        if (StringUtils.isNoneBlank(videoInfoDO.getStatus())) {
            statusV.setText(videoInfoDO.getStatus());
            statusV.setVisibility(View.VISIBLE);
        }

        openCardView.setAlpha(1.0f);

        openCardView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).start();
                } else {
                    v.animate().scaleX(1.0f).scaleY(1.0f).setDuration(300).start();
                }
            }
        });
    }

}
