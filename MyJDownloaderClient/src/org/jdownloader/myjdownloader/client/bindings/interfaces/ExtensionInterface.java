package org.jdownloader.myjdownloader.client.bindings.interfaces;

import java.util.List;

import org.jdownloader.myjdownloader.client.bindings.ClientApiNameSpace;
import org.jdownloader.myjdownloader.client.bindings.ExtensionQuery;
import org.jdownloader.myjdownloader.client.bindings.ExtensionStorable;

@ClientApiNameSpace("extensions")
public interface ExtensionInterface extends Linkable {
    public boolean isInstalled(String id);

    public boolean isEnabled(String classname);

    public void setEnabled(String classname, boolean b);

    public void install(String id);

    List<ExtensionStorable> list(ExtensionQuery query);
}
