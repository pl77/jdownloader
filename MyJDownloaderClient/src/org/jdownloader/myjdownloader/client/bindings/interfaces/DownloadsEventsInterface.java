package org.jdownloader.myjdownloader.client.bindings.interfaces;

import org.jdownloader.myjdownloader.client.bindings.ClientApiNameSpace;
import org.jdownloader.myjdownloader.client.bindings.downloadlist.DownloadLinkQuery;
import org.jdownloader.myjdownloader.client.json.DownloadListDiff;

@ClientApiNameSpace("downloadevents")
public interface DownloadsEventsInterface extends Linkable {
    public boolean setStatusEventInterval(long channelID, long interval);

    DownloadListDiff queryLinks(DownloadLinkQuery query, int diffID);

}
