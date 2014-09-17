package org.jdownloader.myjdownloader.client.json;

public class ObjectData extends AbstractJsonData implements RequestIDValidator {
    private long   rid      = -1;

    private Object data;
    private String diffID   = null;
    private String diffType = null;

    public String getDiffType() {
        return diffType;
    }

    public void setDiffType(String diffType) {
        this.diffType = diffType;
    }

    public String getDiffID() {
        return diffID;
    }

    public void setDiffID(String diffID) {
        this.diffID = diffID;
    }

    public ObjectData(/* keep empty constructor json */) {

    }

    public ObjectData(Object data, long rid) {
        this.data = data;
        this.rid = rid;
    }

    public Object getData() {
        return this.data;
    }

    public long getRid() {
        return this.rid;
    }

    public void setData(final Object data) {
        this.data = data;
    }

    public void setRid(final long rid) {
        this.rid = rid;
    }
}
