//jDownloader - Downloadmanager
//Copyright (C) 2009  JD-Team support@jdownloader.org
//
//This program is free software: you can redistribute it and/or modify
//it under the terms of the GNU General Public License as published by
//the Free Software Foundation, either version 3 of the License, or
//(at your option) any later version.
//
//This program is distributed in the hope that it will be useful,
//but WITHOUT ANY WARRANTY; without even the implied warranty of
//MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//GNU General Public License for more details.
//
//You should have received a copy of the GNU General Public License
//along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jd.plugins.hoster;

import java.io.IOException;

import jd.PluginWrapper;
import jd.config.Property;
import jd.http.Browser;
import jd.http.Browser.BrowserException;
import jd.http.URLConnectionAdapter;
import jd.nutils.encoding.Encoding;
import jd.parser.Regex;
import jd.plugins.DownloadLink;
import jd.plugins.DownloadLink.AvailableStatus;
import jd.plugins.HostPlugin;
import jd.plugins.LinkStatus;
import jd.plugins.PluginException;

@HostPlugin(revision = "$Revision$", interfaceVersion = 2, names = { "sankakucomplex.com" }, urls = { "https?://(www\\.)?chan\\.sankakucomplex\\.com/post/show/\\d+" }, flags = { 0 })
public class SankakucomplexCom extends antiDDoSForHost {

    public SankakucomplexCom(PluginWrapper wrapper) {
        super(wrapper);
    }

    /* Extension which will be used if no correct extension is found */
    private static final String default_Extension = ".jpg";

    private String              DLLINK            = null;

    @Override
    public String getAGBLink() {
        return "http://www.sankakucomplex.com/";
    }

    public void correctDownloadLink(final DownloadLink link) {
        link.setUrlDownload(link.getDownloadURL().replace("http://", "https://"));
    }

    @Override
    public void init() {
        try {
            if (isNewJD()) {
                Browser.setRequestIntervalLimitGlobal(this.getHost(), 3000, 20, 60000);
            } else {
                // law of averages, client shouldn't be making a heap of requests every second...
                Browser.setRequestIntervalLimitGlobal(this.getHost(), 1500);
            }
        } catch (final Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public AvailableStatus requestFileInformation(final DownloadLink downloadLink) throws Exception {
        this.setBrowserExclusive();
        br.setFollowRedirects(true);
        br.setCookie("https://chan.sankakucomplex.com/", "locale", "en");
        br.setCookie("https://chan.sankakucomplex.com/", "hide-news-ticker", "1");
        br.setCookie("https://chan.sankakucomplex.com/", "auto_page", "1");
        br.setCookie("https://chan.sankakucomplex.com/", "hide_resized_notice", "1");
        br.setCookie("https://chan.sankakucomplex.com/", "blacklisted_tags", "");
        getPage(downloadLink.getDownloadURL());
        if (br.getHttpConnection().getResponseCode() == 404) {
            throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        }
        String filename = new Regex(downloadLink.getDownloadURL(), "(\\d+)$").getMatch(0);
        DLLINK = checkDirectLink(downloadLink, "directlink");
        if (DLLINK == null) {
            DLLINK = br.getRegex("<li>Original: <a href=\"(//[^<>\"]*?)\"").getMatch(0);
            if (DLLINK != null) {
                DLLINK = "https:" + DLLINK;
            }
        }
        if (filename == null || DLLINK == null) {
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        }
        DLLINK = Encoding.htmlDecode(DLLINK);
        filename = Encoding.htmlDecode(filename);
        filename = filename.trim();
        filename = encodeUnicode(filename);
        String ext = new Regex(DLLINK, "[a-z0-9]+(\\.[a-z]+)\\?\\d+$").getMatch(0);
        if (ext == null) {
            ext = DLLINK.substring(DLLINK.lastIndexOf("."));
        }
        /* Make sure that we get a correct extension */
        if (ext == null || !ext.matches("\\.[A-Za-z0-9]{3,5}")) {
            ext = default_Extension;
        }
        if (!filename.endsWith(ext)) {
            filename += ext;
        }
        downloadLink.setFinalFileName(filename);
        final Browser br2 = br.cloneBrowser();
        // In case the link redirects to the finallink
        br2.setFollowRedirects(true);
        URLConnectionAdapter con = null;
        try {
            try {
                con = openConnection(br2, DLLINK);
            } catch (final BrowserException e) {
                throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
            }
            if (!con.getContentType().contains("html")) {
                downloadLink.setDownloadSize(con.getLongContentLength());
            } else {
                throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
            }
            downloadLink.setProperty("directlink", DLLINK);
            return AvailableStatus.TRUE;
        } finally {
            try {
                con.disconnect();
            } catch (final Throwable e) {
            }
        }
    }

    private URLConnectionAdapter openConnection(final Browser br, final String directlink) throws IOException {
        URLConnectionAdapter con;
        if (isJDStable()) {
            con = br.openGetConnection(directlink);
        } else {
            con = br.openHeadConnection(directlink);
        }
        return con;
    }

    private boolean isJDStable() {
        return System.getProperty("jd.revision.jdownloaderrevision") == null;
    }

    @Override
    public void handleFree(final DownloadLink downloadLink) throws Exception {
        requestFileInformation(downloadLink);
        /* Disable chunks as we only download small files */
        dl = jd.plugins.BrowserAdapter.openDownload(br, downloadLink, DLLINK, true, 1);
        if (dl.getConnection().getContentType().contains("html")) {
            br.followConnection();
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        }
        dl.startDownload();
    }

    private String checkDirectLink(final DownloadLink downloadLink, final String property) {
        String dllink = downloadLink.getStringProperty(property);
        if (dllink != null) {
            try {
                final Browser br2 = br.cloneBrowser();
                URLConnectionAdapter con = openConnection(br2, dllink);
                if (con.getContentType().contains("html") || con.getLongContentLength() == -1) {
                    downloadLink.setProperty(property, Property.NULL);
                    dllink = null;
                }
                con.disconnect();
            } catch (final Exception e) {
                downloadLink.setProperty(property, Property.NULL);
                dllink = null;
            }
        }
        return dllink;
    }

    /* Avoid chars which are not allowed in filenames under certain OS' */
    private static String encodeUnicode(final String input) {
        String output = input;
        output = output.replace(":", ";");
        output = output.replace("|", "¦");
        output = output.replace("<", "[");
        output = output.replace(">", "]");
        output = output.replace("/", "⁄");
        output = output.replace("\\", "∖");
        output = output.replace("*", "#");
        output = output.replace("?", "¿");
        output = output.replace("!", "¡");
        output = output.replace("\"", "'");
        return output;
    }

    @Override
    public int getMaxSimultanFreeDownloadNum() {
        return -1;
    }

    @Override
    public void reset() {
    }

    @Override
    public void resetPluginGlobals() {
    }

    @Override
    public void resetDownloadlink(DownloadLink link) {
    }
}
