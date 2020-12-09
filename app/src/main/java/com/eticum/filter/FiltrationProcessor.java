package com.eticum.filter;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Locale;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FiltrationProcessor {
    private FilterInfoHolder filterInfoHolder = FilterInfoHolder.get();

    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    public static final class EticumHttpFilterSourceAdapter extends HttpFiltersSourceAdapter {

        private static final AttributeKey<String> CONNECTED_URL = AttributeKey.valueOf("connected_url");

        @Override
        public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
            String uri = originalRequest.getUri();
            if (originalRequest.getMethod() == HttpMethod.CONNECT) {
                String prefix = null;
                if (ctx != null) {
                    prefix = "https://" + uri.replaceFirst(":443$", "");
                    ctx.channel().attr(CONNECTED_URL).set(prefix);
                }
                return new HttpFiltersAdapter(originalRequest, ctx);
            }
            String connectedUrl = ctx.channel().attr(CONNECTED_URL).get();

            if (connectedUrl == null) {
                return new EticumHttpFilters(uri);
            }
            return new EticumHttpFilters(connectedUrl + uri);
        }


        private static final class EticumHttpFilters implements HttpFilters {
            private String uri;

            public EticumHttpFilters(String uri) {
                this.uri = uri;
            }

            @Override
            public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                log.debug("Request: {}", httpObject);
                ByteBuf buffer = Unpooled.wrappedBuffer("xnj".getBytes(StandardCharsets.UTF_8));
                HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
                HttpHeaders.setContentLength(response, buffer.readableBytes());
                HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_TYPE, "text/html");
                HttpHeaders.setHeader(response, HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
                return response;
            }

            @Override
            public HttpResponse proxyToServerRequest(HttpObject httpObject) {
                return null;
            }

            @Override
            public void proxyToServerRequestSending() {

            }

            @Override
            public void proxyToServerRequestSent() {

            }

            @Override
            public HttpObject serverToProxyResponse(HttpObject httpObject) {
                log.debug("ServerResponse: {}", httpObject);
                return httpObject;
            }

            @Override
            public void serverToProxyResponseTimedOut() {

            }

            @Override
            public void serverToProxyResponseReceiving() {

            }

            @Override
            public void serverToProxyResponseReceived() {

            }

            @Override
            public HttpObject proxyToClientResponse(HttpObject httpObject) {
                log.debug("Response: {}", httpObject);
                return httpObject;
            }

            @Override
            public void proxyToServerConnectionQueued() {

            }

            @Override
            public InetSocketAddress proxyToServerResolutionStarted(String resolvingServerHostAndPort) {
                return null;
            }

            @Override
            public void proxyToServerResolutionFailed(String hostAndPort) {

            }

            @Override
            public void proxyToServerResolutionSucceeded(String serverHostAndPort, InetSocketAddress resolvedRemoteAddress) {

            }

            @Override
            public void proxyToServerConnectionStarted() {

            }

            @Override
            public void proxyToServerConnectionSSLHandshakeStarted() {

            }

            @Override
            public void proxyToServerConnectionFailed() {

            }

            @Override
            public void proxyToServerConnectionSucceeded(ChannelHandlerContext serverCtx) {

            }
        }

    }
}
