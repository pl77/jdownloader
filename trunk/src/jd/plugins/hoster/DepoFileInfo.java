//jDownloader - Downloadmanager
//Copyright (C) 2010  JD-Team support@jdownloader.org
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

import java.io.File;
import java.io.IOException;

import jd.PluginWrapper;
import jd.nutils.encoding.Encoding;
import jd.parser.Regex;
import jd.plugins.DownloadLink;
import jd.plugins.DownloadLink.AvailableStatus;
import jd.plugins.HostPlugin;
import jd.plugins.LinkStatus;
import jd.plugins.PluginException;
import jd.plugins.PluginForHost;
import jd.utils.JDUtilities;

import org.appwork.utils.formatter.SizeFormatter;

@HostPlugin(revision = "$Revision$", interfaceVersion = 2, names = { "depofile.info" }, urls = { "http://(www\\.)?depofile\\.info/[a-z0-9]+" }, flags = { 2 })
public class DepoFileInfo extends PluginForHost {

    public DepoFileInfo(PluginWrapper wrapper) {
        super(wrapper);
        this.enablePremium("http://www.depofile.info/register.php");
    }

    // DTemplate Version 0.1.9-psp
    // mods: heavily modified, do NOT upgrade!
    // non account: 1 * 20
    // premium account: chunks * maxdls
    // protocol: no https
    // captchatype: reCaptcha

    @Override
    public String getAGBLink() {
        return MAINPAGE + "/terms." + TYPE;
    }

    private final String        MAINPAGE                 = "http://depofile.info";
    private final String        TYPE                     = "php";
    private final boolean       RESUME                   = true;
    private final int           MAXCHUNKS                = 1;
    private static final String SIMULTANDLSLIMIT         = "e=You+have+reached+the+maximum+concurrent+downloads";
    private static final String SIMULTANDLSLIMIT_2       = "e=You+must+wait+";
    private static final String SIMULTANDLSLIMITUSERTEXT = "Max. simultan downloads limit reached, wait to start more downloads from this host";
    private static final String SERVERERROR              = "e=Error%3A+Could+not+open+file+for+reading.";
    private static final String SERVERERRORUSERTEXT      = "Server error";

    public void correctDownloadLink(DownloadLink link) {
        link.setUrlDownload(link.getDownloadURL().replace(this.getHost() + "decrypted", this.getHost()));
    }

    @Override
    public AvailableStatus requestFileInformation(final DownloadLink link) throws IOException, PluginException {
        this.setBrowserExclusive();
        br.setFollowRedirects(true);
        br.getPage(link.getDownloadURL());
        if (br.getURL().contains(SIMULTANDLSLIMIT) || br.getURL().contains(SIMULTANDLSLIMIT_2)) {
            link.setName(new Regex(link.getDownloadURL(), "([A-Za-z0-9]+)$").getMatch(0));
            link.getLinkStatus().setStatusText(SIMULTANDLSLIMITUSERTEXT);
            return AvailableStatus.TRUE;
        } else if (br.getURL().contains(SERVERERROR)) {
            link.setName(new Regex(link.getDownloadURL(), "([A-Za-z0-9]+)$").getMatch(0));
            link.getLinkStatus().setStatusText(SERVERERRORUSERTEXT);
            return AvailableStatus.TRUE;
        }
        if (br.getURL().contains("/error." + TYPE) || br.getURL().contains("/index." + TYPE) || br.containsHTML("No htmlCode read")) {
            throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        }
        final Regex fInfo = br.getRegex("<th class=\"descr\"([^<>]*?)?>[\t\n\r ]+<strong>([^<>\"]*?) \\((\\d+(,\\d+)?(\\.\\d+)? (KB|MB|GB))\\)<br/>");
        final String filename = fInfo.getMatch(1);
        final String filesize = fInfo.getMatch(2);
        if (filename == null || filesize == null) {
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        }
        link.setName(Encoding.htmlDecode(filename.trim()));
        link.setDownloadSize(SizeFormatter.getSize(filesize.replace(",", "")));
        return AvailableStatus.TRUE;
    }

    @Override
    public void handleFree(final DownloadLink downloadLink) throws Exception, PluginException {
        boolean captcha = false;
        requestFileInformation(downloadLink);
        if (br.getURL().contains(SIMULTANDLSLIMIT) || br.getURL().contains(SIMULTANDLSLIMIT_2)) {
            throw new PluginException(LinkStatus.ERROR_HOSTER_TEMPORARILY_UNAVAILABLE, SIMULTANDLSLIMITUSERTEXT, 1 * 60 * 1000l);
        } else if (br.getURL().contains(SERVERERROR)) {
            throw new PluginException(LinkStatus.ERROR_TEMPORARILY_UNAVAILABLE, SERVERERRORUSERTEXT, 5 * 60 * 1000l);
        }
        String free_link = br.getRegex("\\'(https?://depofile\\.info/[^<>\"]+\\?pt=[^<>\"]*?)\\'").getMatch(0);
        if (free_link == null) {
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        }
        final String waittime = br.getRegex("\\$\\(\\'\\.download\\-timer\\-seconds\\'\\)\\.html\\((\\d+)\\);").getMatch(0);
        if (waittime != null) {
            sleep(Integer.parseInt(waittime) * 1001l, downloadLink);
        }
        dl = jd.plugins.BrowserAdapter.openDownload(br, downloadLink, free_link, RESUME, MAXCHUNKS);
        if (!dl.getConnection().isContentDisposition()) {
            br.followConnection();
            if (br.getURL().contains(SERVERERROR)) {
                throw new PluginException(LinkStatus.ERROR_TEMPORARILY_UNAVAILABLE, SERVERERRORUSERTEXT, 5 * 60 * 1000l);
            }
            final String captchaAction = br.getRegex("<div class=\"captchaPageTable\">[\t\n\r ]+<form method=\"POST\" action=\"(http://[^<>\"]*?)\"").getMatch(0);
            final String rcID = br.getRegex("recaptcha/api/noscript\\?k=([^<>\"]*?)\"").getMatch(0);
            if (rcID == null || captchaAction == null) {
                logger.warning("reCaptcha_ID or captchaAction NULL - plugin seems to be broken!");
                throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
            }
            final PluginForHost recplug = JDUtilities.getPluginForHost("DirectHTTP");
            final jd.plugins.hoster.DirectHTTP.Recaptcha rc = ((DirectHTTP) recplug).getReCaptcha(br);
            rc.setId(rcID);
            rc.load();
            for (int i = 0; i <= 5; i++) {
                File cf = rc.downloadCaptcha(getLocalCaptchaFile());
                String c = getCaptchaCode(cf, downloadLink);
                dl = jd.plugins.BrowserAdapter.openDownload(br, downloadLink, captchaAction, "submit=continue&submitted=1&d=1&recaptcha_challenge_field=" + rc.getChallenge() + "&recaptcha_response_field=" + c, RESUME, MAXCHUNKS);
                if (!dl.getConnection().isContentDisposition()) {
                    br.followConnection();
                    rc.reload();
                    continue;
                }
                break;
            }
            captcha = true;
        }
        if (!dl.getConnection().isContentDisposition()) {
            br.followConnection();
            if (captcha && br.containsHTML("(api\\.recaptcha\\.net|google\\.com/recaptcha/api/)")) {
                throw new PluginException(LinkStatus.ERROR_CAPTCHA);
            }
            throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        }
        dl.startDownload();
    }

    @Override
    public void reset() {
    }

    @Override
    public int getMaxSimultanFreeDownloadNum() {
        return 1;
    }

    @Override
    public void resetDownloadlink(DownloadLink link) {
    }

}