//    jDownloader - Downloadmanager
//    Copyright (C) 2009  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.

package jd.plugins.hoster;

import jd.PluginWrapper;
import jd.plugins.DownloadLink;
import jd.plugins.DownloadLink.AvailableStatus;
import jd.plugins.HostPlugin;
import jd.plugins.LinkStatus;
import jd.plugins.PluginException;
import jd.plugins.PluginForHost;

import org.appwork.utils.formatter.SizeFormatter;

@HostPlugin(revision = "$Revision$", interfaceVersion = 2, names = { "sharex.xpg.com.br" }, urls = { "http://(www\\.)?sharex\\.xpg(?:\\.uol)?\\.com\\.br/files/[0-9]+" })
public class ShareXXpgComBr extends PluginForHost {

    public ShareXXpgComBr(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    public String getAGBLink() {
        return "http://sharex.xpg.com.br/contact.php";
    }

    private String dllink = null;

    /**
     * Important: brazil proxy is needed, else you are redirected to http://www3.xpg.uol.com.br/404.html
     */
    @SuppressWarnings("deprecation")
    @Override
    public AvailableStatus requestFileInformation(final DownloadLink downloadLink) throws Exception {
        dllink = null;
        this.setBrowserExclusive();
        br.setReadTimeout(3 * 60 * 1000);
        br.setFollowRedirects(true);
        br.getPage(downloadLink.getDownloadURL());
        if (br.getURL().contains("/?NOT_FOUND")) {
            throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        }
        dllink = br.getRegex("\"(http://sharex\\.xpg(?:\\.uol)?\\.com\\.br/download/[0-9]+/.*?)\"").getMatch(0);
        String filename = br.getRegex("<div class=\"downinfo\">([^<>\"]*?)<").getMatch(0);
        if (filename == null) {
            filename = br.getRegex("/download/.*?/(.*?)\"").getMatch(0);
        }
        String filesize = br.getRegex(">([0-9]+ bytes)<").getMatch(0);
        if (filename == null) {
            throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        } else if (dllink == null && this.br.containsHTML("/download//")) {
            throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        }
        downloadLink.setName(filename.trim());
        if (filesize != null) {
            downloadLink.setDownloadSize(SizeFormatter.getSize(filesize));
        }
        return AvailableStatus.TRUE;
    }

    @Override
    public void handleFree(DownloadLink link) throws Exception {
        this.setBrowserExclusive();
        requestFileInformation(link);
        if (dllink == null) {
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        }
        dllink = dllink.replaceAll("\\{", "%7B");
        dllink = dllink.replaceAll("\\[", "%5B");
        dllink = dllink.replaceAll("\\]", "%5D");
        dllink = dllink.replaceAll("\\}", "%7D");

        int wait = 15;
        final String waittime = br.getRegex("id=\"tempo_espera\">(\\d+)</span>").getMatch(0);
        if (waittime != null) {
            wait = Integer.parseInt(waittime);
        }
        sleep(wait * 1001l, link);
        dl = jd.plugins.BrowserAdapter.openDownload(br, link, dllink, true, 0);
        if ((dl.getConnection().getContentType().contains("html"))) {
            if (dl.getConnection().getResponseCode() == 403) {
                throw new PluginException(LinkStatus.ERROR_TEMPORARILY_UNAVAILABLE, "Server error 403", 60 * 60 * 1000l);
            } else if (dl.getConnection().getResponseCode() == 404) {
                throw new PluginException(LinkStatus.ERROR_TEMPORARILY_UNAVAILABLE, "Server error 404", 60 * 60 * 1000l);
            } else if (dl.getConnection().getResponseCode() == 503) {
                throw new PluginException(LinkStatus.ERROR_TEMPORARILY_UNAVAILABLE, "Server error 503", 60 * 60 * 1000l);
            }
            br.followConnection();
            if (br.containsHTML(">404 Not Found<")) {
                throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
            }
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        }
        dl.startDownload();
    }

    @Override
    public int getMaxSimultanFreeDownloadNum() {
        return -1;
    }

    @Override
    public void reset() {
    }

    @Override
    public void resetDownloadlink(DownloadLink link) {
    }

}