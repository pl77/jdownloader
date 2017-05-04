package jd.websocket;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import jd.http.Browser;

public class WebSocketClient {
    // https://tools.ietf.org/html/rfc6455
    // http://www.websocket.org/echo.html
    // https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_WebSocket_servers

    public static enum OP_CODE {
        CONTINUATION(0x0),
        UTF8_TEXT(0x1),
        BINARY(0x2),
        CLOSE(0x8),
        PING(0x9),
        PONG(0xA);

        private final int opCode;

        final int getOpCode() {
            return this.opCode;
        }

        private OP_CODE(int opCode) {
            this.opCode = opCode;
        }

        static OP_CODE get(int opCode) {
            for (final OP_CODE value : OP_CODE.values()) {
                if (value.getOpCode() == opCode) {
                    return value;
                }
            }
            return null;
        }
    }

    protected final URL           url;
    protected final Browser       br;
    protected WebSocketRequest    webSocketRequest = null;
    protected final AtomicBoolean closed           = new AtomicBoolean(false);

    public WebSocketClient(final Browser br, final URL url) {
        this.url = url;
        this.br = br;
        br.setAllowedResponseCodes(new int[] { 101 });
    }

    public URL getURL() {
        return this.url;
    }

    protected WebSocketRequest buildWebSocketRequest(final URL url) throws IOException {
        return new WebSocketRequest(url);
    }

    public void connect() throws IOException {
        if (this.webSocketRequest != null) {
            throw new IOException("already connected!");
        } else {
            this.webSocketRequest = this.buildWebSocketRequest(this.getURL());
            this.br.openRequestConnection(this.webSocketRequest);
        }
    }

    protected static byte[] fill(final InputStream is, final byte[] buffer) throws IOException {
        final int length = buffer.length;
        int done = 0;
        int read = 0;
        while (done < length && (read = is.read(buffer, done, length - done)) != -1) {
            done += read;
        }
        if (done != length) {
            throw new EOFException(done + "!=" + length);
        }
        return buffer;
    }

    protected byte[] nextMask() {
        final byte[] ret = new byte[4];
        new Random().nextBytes(ret);
        return ret;
    }

    /**
     * //https://tools.ietf.org/html/rfc6455#section-5.5.1
     *
     * @return
     */
    public WebSocketFrame buildCloseFrame() {
        return new WebSocketFrame(new WebSocketFrameHeader(true, OP_CODE.CLOSE, 0, this.nextMask()));
    }

    /**
     * https://tools.ietf.org/html/rfc6455#section-5.5.2
     */
    public WebSocketFrame buildPingFrame() {
        return this.buildPingFrame(null);
    }

    /**
     * https://tools.ietf.org/html/rfc6455#section-5.5.2
     */
    public WebSocketFrame buildPingFrame(byte[] payLoad) {
        if (payLoad != null && payLoad.length > 0) {
            if (payLoad.length > 125) {
                throw new IllegalArgumentException("Payload length must be <=125!");
            }
            return new WebSocketFrame(new WebSocketFrameHeader(true, OP_CODE.PING, payLoad.length, this.nextMask()), payLoad);
        } else {
            return new WebSocketFrame(new WebSocketFrameHeader(true, OP_CODE.PING, 0, null));
        }
    }

    /**
     * https://tools.ietf.org/html/rfc6455#section-5.6
     *
     * @param text
     * @return
     */
    public WebSocketFrame buildUTF8TextFrame(final String text) {
        final byte[] bytes = text.getBytes(Charset.forName("UTF-8"));
        return new WebSocketFrame(new WebSocketFrameHeader(true, OP_CODE.UTF8_TEXT, bytes.length, this.nextMask()), bytes);
    }

    public synchronized void writeFrame(WebSocketFrame webSocketFrame) throws IOException {
        final OutputStream os = this.getOutputStream();
        os.write(webSocketFrame.getHeader());
        if (webSocketFrame.hasPayLoad()) {
            os.write(webSocketFrame.getPayload());
        }
    }

    /**
     * https://tools.ietf.org/html/rfc6455#section-5.5.1
     *
     * @param ping
     * @return
     */
    protected void onOpCode_Close(WebSocketFrame close) throws IOException {
        this.disconnect();
    }

    protected void onOpCode_Pong(WebSocketFrame pong) throws IOException {
    }

    /**
     * https://tools.ietf.org/html/rfc6455#section-5.5.3
     *
     * @param ping
     * @return
     */
    public WebSocketFrame buildPongFrame(WebSocketFrame ping) {
        if (OP_CODE.PING.equals(ping.getOpcode())) {
            if (ping.hasPayLoad()) {
                return new WebSocketFrame(new WebSocketFrameHeader(true, OP_CODE.PONG, ping.getPayloadLength(), ping.getMask()), ping.getPayload());
            } else {
                return new WebSocketFrame(new WebSocketFrameHeader(true, OP_CODE.PONG, 0), null);
            }
        } else {
            throw new IllegalArgumentException("Parameter must be valid PING!");
        }
    }

    /**
     * https://tools.ietf.org/html/rfc6455#section-5.5.2
     *
     * @param ping
     * @return
     */
    protected void onOpCode_Ping(WebSocketFrame ping) throws IOException {
        this.writeFrame(this.buildPongFrame(ping));
    }

    public synchronized WebSocketFrame readNextFrame() throws IOException {
        final WebSocketFrame webSocketFrame = WebSocketFrame.read(this.getInputStream());
        switch (webSocketFrame.getOpcode()) {
        case PING:
            this.onOpCode_Ping(webSocketFrame);
            return webSocketFrame;
        case PONG:
            this.onOpCode_Pong(webSocketFrame);
            return webSocketFrame;
        case CLOSE:
            this.onOpCode_Close(webSocketFrame);
            return webSocketFrame;
        default:
            return webSocketFrame;
        }
    }

    protected InputStream getInputStream() throws IOException {
        this.checkWebSocket();
        return this.webSocketRequest.getHttpConnection().getInputStream();
    }

    protected void checkWebSocket() throws IOException {
        if (this.webSocketRequest == null) {
            throw new IOException("Websocket is unconnected!");
        } else if (this.closed.get()) {
            throw new IOException("Websocket is closed!");
        }
    }

    protected OutputStream getOutputStream() throws IOException {
        this.checkWebSocket();
        return this.webSocketRequest.getHttpConnection().getOutputStream();
    }

    public void close() throws IOException {
        try {
            this.writeFrame(this.buildCloseFrame());
        } finally {
            this.disconnect();
        }
    }

    public void disconnect() throws IOException {
        try {
            final WebSocketRequest webSocketRequest = this.webSocketRequest;
            if (webSocketRequest != null) {
                webSocketRequest.disconnect();
            }
        } finally {
            this.closed.set(true);
        }
    }
}
