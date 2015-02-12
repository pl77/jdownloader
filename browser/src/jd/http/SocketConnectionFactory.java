package jd.http;

import java.net.Socket;

import org.appwork.utils.net.httpconnection.HTTPProxy;
import org.appwork.utils.net.httpconnection.SocksHTTPconnection.DESTTYPE;
import org.appwork.utils.net.socketconnection.DirectSocket;
import org.appwork.utils.net.socketconnection.Socks4Socket;
import org.appwork.utils.net.socketconnection.Socks5Socket;

public class SocketConnectionFactory {

    public static Socket createSocket(final HTTPProxy proxy) {
        if (proxy == null) {
            return new DirectSocket();
        } else {
            if (proxy.isPreferNativeImplementation()) {
                /* TODO */
                return new Socket();
            } else {
                switch (proxy.getType()) {
                case DIRECT:
                    return new DirectSocket(proxy);
                case HTTP:
                    /* TODO */
                    return new Socket();
                case NONE:
                    return new DirectSocket();
                case SOCKS4:
                    return new Socks4Socket(proxy, DESTTYPE.DOMAIN);
                case SOCKS5:
                    return new Socks5Socket(proxy, DESTTYPE.DOMAIN);
                default:
                    throw new RuntimeException("unsupported proxy type: " + proxy.getType().name());
                }
            }
        }
    }
}
