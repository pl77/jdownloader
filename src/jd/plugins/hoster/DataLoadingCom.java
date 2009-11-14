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

import java.io.IOException;

import jd.PluginWrapper;
import jd.http.URLConnectionAdapter;
import jd.parser.Regex;
import jd.parser.html.Form;
import jd.plugins.DownloadLink;
import jd.plugins.HostPlugin;
import jd.plugins.LinkStatus;
import jd.plugins.Plugin;
import jd.plugins.PluginException;
import jd.plugins.PluginForHost;
import jd.plugins.DownloadLink.AvailableStatus;

@HostPlugin(revision = "$Revision$", interfaceVersion = 2, names = { "data-loading.com" }, urls = { "http://[\\w\\.]*?data-loading\\.com/.{2}/file/\\d+/\\w+" }, flags = { 0 })
public class DataLoadingCom extends PluginForHost {

    public DataLoadingCom(PluginWrapper wrapper) {
        super(wrapper);
    }

    // @Overrid
    @Override
    public void correctDownloadLink(DownloadLink link) throws Exception {
        link.setUrlDownload(link.getDownloadURL().replaceAll("(/de/|/ru/|/es/)", "/en/"));
    }

    @Override
    public String getAGBLink() {
        return "http://www.data-loading.com/en/rules.html";
    }

    @Override
    public AvailableStatus requestFileInformation(DownloadLink downloadLink) throws IOException, PluginException {
        this.setBrowserExclusive();
        // Atm we don't need the cookie but maybe in the future so you can just
        // "activate" it here then
        // br.setCookie("http://data-loading.com", "yab_mylang", "en");
        br.setFollowRedirects(false);
        br.getPage(downloadLink.getDownloadURL());
        if (!(br.containsHTML("Your requested file is not found") || br.containsHTML("File Not Found"))) {
            String filename = br.getRegex("<td align=left width=150px id=filename>(.*?)</td>").getMatch(0);
            String filesize = br.getRegex("<td align=left id=filezize>(.*?)</td>").getMatch(0);
            if (!(filename == null || filesize == null)) {
                downloadLink.setName(filename);
                downloadLink.setDownloadSize(Regex.getSize(filesize.replaceAll(",", "\\.")));
                return AvailableStatus.TRUE;
            }
        }
        throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
    }

    @Override
    public void handleFree(DownloadLink downloadLink) throws Exception {
        requestFileInformation(downloadLink);
        String passCode = null;
        // Captcha required or not ?
        if (br.containsHTML("captcha.php")) {
            for (int i = 0; i <= 3; i++) {
                Form captchaform = br.getFormbyProperty("name", "myform");
                String captchaurl = "http://data-loading.com/captcha.php";
                if (captchaform == null || !br.containsHTML("captcha.php")) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
                if (br.containsHTML("downloadpw")) {
                    if (downloadLink.getStringProperty("pass", null) == null) {
                        passCode = Plugin.getUserInput("Password?", downloadLink);

                    } else {
                        /* gespeicherten PassCode holen */
                        passCode = downloadLink.getStringProperty("pass", null);
                    }
                    captchaform.put("downloadpw", passCode);
                }
                String code = getCaptchaCode(captchaurl, downloadLink);
                captchaform.put("captchacode", code);
                br.submitForm(captchaform);
                System.out.print(br.toString());
                if (br.containsHTML("Password Error")) {
                    logger.warning("Wrong password!");
                    downloadLink.setProperty("pass", null);
                    continue;
                }
                if (br.containsHTML("Captcha number error") || br.containsHTML("captcha.php") && !br.containsHTML("You have got max allowed bandwidth size per hour")) {
                    logger.warning("Wrong captcha or wrong password!");
                    downloadLink.setProperty("pass", null);
                    continue;
                }
                break;
            }
        } else {
            if (br.containsHTML("downloadpw")) {
                Form pwform = br.getFormbyProperty("name", "myform");
                if (pwform == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
                if (downloadLink.getStringProperty("pass", null) == null) {
                    passCode = Plugin.getUserInput("Password?", downloadLink);
                } else {
                    /* gespeicherten PassCode holen */
                    passCode = downloadLink.getStringProperty("pass", null);
                }
                pwform.put("downloadpw", passCode);
                br.submitForm(pwform);
            }
        }
        if (br.containsHTML("Password Error")) {
            logger.warning("Wrong password!");
            downloadLink.setProperty("pass", null);
            throw new PluginException(LinkStatus.ERROR_RETRY);
        }
        if (br.containsHTML("Captcha number error") || br.containsHTML("captcha.php") && !br.containsHTML("You have got max allowed bandwidth size per hour")) {
            logger.warning("Wrong captcha or wrong password!");
            downloadLink.setProperty("pass", null);
            throw new PluginException(LinkStatus.ERROR_CAPTCHA);
        }
        if (passCode != null) {
            downloadLink.setProperty("pass", passCode);
        }
        if (br.containsHTML("You have got max allowed bandwidth size per hour")) throw new PluginException(LinkStatus.ERROR_IP_BLOCKED);
        String linkurl = br.getRegex("<input.*document.location=\"(.*?)\";").getMatch(0);
        if (linkurl == null) throw new PluginException(LinkStatus.ERROR_PLUGIN_DEFECT);
        br.setFollowRedirects(false);
        dl = jd.plugins.BrowserAdapter.openDownload(br, downloadLink, linkurl, false, 1);
        if (!(dl.getConnection().isContentDisposition())) {
            dl.getConnection().disconnect();
            throw new PluginException(LinkStatus.ERROR_FILE_NOT_FOUND);
        }
        URLConnectionAdapter con = dl.getConnection();
        if (con.getResponseCode() != 200 && con.getResponseCode() != 206) {
            con.disconnect();
            throw new PluginException(LinkStatus.ERROR_TEMPORARILY_UNAVAILABLE, 30 * 1000l);
        }
        dl.startDownload();
    }

    @Override
    public int getMaxSimultanFreeDownloadNum() {
        return 20;
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
