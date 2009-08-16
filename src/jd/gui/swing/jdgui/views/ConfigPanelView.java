package jd.gui.swing.jdgui.views;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuBar;

import jd.gui.swing.jdgui.borders.JDBorderFactory;
import jd.gui.swing.jdgui.settings.ConfigPanel;

public class ConfigPanelView extends ClosableView {

    private static final long serialVersionUID = -4273043756814096939L;
    private ConfigPanel panel;
    private Icon icon;
    private String title;

    public ConfigPanelView(ConfigPanel premium, String title, Icon icon) {
        super();
        panel = premium;
        this.title = title;
        this.icon = icon;
        this.setContent(panel);
        this.init();
    }

    @Override
    protected void initMenu(JMenuBar menubar) {
        JLabel label;
        menubar.add(label = new JLabel(title));
        label.setIcon(icon);
        label.setIconTextGap(10);

        menubar.setBorder(JDBorderFactory.createInsideShadowBorder(0, 0, 1, 0));
    }

    @Override
    public Icon getIcon() {
        return icon;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getTooltip() {
        return null;
    }

    @Override
    protected void onHide() {
    }

    @Override
    protected void onShow() {
    }

}
