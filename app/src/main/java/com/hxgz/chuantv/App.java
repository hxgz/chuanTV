/*
 * Copyright (C) 2016 hejunlin <hejunlin2013@gmail.com>
 * Github:https://github.com/hejunlin2013/TVSample
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hxgz.chuantv;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import java.io.File;

import com.hxgz.chuantv.extractors.Demo;
import com.hxgz.chuantv.extractors.Qiqi;
import com.hxgz.chuantv.extractors.TVExtractor;
import okhttp3.Cache;
import okhttp3.OkHttpClient;

public class App extends Application {

    private static Context mContext;
    private static OkHttpClient mHttpClient;
    private static final long SIZE_OF_HTTP_CACHE = 10 * 1024 * 1024;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        mHttpClient = new OkHttpClient();
        initHttpClient(mHttpClient, mContext);
    }

    private void initHttpClient(OkHttpClient client, Context context) {
        File httpCacheDirectory = context.getCacheDir();
        Cache httpResponseCache = new Cache(httpCacheDirectory, SIZE_OF_HTTP_CACHE);
//        try {
//            httpResponseCache =
//        } catch (IOException e) {
//            Log.e("Retrofit", "Could not create http cache", e);
//        }
//        client.setCache(httpResponseCache);
    }

    public static Resources getResource() {
        return mContext.getResources();
    }

    public static Context getContext() {
        return mContext;
    }

    public static OkHttpClient getHttpClient() {
        return mHttpClient;
    }

    public static boolean checkPermission(Context paramContext, String paramString) {
        return paramContext.checkCallingOrSelfPermission(paramString) == PackageManager.PERMISSION_GRANTED;
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static TVExtractor getTVForSearch() {
        return new Demo();
    }
}
