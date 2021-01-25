package com.eticum.filter.ssl;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.bc.BcX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.littleshoot.proxy.mitm.Authority;
import org.littleshoot.proxy.mitm.CertificateHelper;
import org.littleshoot.proxy.mitm.SubjectAlternativeNameHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyManagementException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Random;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

public class CH {
    private static final Logger log = LoggerFactory.getLogger(CertificateHelper.class);

    private static final String KEYGEN_ALGORITHM = "RSA";

    private static final String SECURE_RANDOM_ALGORITHM = "SHA1PRNG";

    private static final String SIGNATURE_ALGORITHM = (is32BitJvm() ? "SHA256" : "SHA512") + "WithRSAEncryption";

    private static final int FAKE_KEYSIZE = 1024;

    private static final Date NOT_BEFORE = new Date(System.currentTimeMillis() - 86400000L * 365);

    private static final Date NOT_AFTER = new Date(
            System.currentTimeMillis() + 86400000L * 365 * 100);

    private static final String SSL_CONTEXT_PROTOCOL = "TLSv1.2";

    private static final String SSL_CONTEXT_FALLBACK_PROTOCOL = "TLSv1";

    private CH() {
    }

    public static KeyPair generateKeyPair(int keySize)
            throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator
                .getInstance(KEYGEN_ALGORITHM);
        SecureRandom secureRandom = SecureRandom
                .getInstance(SECURE_RANDOM_ALGORITHM);
        generator.initialize(keySize, secureRandom);
        return generator.generateKeyPair();
    }

    /**
     * Uses the non-portable system property sun.arch.data.model to help
     * determine if we are running on a 32-bit JVM. Since the majority of modern
     * systems are 64 bits, this method "assumes" 64 bits and only returns true
     * if sun.arch.data.model explicitly indicates a 32-bit JVM.
     *
     * @return true if we can determine definitively that this is a 32-bit JVM,
     * otherwise false
     */
    private static boolean is32BitJvm() {
        Integer bits = Integer.getInteger("sun.arch.data.model");
        return bits != null && bits == 32;
    }

    private static SubjectKeyIdentifier createSubjectKeyIdentifier(Key key)
            throws IOException {
        ByteArrayInputStream bIn = new ByteArrayInputStream(key.getEncoded());
        ASN1InputStream is = new ASN1InputStream(bIn);
        ASN1Sequence seq = (ASN1Sequence) is.readObject();
        SubjectPublicKeyInfo info = new SubjectPublicKeyInfo(seq);
        return new BcX509ExtensionUtils().createSubjectKeyIdentifier(info);
    }

    public static KeyStore createServerCertificate(String commonName,
                                                   SubjectAlternativeNameHolder subjectAlternativeNames,
                                                   Authority authority, Certificate caCert, PrivateKey caPrivKey)
            throws NoSuchAlgorithmException, NoSuchProviderException,
            IOException, OperatorCreationException, CertificateException,
            InvalidKeyException, SignatureException, KeyStoreException {

        KeyPair keyPair = generateKeyPair(FAKE_KEYSIZE);

        X500Name issuer = new X509CertificateHolder(caCert.getEncoded())
                .getSubject();
        BigInteger serial = BigInteger.valueOf(initRandomSerial());

        X500NameBuilder name = new X500NameBuilder(BCStyle.INSTANCE);
        name.addRDN(BCStyle.CN, commonName);
        name.addRDN(BCStyle.O, authority.certOrganisation());
        name.addRDN(BCStyle.OU, authority.certOrganizationalUnitName());
        X500Name subject = name.build();

        X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(
                issuer, serial, NOT_BEFORE, NOT_AFTER, subject,
                keyPair.getPublic());

        builder.addExtension(Extension.subjectKeyIdentifier, false,
                createSubjectKeyIdentifier(keyPair.getPublic()));
        builder.addExtension(Extension.basicConstraints, false,
                new BasicConstraints(false));

        subjectAlternativeNames.fillInto(builder);

        X509Certificate cert = signCertificate(builder, caPrivKey);

        cert.checkValidity(new Date());
        cert.verify(caCert.getPublicKey());

        KeyStore result = KeyStore.getInstance(KeyStore.getDefaultType());
        result.load(null, null);
        Certificate[] chain = {cert, caCert};
        result.setKeyEntry(authority.alias(), keyPair.getPrivate(),
                authority.password(), chain);

        return result;
    }

    private static X509Certificate signCertificate(
            X509v3CertificateBuilder certificateBuilder,
            PrivateKey signedWithPrivateKey) throws OperatorCreationException,
            CertificateException {
        ContentSigner signer = new JcaContentSignerBuilder(SIGNATURE_ALGORITHM).build(signedWithPrivateKey);
        return new JcaX509CertificateConverter().getCertificate(certificateBuilder.build(signer));
    }

    public static KeyManager[] getKeyManagers(KeyStore keyStore,
                                              Authority authority) throws NoSuchAlgorithmException, UnrecoverableKeyException,
            KeyStoreException {
        String keyManAlg = KeyManagerFactory.getDefaultAlgorithm();
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(keyManAlg);
        kmf.init(keyStore, authority.password());
        return kmf.getKeyManagers();
    }

    public static SSLContext newClientContext(KeyManager[] keyManagers,
                                              TrustManager[] trustManagers) throws NoSuchAlgorithmException,
            KeyManagementException {
        SSLContext result = newSSLContext();
        result.init(keyManagers, trustManagers, null);
        return result;
    }

    public static SSLContext newServerContext(KeyManager[] keyManagers)
            throws NoSuchAlgorithmException,
            KeyManagementException {
        SSLContext result = newSSLContext();
        SecureRandom random = new SecureRandom();
        random.setSeed(System.currentTimeMillis() + 1);
        result.init(keyManagers, null, random);
        return result;
    }

    private static SSLContext newSSLContext() throws NoSuchAlgorithmException {
        try {
            log.debug("Using protocol {}", SSL_CONTEXT_PROTOCOL);
            return SSLContext.getInstance(SSL_CONTEXT_PROTOCOL);
        } catch (NoSuchAlgorithmException e) {
            log.warn("Protocol {} not available, falling back to {}", SSL_CONTEXT_PROTOCOL,
                    SSL_CONTEXT_FALLBACK_PROTOCOL);
            return SSLContext.getInstance(SSL_CONTEXT_FALLBACK_PROTOCOL);
        }
    }

    public static long initRandomSerial() {
        final Random rnd = new Random();
        rnd.setSeed(System.currentTimeMillis());
        // prevent browser certificate caches, cause of doubled serial numbers
        // using 48bit random number
        long sl = ((long) rnd.nextInt()) << 32 | (rnd.nextInt() & 0xFFFFFFFFL);
        // let reserve of 16 bit for increasing, serials have to be positive
        sl = sl & 0x0000FFFFFFFFFFFFL;
        return sl;
    }
}
