package com.hxgz.chuantv.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zhoujianwu
 * @date 2021/01/30
 * @description：
 */
public class DNSUtil {
    public static boolean detectDNS(String mApiHost) {
        mApiHost = mApiHost.replaceAll("http://", "").replaceAll("https://", "");

        DNSResolver dnsRes = new DNSResolver(mApiHost);
        Thread t = new Thread(dnsRes);

        try {
            t.start();
            t.join(2000);
            InetAddress inetAddr = dnsRes.get();
            return inetAddr != null;
        } catch (Exception e) {
            return false;
        }
    }

    private static class DNSResolver implements Runnable {
        private final String domain;
        private InetAddress inetAddr;

        public DNSResolver(String domain) {
            this.domain = domain;
        }

        public void run() {
            try {
                InetAddress addr = InetAddress.getByName(domain);
                set(addr);
            } catch (UnknownHostException ignored) {
                LogUtil.e(ignored.getMessage());
            }
        }

        public synchronized void set(InetAddress inetAddr) {
            this.inetAddr = inetAddr;
        }

        public synchronized InetAddress get() {
            return inetAddr;
        }
    }
}

    