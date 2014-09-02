package org.jdownloader.myjdownloader.client.bindings.interfaces;

import org.jdownloader.myjdownloader.client.bindings.ClientApiNameSpace;

@ClientApiNameSpace("downloadevents")
public interface DownloadsEventsInterface extends Linkable {
    public boolean setStatusEventInterval(long channelID, long interval);

}
