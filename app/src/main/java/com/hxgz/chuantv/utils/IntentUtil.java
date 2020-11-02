package com.hxgz.chuantv.utils;

import android.content.Intent;
import android.os.Bundle;

import java.io.Serializable;

/**
 * @author zhoujianwu
 * @date 2020/10/18
 * @description：
 */
public class IntentUtil {

    public static <T extends Serializable> void putData(Intent intent, String key, T o) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(key, o);
        intent.putExtras(bundle);
    }

    public static Serializable getData(Intent intent, String key) {
        Bundle extras = intent.getExtras();
        if (null != extras) {
            return extras.getSerializable(key);
        }
        return null;
    }
}

    