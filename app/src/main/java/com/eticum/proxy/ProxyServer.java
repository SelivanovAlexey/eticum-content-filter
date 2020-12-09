package com.eticum.proxy;

import com.eticum.Constants;
import com.eticum.filter.FiltrationProcessor;

import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

public class ProxyServer {
    private static HttpProxyServer server;

    public static void start() {
        server = DefaultHttpProxyServer.bootstrap()
                .withPort(Constants.LOCAL_PROXY_PORT)
//                    .withManInTheMiddle(new CSMM(SecurityUtils.authority()))
                .withFiltersSource(new FiltrationProcessor.EticumHttpFilterSourceAdapter())
                .start();
    }

    public static void stop() {
        server.abort();
    }
}
