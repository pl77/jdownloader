package org.jdownloader.myjdownloader.client.bindings.linkgrabber;

import org.jdownloader.myjdownloader.client.bindings.AbstractLinkQuery;

public class CrawledLinkQuery extends AbstractLinkQuery {

    private boolean availability = false;

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(final boolean availability) {
        this.availability = availability;
    }

    /**
     * 03.09.14
     * 
     * @deprecated Use {@link #isVariantID()}
     * @return
     */
    @Deprecated
    public boolean isVariants() {
        return variants;
    }

    /**
     * 03.09.14
     * 
     * @deprecated Use {@link #isVariantID()}
     * @return
     */
    @Deprecated
    public void setVariants(final boolean variants) {
        this.variants = variants;
    }

    private boolean variantIcon = false;

    public boolean isVariantIcon() {
        return variantIcon;
    }

    public void setVariantIcon(boolean variantIcon) {
        this.variantIcon = variantIcon;
    }

    private boolean variantName = false;
    private boolean variantID   = false;

    public boolean isVariantID() {
        return variantID;
    }

    public void setVariantID(boolean variant) {
        this.variantID = variant;
    }

    public boolean isVariantName() {
        return variantName;
    }

    public void setVariantName(boolean variantName) {
        this.variantName = variantName;
    }

    /**
     * @deprecated Use {@link #isVariantID()}
     * @return
     */
    @Deprecated
    private boolean variants = false;

    private boolean priority = false;

    public boolean isPriority() {
        return priority;
    }

    public void setPriority(boolean priority) {
        this.priority = priority;
    }

}