package com.hxgz.chuantv.widget.ImageCardView;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.hxgz.chuantv.R;
import com.open.androidtvwidget.leanback.mode.OpenPresenter;

public class GridViewHolder extends OpenPresenter.ViewHolder {

    public ImageView iv;
    public TextView tv;
    public TextView head_tv;

    public GridViewHolder(View itemView) {
        super(itemView);
        iv = (ImageView) itemView.findViewById(R.id.imageView);
        tv = (TextView) itemView.findViewById(R.id.textView);
    }

}
