package jd.http.requests;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import jd.http.Browser;
import jd.http.Request;
import jd.http.URLConnectionAdapter;

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

        Request request = new HeadRequest("");
        URLConnectionAdapter con = br.openRequestConnection(request);
        con.disconnect();
    }
}
