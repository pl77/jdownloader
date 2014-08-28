package org.jdownloader.myjdownloader.client.json;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Thomas
 * 
 */
public class IconDescriptor extends AbstractJsonData {
    /**
     * 
     */
    public IconDescriptor(/* Storable */) {

    }

    private HashMap<String, Object> prps;

    public HashMap<String, Object> getPrps() {
        return prps;
    }

    public void setPrps(HashMap<String, Object> prps) {
        this.prps = prps;
    }

    private String cls;
    private String key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getCls() {
        return cls;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public ArrayList<IconDescriptor> getRsc() {
        return rsc;
    }

    public void setRsc(ArrayList<IconDescriptor> rsc) {
        this.rsc = rsc;
    }

    private ArrayList<IconDescriptor> rsc;

    /**
 * 
 */
    public IconDescriptor(String cls) {
        this.cls = cls;
    }

    /**
     * @param string
     * @param tld
     */
    public IconDescriptor(String cls, String tld) {
        this(cls);
        this.key = tld;

    }

    /**
     * @param iconResource
     */
    public void add(IconDescriptor iconResource) {
        if (rsc == null) {
            rsc = new ArrayList<IconDescriptor>();
        }
        rsc.add(iconResource);
    }
}
