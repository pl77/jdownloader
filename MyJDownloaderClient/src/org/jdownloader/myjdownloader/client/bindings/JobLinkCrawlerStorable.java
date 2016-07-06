package org.jdownloader.myjdownloader.client.bindings;

import org.jdownloader.myjdownloader.client.json.AbstractJsonData;

public class JobLinkCrawlerStorable extends AbstractJsonData {

    private boolean crawling;
    private boolean checking;
    private long    jobId;
    private int     broken;
    private int     found;
    private int     filtered;
    private int     unhandled;
    private int     crawled;

    public boolean isCrawling() {
        return crawling;
    }

    public void setCrawling(boolean crawling) {
        this.crawling = crawling;
    }

    public boolean isChecking() {
        return checking;
    }

    public void setChecking(boolean checking) {
        this.checking = checking;
    }

    public long getJobId() {
        return jobId;
    }

    public void setJobId(long jobId) {
        this.jobId = jobId;
    }

    public int getBroken() {
        return broken;
    }

    public void setBroken(int broken) {
        this.broken = broken;
    }

    public int getFound() {
        return found;
    }

    public void setFound(int found) {
        this.found = found;
    }

    public int getFiltered() {
        return filtered;
    }

    public void setFiltered(int filtered) {
        this.filtered = filtered;
    }

    public int getUnhandled() {
        return unhandled;
    }

    public void setUnhandled(int unhandled) {
        this.unhandled = unhandled;
    }

    public int getCrawled() {
        return crawled;
    }

    public void setCrawled(int crawled) {
        this.crawled = crawled;
    }
}
