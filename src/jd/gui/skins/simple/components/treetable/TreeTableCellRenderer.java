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

package jd.gui.skins.simple.components.treetable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JTree;

import jd.plugins.DownloadLink;
import jd.plugins.FilePackage;
import jd.update.PackageData;
import jd.utils.JDLocale;
import jd.utils.JDTheme;
import jd.utils.JDUtilities;

import org.jdesktop.swingx.painter.Painter;
import org.jdesktop.swingx.renderer.DefaultTreeRenderer;
import org.jdesktop.swingx.renderer.JRendererLabel;
import org.jdesktop.swingx.renderer.PainterAware;

public class TreeTableCellRenderer extends DefaultTreeRenderer  {

    private static final long serialVersionUID = 1L;

    private Color FONT_COLOR;

    private Color FONT_COLOR_SELECTED;

    private JRendererLabel lbl_fp_closed;

    private JRendererLabel lbl_fp_opened;

    private JRendererLabel lbl_link;

    private Painter painter;

    public TreeTableCellRenderer() {
        super();

        FONT_COLOR = JDTheme.C("gui.color.downloadlist.font", "ff0000");
        FONT_COLOR_SELECTED = JDTheme.C("gui.color.downloadlist.font_selected", "ffffff");
        lbl_link = new JRendererLabel();
        lbl_link.setIcon(new ImageIcon(JDUtilities.getImage(JDTheme.V("gui.images.link"))));

        // , SwingConstants.LEFT);
        lbl_fp_closed = new JRendererLabel();
        lbl_fp_closed.setIcon(new ImageIcon(JDUtilities.getImage(JDTheme.V("gui.images.package_closed"))));
        lbl_fp_opened = new JRendererLabel();
        lbl_fp_opened.setIcon(new ImageIcon(JDUtilities.getImage(JDTheme.V("gui.images.package_opened"))));

        lbl_link.setOpaque(false);
        lbl_fp_closed.setOpaque(false);
        lbl_fp_opened.setOpaque(false);
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

        if (value instanceof DownloadLink) {
            DownloadLink dl = (DownloadLink) value;

            if (dl.getLinkType() == DownloadLink.LINKTYPE_JDU) {
                PackageData pd = (PackageData) dl.getProperty("JDU");
                lbl_link.setText(JDLocale.L("gui.treetable.part.label_update", "Update") + " " + pd.getInstalledVersion() + " -> " + pd.getStringProperty("version"));
            } else {
                int id = dl.getPartByName();
                lbl_link.setText(JDLocale.L("gui.treetable.part.label", "Datei") + " " + (id < 0 ? "" : JDUtilities.fillInteger(id, 3, "0")));
            }
//            if (selected) {
//                lbl_link.setForeground(FONT_COLOR_SELECTED);
//                lbl_link.setBackground(FONT_COLOR_SELECTED);
//            } else {
//                lbl_link.setForeground(FONT_COLOR);
//                lbl_link.setBackground(FONT_COLOR);
//            }
            return lbl_link;
        } else if (value instanceof FilePackage) {
            FilePackage fp = (FilePackage) value;

            if (expanded) {

                lbl_fp_opened.setText(fp.getName());
//                if (selected) {
//                    lbl_fp_opened.setForeground(FONT_COLOR_SELECTED);
//                    lbl_fp_opened.setBackground(FONT_COLOR_SELECTED);
//                } else {
//                    lbl_fp_opened.setForeground(FONT_COLOR);
//                    lbl_fp_opened.setBackground(FONT_COLOR);
//                }
                return lbl_fp_opened;
            } else {

                lbl_fp_closed.setText(fp.getName());
//                if (selected) {
//                    lbl_fp_closed.setForeground(FONT_COLOR_SELECTED);
//                    lbl_fp_closed.setBackground(FONT_COLOR_SELECTED);
//                } else {
//                    lbl_fp_closed.setForeground(FONT_COLOR);
//                    lbl_fp_closed.setBackground(FONT_COLOR);
//                }
                return lbl_fp_closed;
            }
        }
//        if (selected) {
//            lbl_link.setForeground(FONT_COLOR_SELECTED);
//            lbl_link.setBackground(FONT_COLOR_SELECTED);
//        } else {
//            lbl_link.setForeground(FONT_COLOR);
//            lbl_link.setBackground(FONT_COLOR);
//        }
//        lbl_link.setPreferredSize(new Dimension(300, 16));
        return lbl_link;
    }

//    public Painter getPainter() {
//        // TODO Auto-generated method stub
//        return painter;
//    }
//
//    public void setPainter(Painter painter) {
//        this.painter = painter;
//        lbl_fp_closed.setPainter(painter);
//        lbl_fp_opened.setPainter(painter);
//        lbl_link.setPainter(painter);
//
//    }
}