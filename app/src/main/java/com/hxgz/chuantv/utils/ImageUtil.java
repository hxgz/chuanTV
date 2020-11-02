package com.hxgz.chuantv.utils;

import android.view.View;
import android.widget.ImageView;
import com.hxgz.chuantv.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import org.apache.commons.lang3.StringUtils;

/**
 * @author zhoujianwu
 * @date 2020/10/23
 * @description：
 */
public class ImageUtil {
    public static void displayImage(ImageView view, String picUrl, int width, int height) {
        LogUtil.e(picUrl + view.toString());

        if (!StringUtils.isEmpty(picUrl) && view != null && height > 0 && width > 0) {
            if (height > width)
                Picasso.with(view.getContext()).load(picUrl).centerCrop().resize(width, height).into(view);
            else
                Picasso.with(view.getContext()).load(picUrl).centerCrop().resize(width, height).into(view);
        }
    }

    public static void displayImage(ImageView view, String picUrl) {
        if (!StringUtils.isEmpty(picUrl) && view != null)
            Picasso.with(view.getContext()).load(picUrl).into(view);
    }

    public static <T extends View & Target> void displayImage(T view, String picUrl) {
        Picasso.with(view.getContext()).load(picUrl).into(view);
    }
}

    