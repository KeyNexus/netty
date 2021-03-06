/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */

package org.jboss.netty.handler.ssl;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A client-side {@link SslContext} which uses JDK's SSL/TLS implementation.
 */
public final class JdkSslClientContext extends JdkSslContext {

    private final SSLContext ctx;
    private final List<String> nextProtocols;

    /**
     * Creates a new instance.
     */
    public JdkSslClientContext() throws SSLException, FileNotFoundException {
        this(null, null, null, null, null, 0, 0);
    }

    /**
     * Creates a new instance.
     *
     * @param certChainFile an X.509 certificate chain file in PEM format.
     *                      {@code null} to use the system default
     */
    public JdkSslClientContext(File certChainFile) throws SSLException {
        this(certChainFile, null);
    }

    /**
     * Creates a new instance.
     *
     * @param trustManagerFactory the {@link TrustManagerFactory} that provides the {@link javax.net.ssl.TrustManager}s
     *                            that verifies the certificates sent from servers.
     *                            {@code null} to use the default.
     */
    public JdkSslClientContext(TrustManagerFactory trustManagerFactory) throws SSLException {
        this(null, trustManagerFactory);
    }

    /**
     * Creates a new instance.
     *
     * @param certChainFile an X.509 certificate chain file in PEM format.
     *                      {@code null} to use the system default
     * @param trustManagerFactory the {@link TrustManagerFactory} that provides the {@link javax.net.ssl.TrustManager}s
     *                            that verifies the certificates sent from servers.
     *                            {@code null} to use the default.
     */
    public JdkSslClientContext(File certChainFile, TrustManagerFactory trustManagerFactory)
            throws SSLException {
        this(null, certChainFile, trustManagerFactory, null, null, 0, 0);
    }

    /**
     * Creates a new instance.
     *
     * @param certChainFile an X.509 certificate chain file in PEM format.
     *                      {@code null} to use the system default
     * @param trustManagerFactory the {@link TrustManagerFactory} that provides the {@link javax.net.ssl.TrustManager}s
     *                            that verifies the certificates sent from servers.
     *                            {@code null} to use the default.
     */
    public JdkSslClientContext(InputStream certChainFile,
                               InputStream keyFile, TrustManagerFactory trustManagerFactory) throws SSLException {
            this(null, certChainFile, keyFile, null,
                null, trustManagerFactory,
                null, null,
                0, 0);
    }

    /**
     * Creates a new instance.
     *
     * @param bufPool the buffer pool which will be used by this context.
     *                {@code null} to use the default buffer pool.
     * @param certChainFile an X.509 certificate chain file in PEM format.
     *                      {@code null} to use the system default
     * @param trustManagerFactory the {@link TrustManagerFactory} that provides the {@link javax.net.ssl.TrustManager}s
     *                            that verifies the certificates sent from servers.
     *                            {@code null} to use the default.
     * @param ciphers the cipher suites to enable, in the order of preference.
     *                {@code null} to use the default cipher suites.
     * @param nextProtocols the application layer protocols to accept, in the order of preference.
     *                      {@code null} to disable TLS NPN/ALPN extension.
     * @param sessionCacheSize the size of the cache used for storing SSL session objects.
     *                         {@code 0} to use the default value.
     * @param sessionTimeout the timeout for the cached SSL session objects, in seconds.
     *                       {@code 0} to use the default value.
     */
    public JdkSslClientContext(
            SslBufferPool bufPool,
            File certChainFile, TrustManagerFactory trustManagerFactory,
            Iterable<String> ciphers, Iterable<String> nextProtocols,
            long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(bufPool,
                null, null, null,
                certChainFile, trustManagerFactory,
                ciphers, nextProtocols,
                sessionCacheSize, sessionTimeout);
    }

