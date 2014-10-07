package org.jdownloader.myjdownloader.client.exceptions;

import org.jdownloader.myjdownloader.client.json.ErrorResponse.Source;

public class MyJDownloaderException extends Exception {
    
    private Source source;
    
    public Source getSource() {
        return this.source;
    }
    
    public MyJDownloaderException(final String string) {
        super(string);
    }
    
    public MyJDownloaderException() {
    }
    
    public MyJDownloaderException(final Exception e) {
        super(e);
    }
    
    public MyJDownloaderException(final String string, final Exception e) {
        super(string, e);
    }
    
    public static MyJDownloaderException get(final Exception e) {
        if (e instanceof MyJDownloaderException) { return (MyJDownloaderException) e; }
        return new MyJDownloaderException(e);
    }
    
    public static MyJDownloaderException get(final Exception e, String message) {
        if (e instanceof MyJDownloaderException) { return (MyJDownloaderException) e; }
        return new MyJDownloaderException(message, e);
    }
    
    @Override
    public String toString() {
        return super.toString() + "(SRC: " + this.getSource() + ")";
    }
    
    public void setSource(final Source src) {
        this.source = src;
    }
}
