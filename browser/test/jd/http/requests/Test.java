package jd.http.requests;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import jd.http.Browser;

public class Test {

    public static void main(String[] args) throws IOException {
        Browser br = new Browser();
        Logger logger = Logger.getLogger("test");
        ConsoleHandler ch = new ConsoleHandler();
        logger.addHandler(ch);
        logger.setLevel(Level.ALL);

        ch.setLevel(Level.ALL);
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
