package com.eticum.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lombok.Getter;
import lombok.Setter;
import lombok.Synchronized;

public class NetworkUtils {
    public static class DNSResolver implements Runnable {
        private String mDomain;
        @Getter(onMethod_ = {@Synchronized})
        @Setter(onMethod_ = {@Synchronized})
        private InetAddress mAddress;

        public static boolean isDNSReachable(String domain, long timeoutMillis) {
            try {
                DNSResolver dnsRes = new DNSResolver(domain);

                Thread t = new Thread(dnsRes, "DNSResolver");
                t.start();
                t.join(timeoutMillis);
                return dnsRes.getMAddress() != null;
            } catch (Exception e) {
                return false;
            }
        }

        public DNSResolver(String domain) {
            this.mDomain = domain;
        }

        public void run() {
            try {
                InetAddress addr = InetAddress.getByName(mDomain);
                setMAddress(addr);
            } catch (UnknownHostException ignored) {
            }
        }
    }
}