package org.jdownloader.myjdownloader.client;

import org.jdownloader.myjdownloader.client.json.JSonRequest;
import org.jdownloader.myjdownloader.client.json.ObjectData;

public interface DiffHandler {

    public void prepare(JSonRequest payload, String deviceID, String action);

    public String handle(JSonRequest payload, ObjectData dataObject, String deviceID, String action, String diffString);
}