    /**
     * Creates a new instance.
     *
     * @param bufPool the buffer pool which will be used by this context.
     *                {@code null} to use the default buffer pool.
     * @param certChainFile Client SSL cert
     * @param keyFile Client SSL private key
     * @param keyPassword Client SSL private key password
     * @param serverCertChainFile an X.509 certificate chain file in PEM format.
     *                      {@code null} to use the system default
     * @param trustManagerFactory the {@link TrustManagerFactory} that provides the {@link javax.net.ssl.TrustManager}s
     *                            that verifies the certificates sent from servers.
     *                            {@code null} to use the default.
     * @param ciphers the cipher suites to enable, in the order of preference.
     *                {@code null} to use the default cipher suites.
     * @param nextProtocols the application layer protocols to accept, in the order of preference.
     *                      {@code null} to disable TLS NPN/ALPN extension.
     * @param sessionCacheSize the size of the cache used for storing SSL session objects.
     *                         {@code 0} to use the default value.
     * @param sessionTimeout the timeout for the cached SSL session objects, in seconds.
     *                       {@code 0} to use the default value.
     */
    public JdkSslClientContext(
        SslBufferPool bufPool,
        // Client cert
        File certChainFile, File keyFile, String keyPassword,
        // Server cert trust
        File serverCertChainFile, TrustManagerFactory trustManagerFactory,
        Iterable<String> ciphers, Iterable<String> nextProtocols,
        long sessionCacheSize, long sessionTimeout) throws SSLException {
        this(bufPool,
                getSslFile(certChainFile),
                getSslFile(keyFile),
                keyPassword,
                getSslFile(serverCertChainFile),
                trustManagerFactory, ciphers, nextProtocols, sessionCacheSize, sessionTimeout);
    }

    public JdkSslClientContext(
            SslBufferPool bufPool,
            // Client cert
            InputStream certChainFile, InputStream keyFile, String keyPassword,
            // Server cert trust
            InputStream serverCertChainFile, TrustManagerFactory trustManagerFactory,
            Iterable<String> ciphers, Iterable<String> nextProtocols,
            long sessionCacheSize, long sessionTimeout) throws SSLException {

        super(bufPool, ciphers);

        if (nextProtocols != null && nextProtocols.iterator().hasNext()) {
            if (!JettyNpnSslEngine.isAvailable()) {
                throw new SSLException("NPN/ALPN unsupported: " + nextProtocols);
            }

            List<String> nextProtoList = new ArrayList<String>();
            for (String p: nextProtocols) {
                if (p == null) {
                    break;
                }
                nextProtoList.add(p);
            }
            this.nextProtocols = Collections.unmodifiableList(nextProtoList);
        } else {
            this.nextProtocols = Collections.emptyList();
        }

        try {
            KeyManager[] keyManagers = null;
            if (certChainFile != null) {
                KeyManagerFactory kmf = buildKeyManager(certChainFile, keyFile, keyPassword);
                keyManagers = kmf.getKeyManagers();
            }

            if (serverCertChainFile != null) {
                trustManagerFactory = buildTrustManager(serverCertChainFile);
            }
            TrustManager[] trustManagers = null;
            if (trustManagerFactory != null) {
                trustManagers = trustManagerFactory.getTrustManagers();
            }

            // Initialize the SSLContext to work with the trust managers.
            ctx = SSLContext.getInstance(PROTOCOL);
            ctx.init(keyManagers, trustManagers, null);

            SSLSessionContext sessCtx = ctx.getClientSessionContext();
            if (sessionCacheSize > 0) {
                sessCtx.setSessionCacheSize((int) Math.min(sessionCacheSize, Integer.MAX_VALUE));
            }
            if (sessionTimeout > 0) {
                sessCtx.setSessionTimeout((int) Math.min(sessionTimeout, Integer.MAX_VALUE));
            }
        } catch (Exception e) {
            throw new SSLException("failed to initialize the client-side SSL context", e);
        }
    }

    @Override
    public boolean isClient() {
        return true;
    }

    @Override
    public List<String> nextProtocols() {
        return nextProtocols;
    }

    @Override
    public SSLContext context() {
        return ctx;
    }
}
