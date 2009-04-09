//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team support@jdownloader.org
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

package jd.router;

import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.logging.LogRecord;

import javax.swing.ScrollPaneConstants;

import jd.controlling.ProgressController;
import jd.event.ControlEvent;
import jd.event.ControlListener;
import jd.gui.skins.simple.config.GUIConfigEntry;
import jd.utils.JDLocale;
import jd.utils.JDUtilities;

public class FindRouterIP {
public static String findIP(GUIConfigEntry ip){
    final ProgressController progress = new ProgressController(JDLocale.L("gui.config.routeripfinder.featchIP", "Search for routers hostname..."), 100);
    
    ip.setData(JDLocale.L("gui.config.routeripfinder.featchIP", "Search for routers hostname..."));
    progress.setStatus(60);
    GetRouterInfo rinfo = new GetRouterInfo(null);
    progress.setStatus(80);
    InetAddress ia = rinfo.getAdress();
    if (ia != null) ip.setData(ia.getHostAddress());
    progress.setStatus(100);
    if (ia != null) {
        progress.setStatusText(JDLocale.LF("gui.config.routeripfinder.ready", "Hostname found: %s", ia.getHostAddress()));
        progress.finalize(3000);
        return ia.getHostAddress();

    } else {
        progress.setStatusText(JDLocale.L("gui.config.routeripfinder.notfound", "Can't find your routers hostname"));
        progress.finalize(3000);
        progress.setColor(Color.RED);
        
        return null;
    }
}
    public FindRouterIP(final GUIConfigEntry ip) {
       
        new Thread() {
            @Override
            public void run() {
                findIP(ip);
            }
        }.start();
    }



}