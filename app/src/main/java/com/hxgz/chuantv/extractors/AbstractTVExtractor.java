package com.hxgz.chuantv.extractors;

import android.annotation.SuppressLint;
import com.hxgz.chuantv.exception.BizException;
import com.hxgz.chuantv.utils.LogUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;


/**
 * 抽象类
 */
public abstract class AbstractTVExtractor implements TVExtractor {

    private static final Map<String, String> cookies = new HashMap<>();
    private static final int DEFAULT_TIMEOUT = 8000;

    abstract String getHost();

    protected Document downloadToDocument(String path) {
        return downloadToDocument(path, DEFAULT_TIMEOUT);
    }

    protected Document downloadToDocument(String path, int timeoutMs) {
        String body = downloadToRaw(path, timeoutMs);
        return Jsoup.parse(body);
    }

    protected String downloadToRaw(String path) {
        return downloadToRaw(path, DEFAULT_TIMEOUT);
    }

    @SuppressLint("DefaultLocale")
    protected String downloadToRaw(String path, int timeoutMs) {
        try {
//            if (!DNSUtil.detectDNS(QIQI_HOST)) {
//                throw new BizException("DNS解析失败，重启或者联系管理员");
//            }

            String url = path;
            if (!url.startsWith("https://") && !url.startsWith("http://")) {
                url = getHost() + path;
            }

            long startTime = System.currentTimeMillis(); //起始时间

            Connection.Response response = Jsoup.connect(url)
                    .cookies(cookies)
                    .ignoreContentType(true)
//                    .header("Accept-Encoding","gzip")
                    .timeout(timeoutMs)
                    .execute();

            processResponse(response);

            String body = response.body();

            long endTime = System.currentTimeMillis(); //结束时间
            LogUtil.d(String.format("%s cost: %dms", url, endTime - startTime));
            return body;
        } catch (Exception e) {
            LogUtil.e(e.getMessage());
            throw BizException.of(e);
        }
    }

    private void processResponse(Connection.Response response) {
        boolean qiqiHost = response.url().getHost().equals(getHost().replaceAll("http[s]://", ""));
        if (!qiqiHost) return;

        cookies.putAll(response.cookies());
    }
}

    