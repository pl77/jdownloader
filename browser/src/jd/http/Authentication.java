package jd.http;

public abstract class Authentication {
    protected final String username;

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getRealm() {
        return this.realm;
    }

    protected final String  password;
    protected final String  realm;
    protected final boolean isProxyAuthentication;
    protected final String  host;

    public String getHost() {
        return this.host;
    }

    public boolean isProxyAuthentication() {
        return this.isProxyAuthentication;
    }

    protected Authentication(final boolean isProxyAuthentication, final String host, final String username, final String password, final String realm) {
        this.isProxyAuthentication = isProxyAuthentication;
        this.host = host;
        this.username = username;
        this.password = password;
        this.realm = realm;
    }

    public abstract boolean authorize(Browser browser, Request request);

    public abstract boolean retry(Browser browser, Request request);
}
