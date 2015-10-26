package jd.http.requests;

import java.io.IOException;

import jd.http.Browser;

import org.appwork.utils.Application;
import org.appwork.utils.logging2.LogInterface;
import org.appwork.utils.logging2.extmanager.LoggerFactory;

public class Test {

    public static void main(String[] args) throws IOException {
        Application.ensureFrameWorkInit();
        Browser br = new Browser();
        LogInterface logger = LoggerFactory.get("test");

        br.setLogger(logger);
        br.setVerbose(true);
        br.setDebug(true);
        br.setFollowRedirects(true);
        // br.setProxy(HTTPProxy.parseHTTPProxy("socks5://127.0.0.1:1080"));
        // br.getHeaders().put("Referer", "http://facebook.com/pages/");
        System.out.println(br.getPage("http://ipcheck0.jdownloader.org"));
        // prevent referrer (see directhttp/recaptcha)
        // br.setCurrentURL(null);
        // br.getPage("http://api.recaptcha.net/challenge?k=" + "6Lcu6f4SAAAAABuG2JGXfAszg3j5uYZFHwIRAr6u");
        // as you can see referrer sent!
    }
}
