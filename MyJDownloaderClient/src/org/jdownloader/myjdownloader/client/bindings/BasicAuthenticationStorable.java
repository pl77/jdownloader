package org.jdownloader.myjdownloader.client.bindings;

import java.util.Locale;

import org.jdownloader.myjdownloader.client.json.AbstractJsonData;

public class BasicAuthenticationStorable extends AbstractJsonData {
    public BasicAuthenticationStorable() {
        // empty const. required for Storable interface
    }

    public static enum Type {
        FTP,
        HTTP
    }

    private long created = System.currentTimeMillis();

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getLastValidated() {
        return lastValidated;
    }

    public void setLastValidated(long lastValidated) {
        this.lastValidated = lastValidated;
    }

    private long    lastValidated = -1;
    private boolean enabled       = true;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getHostmask() {
        return hostmask;
    }

    public void setHostmask(String hostmask) {
        if (hostmask == null) {
            this.hostmask = null;
        } else {
            this.hostmask = hostmask.toLowerCase(Locale.ENGLISH);
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    private String username;
    private Type   type = Type.HTTP;
    private String hostmask;
    private long   id;
}
