package org.jdownloader.myjdownloader.client.bindings;

public class ExtensionQuery {
    private boolean installed       = false;
    private boolean enabled         = false;
    private boolean name            = false;
    private boolean iconKey         = false;
    private boolean description     = false;
    private boolean configInterface = false;
    private String  pattern         = null;

    public boolean isIconKey() {
        return iconKey;
    }

    public void setIconKey(boolean iconKey) {
        this.iconKey = iconKey;
    }

    public boolean isName() {
        return name;
    }

    public void setName(boolean name) {
        this.name = name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isInstalled() {
        return installed;
    }

    public void setInstalled(boolean installed) {
        this.installed = installed;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean isDescription() {
        return description;
    }

    public void setDescription(boolean description) {
        this.description = description;
    }

    public boolean isConfigInterface() {
        return configInterface;
    }

    public void setConfigInterface(boolean configInterface) {
        this.configInterface = configInterface;
    }
}
