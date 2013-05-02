package org.jdownloader.gui.views.downloads.context.submenu;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;

import org.jdownloader.controlling.contextmenu.MenuItemData;
import org.jdownloader.controlling.contextmenu.MenuLink;
import org.jdownloader.gui.translate._GUI;
import org.jdownloader.gui.views.SelectionInfo;

public class AddonSubMenuLink extends MenuItemData implements MenuLink {
    public AddonSubMenuLink() {
        setName(_GUI._.AddonSubMenuLink_AddonSubMenuLink_());
        setIconKey("extension");
    }

    public JComponent addTo(JComponent root, SelectionInfo<?, ?> selection) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {

        // if (!showItem(selection)) return null;
        // if (selection.getContextPackage() instanceof FilePackage) {
        // int count = root.getComponentCount();
        // MenuFactoryEventSender.getInstance().fireEvent(new MenuFactoryEvent(MenuFactoryEvent.Type.EXTEND, new DownloadTableContext(root,
        // (SelectionInfo<FilePackage, DownloadLink>) selection, selection.getContextColumn())));
        // if (root.getComponentCount() > count) {
        // root.add(new JSeparator());
        // }
        //
        // return null;
        // } else {
        // throw new WTFException("TODO");
        // }
        return null;

    }
}
