package org.jdownloader.myjdownloader.client.bindings.interfaces;

import org.jdownloader.myjdownloader.client.bindings.ClientApiNameSpace;

@ClientApiNameSpace(SystemInterface.NAMESPACE)
public interface SystemInterface extends Linkable {
    public static final String NAMESPACE = "system";
    
    public void shutdownOS(boolean force);

    public void standbyOS();

    public void hibernateOS();

    public void restartJD();

    public void exitJD();

}
