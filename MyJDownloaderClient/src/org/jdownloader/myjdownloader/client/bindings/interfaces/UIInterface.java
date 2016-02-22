package org.jdownloader.myjdownloader.client.bindings.interfaces;

import org.jdownloader.myjdownloader.client.bindings.ClientApiNameSpace;
import org.jdownloader.myjdownloader.client.bindings.MenuStructure;
import org.jdownloader.myjdownloader.client.exceptions.device.InternalServerErrorException;

@ClientApiNameSpace("ui")
public interface UIInterface extends Linkable {
    public static enum Context {
        /** Linkgrabber rightlick */
        LGC,
        /** Downloadlist rightlick */
        DLC
    }
    
    public MenuStructure getMenu(Context context) throws InternalServerErrorException;
    
    public Object invokeAction(Context context, String id, long[] linkIds, long[] packageIds) throws InternalServerErrorException;
}
