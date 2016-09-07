package jd.http;

import java.net.URL;

import org.appwork.utils.net.httpconnection.HTTPProxy;

public class HTTPConnectionFactory {

    public static URLConnectionAdapter createHTTPConnection(final URL url, final HTTPProxy proxy) {
        if (proxy == null) {
            return new URLConnectionAdapterDirectImpl(url);
        } else {
            if (proxy.isPreferNativeImplementation()) {
                return new URLConnectionAdapterNative(url, proxy);
            } else {
                switch (proxy.getType()) {
                case DIRECT:
                    return new URLConnectionAdapterDirectImpl(url, proxy);
                case HTTP:
                    return new URLConnectionAdapterHTTPProxyImpl(url, proxy);
                case HTTPS:
                    return new URLConnectionAdapterHTTPProxyImpl(url, proxy);
                case NONE:
                    return new URLConnectionAdapterDirectImpl(url, proxy);
                case SOCKS4:
                    return new URLConnectionAdapterSocks4Impl(url, proxy);
                case SOCKS5:
                    return new URLConnectionAdapterSocks5Impl(url, proxy);
                default:
                    throw new RuntimeException("unsupported proxy type: " + proxy.getType().name());
                }
            }
        }
    }

}
