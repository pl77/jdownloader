/**
 * 
 * ====================================================================================================================================================
 *         "My JDownloader Client" License
 *         The "My JDownloader Client" will be called [The Product] from now on.
 * ====================================================================================================================================================
 *         Copyright (c) 2009-2015, AppWork GmbH <e-mail@appwork.org>
 *         Schwabacher Straße 117
 *         90763 Fürth
 *         Germany   
 * === Preamble ===
 *     This license establishes the terms under which the [The Product] Source Code & Binary files may be used, copied, modified, distributed, and/or redistributed.
 *     The intent is that the AppWork GmbH is able to provide their utilities library for free to non-commercial projects whereas commercial usage is only permitted after obtaining a commercial license.
 *     These terms apply to all files that have the [The Product] License header (IN the file), a <filename>.license or <filename>.info (like mylib.jar.info) file that contains a reference to this license.
 * 	
 * === 3rd Party Licences ===
 *     Some parts of the [The Product] use or reference 3rd party libraries and classes. These parts may have different licensing conditions. Please check the *.license and *.info files of included libraries
 *     to ensure that they are compatible to your use-case. Further more, some *.java have their own license. In this case, they have their license terms in the java file header. 	
 * 	
 * === Definition: Commercial Usage ===
 *     If anybody or any organization is generating income (directly or indirectly) by using [The Product] or if there's any commercial interest or aspect in what you are doing, we consider this as a commercial usage.
 *     If your use-case is neither strictly private nor strictly educational, it is commercial. If you are unsure whether your use-case is commercial or not, consider it as commercial or contact us.
 * === Dual Licensing ===
 * === Commercial Usage ===
 *     If you want to use [The Product] in a commercial way (see definition above), you have to obtain a paid license from AppWork GmbH.
 *     Contact AppWork for further details: <e-mail@appwork.org>
 * === Non-Commercial Usage ===
 *     If there is no commercial usage (see definition above), you may use [The Product] under the terms of the 
 *     "GNU Affero General Public License" (http://www.gnu.org/licenses/agpl-3.0.en.html).
 * 	
 *     If the AGPL does not fit your needs, please contact us. We'll find a solution.
 * ====================================================================================================================================================
 * ==================================================================================================================================================== */
package org.jdownloader.myjdownloader.client.bindings;

import org.jdownloader.myjdownloader.client.json.AbstractJsonData;

public class AddLinksQuery extends AbstractJsonData {
    public AddLinksQuery(/* storable */) {

    }

    /**
     * @param autostart
     * @param links
     * @param packageName
     * @param extractPassword
     * @param downloadPassword
     * @param destinationFolder
     */
    public AddLinksQuery(final boolean autostart, final String links, final String packageName, final String extractPassword, final String downloadPassword, final String destinationFolder) {
        super();
        this.autostart = autostart;
        this.links = links;
        this.packageName = packageName;
        this.extractPassword = extractPassword;
        this.downloadPassword = downloadPassword;
        this.destinationFolder = destinationFolder;
    }

    private boolean autostart       = false;
    private boolean deepDecrypt     = false;
    private boolean autoExtract     = false;
    private String  links           = null;
    private String  packageName     = null;
    private String  extractPassword = null;
    private String  sourceUrl       = null;

    public boolean isAutostart() {
        return autostart;
    }

    public void setAutostart(final boolean autostart) {
        this.autostart = autostart;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(final String links) {
        this.links = links;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    public String getExtractPassword() {
        return extractPassword;
    }

    public void setExtractPassword(final String extractPassword) {
        this.extractPassword = extractPassword;
    }

    public String getDownloadPassword() {
        return downloadPassword;
    }

    public void setDownloadPassword(final String downloadPassword) {
        this.downloadPassword = downloadPassword;
    }

    public String getDestinationFolder() {
        return destinationFolder;
    }

    public void setDestinationFolder(final String destinationFolder) {
        this.destinationFolder = destinationFolder;
    }

    private PriorityStorable priority = PriorityStorable.DEFAULT;

    public PriorityStorable getPriority() {
        return priority;
    }

    public void setPriority(PriorityStorable priority) {
        this.priority = priority;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public boolean isAutoExtract() {
        return autoExtract;
    }

    public void setAutoExtract(boolean autoExtract) {
        this.autoExtract = autoExtract;
    }

    public boolean isDeepDecrypt() {
        return deepDecrypt;
    }

    public void setDeepDecrypt(boolean deepDecrypt) {
        this.deepDecrypt = deepDecrypt;
    }

    private String downloadPassword  = null;
    private String destinationFolder = null;

}