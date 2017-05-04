package jd.websocket;

import java.nio.charset.Charset;

import jd.websocket.WebSocketClient.OP_CODE;

import org.appwork.utils.formatter.HexFormatter;

public class WriteWebSocketFrame extends WebSocketFrame {

    protected final byte[] unmaskedPayload;

    public WriteWebSocketFrame(WebSocketFrameHeader frameHeader, byte[] payload) {
        super(frameHeader, payload);
        this.unmaskedPayload = payload;
    }

    public WriteWebSocketFrame(WebSocketFrameHeader frameHeader) {
        this(frameHeader, null);
    }

    public byte[] getUnMaskedPayload() {
        return this.unmaskedPayload;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Write|Fin:").append(this.isFin());
        sb.append("|OpCode:").append(this.getOpcode());
        if (this.getMask() != null) {
            sb.append("|Mask:").append(HexFormatter.byteArrayToHex(this.getMask()));
        }
        sb.append("|PayLoadLength:" + this.getPayloadLength());
        if (OP_CODE.UTF8_TEXT.equals(this.getOpcode()) && this.hasPayLoad()) {
            sb.append("|UTF8_TEXT:" + new String(this.getUnMaskedPayload(), Charset.forName("UTF-8")));
        }
        return sb.toString();
    }
}
