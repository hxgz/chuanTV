package com.hxgz.chuantv.utils;

import androidx.annotation.RawRes;
import com.alibaba.fastjson.JSON;
import com.hxgz.chuantv.App;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

/**
 * @author zhoujianwu
 * @date 2020/10/30
 * @description：
 */
public class FileUtil {
    public static String getByRaw(@RawRes int res) {
        InputStream inputStream = App.getResource().openRawResource(res);
        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static <T> List<T> getList(@RawRes int res, Class<T> clazz) {
        return JSON.parseArray(getByRaw(res), clazz);
    }

    public static <T> T getObject(@RawRes int res, Class<T> clazz) {
        return JSON.parseObject(getByRaw(res), clazz);
    }
}

    