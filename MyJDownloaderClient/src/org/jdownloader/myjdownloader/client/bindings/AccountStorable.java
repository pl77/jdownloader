package org.jdownloader.myjdownloader.client.bindings;

import org.jdownloader.myjdownloader.client.json.AbstractJsonData;

public class AccountStorable extends AbstractJsonData {
    
    private Long    UUID = null;
    private boolean enabled;
    
    private boolean valid;
    
    private String  hostname;
    
    private String  username;
    private String  errorType;
    
    public String getErrorType() {
        return this.errorType;
    }
    
    public void setErrorType(final String errorType) {
        this.errorType = errorType;
    }
    
    public String getErrorString() {
        return this.errorString;
    }
    
    public void setErrorString(final String errorString) {
        this.errorString = errorString;
    }
    
    private String errorString;
    private Long   validUntil  = null;
    
    private Long   trafficLeft = null;
    private Long   trafficMax  = null;
    
    @SuppressWarnings("unused")
    protected AccountStorable(/* Storable */) {
    }
    
    public String getHostname() {
        return this.hostname;
    }
    
    public Long getTrafficLeft() {
        return this.trafficLeft;
    }
    
    public Long getTrafficMax() {
        return this.trafficMax;
    }
    
    public String getUsername() {
        return this.username;
    }
    
    public Long getUUID() {
        return this.UUID;
    }
    
    public Long getValidUntil() {
        return this.validUntil;
    }
    
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public boolean isValid() {
        return this.valid;
    }
    
    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setHostname(final String hostname) {
        this.hostname = hostname;
    }
    
    public void setTrafficLeft(final Long trafficLeft) {
        if (trafficLeft == null) {
            this.trafficLeft = null;
        } else {
            this.trafficLeft = Math.max(-1, trafficLeft);
        }
    }
    
    public void setTrafficMax(final Long trafficMax) {
        if (trafficMax == null) {
            this.trafficMax = null;
        } else {
            this.trafficMax = Math.max(-1, this.trafficLeft);
        }
    }
    
    public void setUsername(final String username) {
        this.username = username;
    }
    
    public void setUUID(final Long uUID) {
        this.UUID = uUID;
    }
    
    public void setValid(final boolean valid) {
        this.valid = valid;
    }
    
    public void setValidUntil(final Long validUntil) {
        this.validUntil = validUntil;
    }
    
}
