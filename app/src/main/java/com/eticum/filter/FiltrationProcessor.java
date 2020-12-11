package com.eticum.filter;

import com.eticum.api.EticumApiService;
import com.eticum.api.http.model.Info;
import com.eticum.utils.FilterUtils;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;

import java.net.URI;
import java.nio.charset.StandardCharsets;

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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import static com.eticum.filter.FilterStream.REASON_OK;

@Slf4j
public class FiltrationProcessor {
    public static final class EticumHttpFilterSourceAdapter extends HttpFiltersSourceAdapter {

        private static final AttributeKey<String> CONNECTED_URL = AttributeKey.valueOf("connected_url");

        @Override
        public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
            String uri = originalRequest.getUri();
            if (originalRequest.getMethod() == HttpMethod.CONNECT) {
                String prefix;
                if (ctx != null) {
                    prefix = "https://" + uri.replaceFirst(":443$", "");
                    ctx.channel().attr(CONNECTED_URL).set(prefix);
                }
                return new HttpFiltersAdapter(originalRequest, ctx);
            }
            String connectedUrl = ctx.channel().attr(CONNECTED_URL).get();

            if (connectedUrl == null) {
                return new EticumHttpFilters(originalRequest, ctx, uri);
            }
            return new EticumHttpFilters(originalRequest, ctx, connectedUrl + uri);
        }

        private static final class EticumHttpFilters extends HttpFiltersAdapter {
            private final String uri;

            public EticumHttpFilters(HttpRequest originalRequest,
                                     ChannelHandlerContext ctx, String uri) {
                super(originalRequest, ctx);
                this.uri = uri;
            }

            @SneakyThrows
            @Override
            public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                log.debug("Request: {}", httpObject);
                Info info = EticumApiService.doGetURLInfo(new URI(uri));
                FilterInfoHolder holder = FilterInfoHolder.get();

                int accessCode = FilterStream.of(holder.getProfile())
                        .checkAge(info.getAge())
                        .checkCategories(info.getCategories())
                        .checkUrlAccess(uri)
                        .getAccess();

                return FiltrationProcessor.generateResponse(accessCode, uri);
            }
        }

    }

    private static HttpResponse generateResponse(int accessCode, String uri) {
        if (accessCode == REASON_OK) return null;
        else {
            ByteBuf buffer = Unpooled.wrappedBuffer(
                    FilterUtils.generateRestrctedHtml(accessCode, uri).getBytes(StandardCharsets.UTF_8));
            HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN, buffer);
            HttpHeaders.setContentLength(response, buffer.readableBytes());
            HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_TYPE, "text/html");
            HttpHeaders.setHeader(response, HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
            return response;
        }
    }
}
