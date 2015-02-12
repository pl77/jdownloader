package jd.http;

import java.net.Socket;

import org.appwork.utils.net.httpconnection.HTTPProxy;
import org.appwork.utils.net.httpconnection.SocksHTTPconnection.DESTTYPE;
import org.appwork.utils.net.socketconnection.DirectSocketConnection;
import org.appwork.utils.net.socketconnection.HTTPProxySocketConnection;
import org.appwork.utils.net.socketconnection.Socks4SocketConnection;
import org.appwork.utils.net.socketconnection.Socks5SocketConnection;

public class SocketConnectionFactory {

    public static Socket createSocket(final HTTPProxy proxy) {
        if (proxy == null) {
            return new DirectSocketConnection();
        } else {
            if (proxy.isPreferNativeImplementation()) {
                /* TODO */
                return new Socket();
            } else {
                switch (proxy.getType()) {
                case DIRECT:
                    return new DirectSocketConnection(proxy);
                case HTTP:
                    return new HTTPProxySocketConnection(proxy);
                case NONE:
                    return new DirectSocketConnection();
                case SOCKS4:
                    return new Socks4SocketConnection(proxy, DESTTYPE.DOMAIN);
                case SOCKS5:
                    return new Socks5SocketConnection(proxy, DESTTYPE.DOMAIN);
                default:
                    throw new RuntimeException("unsupported proxy type: " + proxy.getType().name());
                }
            }
        }
    }
}
