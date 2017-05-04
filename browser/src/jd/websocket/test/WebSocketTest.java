package jd.websocket.test;

import java.io.IOException;
import java.net.URL;

import jd.http.Browser;
import jd.websocket.WebSocketClient;
import jd.websocket.WebSocketFrame;

import org.appwork.utils.Application;
import org.appwork.utils.logging2.extmanager.LoggerFactory;

public class WebSocketTest {

    public static void main(String[] args) throws IOException {
        Application.setApplication(".websocketTest");
        final Browser br = new Browser();
        br.setLogger(LoggerFactory.getDefaultLogger());
        br.setVerbose(true);
        br.setDebug(true);
        br.setFollowRedirects(true);
        WebSocketClient wsc = new WebSocketClient(br, new URL("http://echo.websocket.org/?encoding=text"));
        wsc.connect();

        wsc.writeFrame(wsc.buildPingFrame("test".getBytes()));
        WebSocketFrame frame = wsc.readNextFrame();
        System.out.println(frame);
        wsc.writeFrame(wsc.buildUTF8TextFrame("Test"));
        frame = wsc.readNextFrame();
        System.out.println(frame);
        wsc.close();
    }
}
