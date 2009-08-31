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

package jd.gui.swing.jdgui.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.UIManager;

import jd.gui.swing.GuiRunnable;
import jd.gui.swing.jdgui.MainTabbedPane;
import jd.gui.swing.jdgui.interfaces.JDMouseAdapter;
import jd.gui.swing.jdgui.interfaces.View;
import jd.utils.locale.JDL;

abstract public class ClosableView extends View {

    private static final long serialVersionUID = 8698758386841005256L;
    private JMenuBar menubar;
    private CloseAction closeAction;

    public ClosableView() {
        super();
    }

    /**
     * has to be called to init the close menu
     */
    public void init() {
        menubar = new JMenuBar();
        int count = menubar.getComponentCount();
        initMenu(menubar);
        closeAction = new CloseAction();
        if (menubar.getComponentCount() > count) {
            menubar.add(Box.createHorizontalGlue());

            final JButton bt = new JButton(closeAction);
            bt.setPreferredSize(new Dimension(closeAction.getWidth(), closeAction.getHeight()));
            bt.setContentAreaFilled(false);
            bt.setBorderPainted(false);
            bt.addMouseListener(new JDMouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    bt.setContentAreaFilled(true);
                    bt.setBorderPainted(true);
                }

                public void mouseExited(MouseEvent e) {
                    bt.setContentAreaFilled(false);
                    bt.setBorderPainted(false);
                }
            });
            bt.setText(null);
            bt.setToolTipText(JDL.LF("jd.gui.swing.jdgui.views.ClosableView.closebtn.tooltip", "Close %s", this.getTitle()));

            Box panel = new Box(1);
            panel.add(bt);
            panel.setOpaque(false);
            panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 1, 0));
            menubar.add(panel);

            add(menubar, "dock NORTH,height 16!,gapbottom 2");
        }
    }

    public CloseAction getCloseAction() {
        return closeAction;
    }

    /**
     * May be overridden to add some more menu Items
     * 
     * @param menubar
     */
    protected void initMenu(JMenuBar menubar) {
        // TODO Auto-generated method stub

    }

    /**
     * CLoses this view
     */
    public void close() {
        new GuiRunnable<Object>() {

            @Override
            public Object runSave() {
                closeAction.actionPerformed(null);
                return null;
            }

        }.start();

    }

    public class CloseAction extends AbstractAction {
        private static final long serialVersionUID = -771203720364300914L;
        private int height;
        private int width;

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public CloseAction() {
            Icon ic = UIManager.getIcon("InternalFrame.closeIcon");
            this.height = ic.getIconHeight();
            this.width = ic.getIconWidth();
            this.putValue(AbstractAction.SMALL_ICON, ic);
        }

        public void actionPerformed(ActionEvent e) {
            MainTabbedPane.getInstance().remove(ClosableView.this);
        }
    }
}
