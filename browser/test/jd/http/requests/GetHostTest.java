package jd.http.requests;

import jd.http.Browser;

public class GetHostTest {

    public static void main(String[] args) {
        String domains[] = new String[] { "127.0.0.1", "test.org", "test.test.org", "bla.com" };
        String appends[] = new String[] { "", "directhttp://" };
        String adds[] = new String[] { "", "/", ":80", ":80/" };
        for (String append : appends) {
            for (String add : adds) {
                for (String domain : domains) {
                    System.out.println(Browser.getHost(append + domain + add, true));
                    System.out.println(Browser.getHost(append + domain + add, false));
                }
                for (String domain : domains) {
                    System.out.println(Browser.getHost(append + "http://" + domain + add, true));
                    System.out.println(Browser.getHost(append + "http://" + domain + add, false));
                }
                for (String domain : domains) {
                    System.out.println(Browser.getHost(append + "http://test@" + domain + add, true));
                    System.out.println(Browser.getHost(append + "http://test@" + domain + add, false));
                }
                for (String domain : domains) {
                    System.out.println(Browser.getHost(append + "http://test:super@" + domain + add, true));
                    System.out.println(Browser.getHost(append + "http://test:super@" + domain + add, false));
                }
            }
        }
    }

}
