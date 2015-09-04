package org.jdownloader.myjdownloader.client.bindings.interfaces;

import java.util.HashMap;
import java.util.List;

import org.jdownloader.myjdownloader.client.bindings.ArchiveStatusStorable;
import org.jdownloader.myjdownloader.client.bindings.ClientApiNameSpace;

@ClientApiNameSpace(ExtractionInterface.NAMESPACE)
public interface ExtractionInterface extends Linkable {
    public static final String NAMESPACE = "extraction";

    public void addArchivePassword(String password);

    public HashMap<String, Boolean> startExtractionNow(final long[] linkIds, final long[] packageIds);

    public List<ArchiveStatusStorable> getArchiveInfo(final long[] linkIds, final long[] packageIds);
    
    public List<ArchiveStatusStorable> getQueue();

    public Boolean cancelExtraction(long archiveId);
}
