package com.eticum.filter.ssl;

import com.eticum.utils.SecurityUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import org.bouncycastle.operator.OperatorCreationException;
import org.jetbrains.annotations.NotNull;
import org.littleshoot.proxy.SslEngineSource;
import org.littleshoot.proxy.mitm.Authority;
import org.littleshoot.proxy.mitm.MergeTrustManager;
import org.littleshoot.proxy.mitm.SubjectAlternativeNameHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManager;

import io.netty.handler.ssl.util.InsecureTrustManagerFactory;

public class SSLES implements SslEngineSource {

    private static final Logger LOG = LoggerFactory.getLogger(org.littleshoot.proxy.mitm.BouncyCastleSslEngineSource.class);

    private final Authority authority;

    private final boolean trustAllServers;
    private final boolean sendCerts;

    private SSLContext sslContext;

    private Certificate eticumCert;

    private PrivateKey eticumCertKey;

    private final Cache<String, SSLContext> serverSSLContexts;

    public SSLES(Authority authority,
                 boolean trustAllServers, boolean sendCerts,
                 Cache<String, SSLContext> sslContexts)
            throws GeneralSecurityException, IOException {
        this.authority = authority;
        this.trustAllServers = trustAllServers;
        this.sendCerts = sendCerts;
        this.serverSSLContexts = sslContexts;
        initializeSSLContext();
    }

    public SSLES(Authority authority,
                 boolean trustAllServers, boolean sendCerts)
            throws GeneralSecurityException,
            IOException {
        this(authority, trustAllServers, sendCerts,
                initDefaultCertificateCache());
    }

    private static Cache<String, SSLContext> initDefaultCertificateCache() {
        return CacheBuilder.newBuilder() //
                .expireAfterAccess(5, TimeUnit.MINUTES) //
                .concurrencyLevel(1) //
                .build();
    }

    private void filterWeakCipherSuites(SSLEngine sslEngine) {
        List<String> ciphers = new LinkedList<>();
        for (String each : sslEngine.getEnabledCipherSuites()) {
            if (each.equals("TLS_DHE_RSA_WITH_AES_128_CBC_SHA") || each.equals("TLS_DHE_RSA_WITH_AES_256_CBC_SHA")) {
                LOG.debug("Removed cipher {}", each);
            } else {
                ciphers.add(each);
            }
        }
        sslEngine.setEnabledCipherSuites(ciphers.toArray(new String[0]));
        if (LOG.isDebugEnabled()) {
            if (sslEngine.getUseClientMode()) {
                LOG.debug("Enabled server cipher suites:");
            } else {
                String host = sslEngine.getPeerHost();
                int port = sslEngine.getPeerPort();
                LOG.debug("Enabled client {}:{} cipher suites:", host, port);
            }
            for (String each : ciphers) {
                LOG.debug(each);
            }
        }
    }

    public SSLEngine newSslEngine() {
        SSLEngine sslEngine = sslContext.createSSLEngine();
        filterWeakCipherSuites(sslEngine);
        return sslEngine;
    }

    @Override
    public SSLEngine newSslEngine(String remoteHost, int remotePort) {
        SSLEngine sslEngine = sslContext
                .createSSLEngine(remoteHost, remotePort);
        sslEngine.setUseClientMode(true);
        if (tryHostNameVerificationJava7(sslEngine)) {
            LOG.debug("Host Name Verification is not supported, causes insecure HTTPS connection");
        }
        filterWeakCipherSuites(sslEngine);
        return sslEngine;
    }

    private boolean tryHostNameVerificationJava7(SSLEngine sslEngine) {
        for (Method method : SSLParameters.class.getMethods()) {
            // method is available since Java 7
            if ("setEndpointIdentificationAlgorithm".equals(method.getName())) {
                SSLParameters sslParams = new SSLParameters();
                try {
                    method.invoke(sslParams, "HTTPS");
                } catch (IllegalAccessException | InvocationTargetException e) {
                    LOG.debug(
                            "SSLParameters#setEndpointIdentificationAlgorithm",
                            e);
                    return true;
                }
                sslEngine.setSSLParameters(sslParams);
                return false;
            }
        }
        return true;
    }

    private void initializeSSLContext() throws GeneralSecurityException,
            IOException {
        KeyStore ks = SecurityUtils.loadKeyStore(authority);
        eticumCert = ks.getCertificate(authority.alias());
        eticumCertKey = (PrivateKey) ks.getKey(authority.alias(),
                authority.password());

        TrustManager[] trustManagers;
        if (trustAllServers) {
            trustManagers = InsecureTrustManagerFactory.INSTANCE
                    .getTrustManagers();
        } else {
            trustManagers = new TrustManager[]{new MergeTrustManager(ks)};
        }

        KeyManager[] keyManagers;
        if (sendCerts) {
            keyManagers = CH.getKeyManagers(ks, authority);
        } else {
            keyManagers = new KeyManager[0];
        }

        sslContext = CH.newClientContext(keyManagers,
                trustManagers);
        SSLEngine sslEngine = sslContext.createSSLEngine();
        if (tryHostNameVerificationJava7(sslEngine)) {
            LOG.warn("Host Name Verification is not supported, causes insecure HTTPS connection to upstream servers.");
        }
    }

    public SSLEngine createCertForHost(final String commonName,
                                       final SubjectAlternativeNameHolder subjectAlternativeNames)
            throws GeneralSecurityException, OperatorCreationException,
            IOException, ExecutionException {
        if (commonName == null) {
            throw new IllegalArgumentException(
                    "Error, 'commonName' is not allowed to be null!");
        }
        if (subjectAlternativeNames == null) {
            throw new IllegalArgumentException(
                    "Error, 'subjectAlternativeNames' is not allowed to be null!");
        }

        SSLContext ctx;
        if (serverSSLContexts == null) {
            ctx = createServerContext(commonName, subjectAlternativeNames);
        } else {
            ctx = serverSSLContexts.get(commonName, () -> createServerContext(commonName,
                    subjectAlternativeNames));
        }
        return ctx.createSSLEngine();
    }

    private SSLContext createServerContext(String commonName,
                                           SubjectAlternativeNameHolder subjectAlternativeNames)
            throws GeneralSecurityException, IOException,
            OperatorCreationException {

        MillisecondsDuration duration = new MillisecondsDuration();
        KeyStore ks = CH.createServerCertificate(commonName,
                subjectAlternativeNames, authority, eticumCert, eticumCertKey);
        KeyManager[] keyManagers = CH.getKeyManagers(ks,
                authority);

        SSLContext result = CH.newServerContext(keyManagers);

        LOG.info("Impersonated {} in {}ms", commonName, duration);
        return result;
    }
}

class MillisecondsDuration {
    private final long mStartTime = System.currentTimeMillis();

    @NotNull
    @Override
    public String toString() {
        return String.valueOf(System.currentTimeMillis() - mStartTime);
    }
}

