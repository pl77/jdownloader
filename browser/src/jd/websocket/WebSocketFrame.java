package jd.websocket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import jd.websocket.WebSocketClient.OP_CODE;

import org.appwork.utils.IO;
import org.appwork.utils.formatter.HexFormatter;
import org.appwork.utils.net.LimitedInputStream;

public class WebSocketFrame {
    
    private final WebSocketFrameHeader frameHeader;
    
    public boolean isFin() {
        return this.frameHeader.isFin();
    }
    
    public final OP_CODE getOpcode() {
        return this.frameHeader.getOpcode();
    }
    
    public final long getPayloadLength() {
        return this.frameHeader.getPayloadLength();
    }
    
    public final byte[] getMask() {
        return this.frameHeader.getMask();
    }
    
    public final byte[] getPayload() {
        return this.payload;
    }
    
    public final byte[] getHeader() {
        return this.getFrameHeader().getBytes();
    }
    
    public WebSocketFrameHeader getFrameHeader() {
        return this.frameHeader;
    }
    
    public boolean hasPayLoad() {
        return this.frameHeader.hasPayLoad();
    }
    
    private final byte[] payload;
    
    public WebSocketFrame(WebSocketFrameHeader frameHeader) {
        this(frameHeader, null);
    }
    
    public static WebSocketFrame read(InputStream is) throws IOException {
        final WebSocketFrameHeader frameHeader = WebSocketFrameHeader.read(is);
        if (frameHeader.getPayloadLength() > 0) {
            final byte[] payLoad = IO.readStream(-1, new LimitedInputStream(is, frameHeader.getPayloadLength()) {
                @Override
                public void close() throws IOException {
                }
            });
            return new WebSocketFrame(frameHeader, payLoad);
        } else {
            return new WebSocketFrame(frameHeader);
        }
    }
    
    public WebSocketFrame(WebSocketFrameHeader frameHeader, byte[] payload) {
        this.frameHeader = frameHeader;
        if (frameHeader.getMask() != null && payload != null && payload.length > 0) {
            final byte[] mask = frameHeader.getMask();
            this.payload = new byte[payload.length];
            System.arraycopy(payload, 0, this.payload, 0, payload.length);
            for (int index = 0; index < payload.length; index++) {
                this.payload[index] ^= mask[index % 4];
            }
        } else {
            this.payload = payload;
        }
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Fin:").append(this.isFin());
        sb.append("|OpCode:").append(this.getOpcode());
        if (OP_CODE.UTF8_TEXT.equals(this.getOpcode()) && this.hasPayLoad()) {
            sb.append("UTF8_TEXT:" + new String(this.getPayload(), Charset.forName("UTF-8")));
        }
        if (this.getMask() != null) {
            sb.append("|Mask:").append(HexFormatter.byteArrayToHex(this.getMask()));
        }
        sb.append("|PayLoadLength:" + this.getPayloadLength());
        
        return sb.toString();
    }
}