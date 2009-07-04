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

package jd.gui.skins.simple.config.panels;

import jd.config.Configuration;
import jd.config.ConfigEntry.PropertyType;
import jd.gui.skins.simple.config.ConfigPanel;
import jd.gui.skins.simple.config.subpanels.SubPanelPluginsOptional;
import net.miginfocom.swing.MigLayout;

/**
 * @author JD-Team
 * 
 */
public class ConfigPanelAddons extends ConfigPanel {

    private static final long serialVersionUID = 4145243293360008779L;

    private Configuration configuration;

    private SubPanelPluginsOptional sppo;

    // private SubPanelOptionalInstaller spr;

    // private JTabbedPane tabbed;

    public ConfigPanelAddons(Configuration configuration) {
        super();
        this.configuration = configuration;
        initPanel();
        load();
    }

    @Override
    public boolean needsViewport() {
        return false;
    }

    @Override
    public void initPanel() {
        panel.setLayout(new MigLayout("ins 0", "[fill,grow]", "[fill,grow]"));
        panel.add(sppo = new SubPanelPluginsOptional(configuration), "spanx");
        // tabbed.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        // tabbed.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        // tabbed.setTabPlacement(SwingConstants.TOP);
        // tabbed.addChangeListener(new ChangeListener() {
        // public void stateChanged(ChangeEvent e) {}
        // });
        // tabbed.addTab(JDL.L("gui.config.addons.settings.tab", "Settings"),
        // JDTheme.II("gui.splash.controller", 16, 16), sppo = new
        // SubPanelPluginsOptional(configuration));
        // tabbed.addTab(JDL.L("gui.config.addons.install.tab",
        // "Installation & updates"),
        // JDTheme.II("gui.images.taskpanes.download", 16, 16), spr = new
        // SubPanelOptionalInstaller(configuration));
        setLayout(new MigLayout("ins 0", "[fill,grow]", "[fill,grow]"));
        add(panel);
    }

    @Override
    public void load() {
        loadConfigEntries();
    }

    @Override
    public void save() {
        sppo.save();
        // spr.save();
    }

    @Override
    public PropertyType hasChanges() {
        return PropertyType.getMax(super.hasChanges(), sppo.hasChanges());
    }
}