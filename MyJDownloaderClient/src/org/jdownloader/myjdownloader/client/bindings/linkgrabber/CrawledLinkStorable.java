package org.jdownloader.myjdownloader.client.bindings.linkgrabber;

import org.jdownloader.myjdownloader.client.bindings.AbstractLinkStorable;
import org.jdownloader.myjdownloader.client.bindings.LinkVariantStorable;
import org.jdownloader.myjdownloader.client.json.AvailableLinkState;

public class CrawledLinkStorable extends AbstractLinkStorable {

    private AvailableLinkState  availability = null;

    private boolean             variants     = false;

    private LinkVariantStorable variant      = null;

    public LinkVariantStorable getVariant() {
        return variant;
    }

    public void setVariant(LinkVariantStorable variant) {
        this.variant = variant;
    }

    public CrawledLinkStorable(/* Storable */) {

    }

    public AvailableLinkState getAvailability() {
        return availability;
    }

    /**
     * @deprecated Use #getVariant instead
     * @return
     */
    @Deprecated
    public boolean isVariants() {
        return variants;
    }

    public void setAvailability(final AvailableLinkState availability) {
        this.availability = availability;
    }

    /**
     * @deprecated Use #setVariant instead
     * @return
     */
    @Deprecated
    public void setVariants(final boolean variants) {
        this.variants = variants;
    }

}
