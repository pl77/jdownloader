package org.jdownloader.myjdownloader.client.bindings;

import java.util.List;

import org.jdownloader.myjdownloader.client.json.AbstractJsonData;

public class ArchiveSettingsStorable extends AbstractJsonData {
    private String       archiveId                          = null;
    private Boolean      autoExtract                        = null;
    private String       extractPath                        = null;
    private String       finalPassword                      = null;
    private List<String> passwords                          = null;
    private Boolean      removeDownloadLinksAfterExtraction = null;
    private Boolean      removeFilesAfterExtraction         = null;

    public ArchiveSettingsStorable(/* Storable */) {

    }

    public Boolean getAutoExtract() {
        return autoExtract;
    }

    public void setAutoExtract(Boolean autoExtract) {
        this.autoExtract = autoExtract;
    }

    public String getExtractPath() {
        return extractPath;
    }

    public void setExtractPath(String extractPath) {
        this.extractPath = extractPath;
    }

    public String getFinalPassword() {
        return finalPassword;
    }

    public void setFinalPassword(String finalPassword) {
        this.finalPassword = finalPassword;
    }

    public List<String> getPasswords() {
        return passwords;
    }

    public void setPasswords(List<String> passwords) {
        this.passwords = passwords;
    }

    public Boolean getRemoveDownloadLinksAfterExtraction() {
        return removeDownloadLinksAfterExtraction;
    }

    public void setRemoveDownloadLinksAfterExtraction(Boolean removeDownloadLinksAfterExtraction) {
        this.removeDownloadLinksAfterExtraction = removeDownloadLinksAfterExtraction;
    }

    public Boolean getRemoveFilesAfterExtraction() {
        return removeFilesAfterExtraction;
    }

    public void setRemoveFilesAfterExtraction(Boolean removeFilesAfterExtraction) {
        this.removeFilesAfterExtraction = removeFilesAfterExtraction;
    }

    public String getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(String archiveId) {
        this.archiveId = archiveId;
    }
}
