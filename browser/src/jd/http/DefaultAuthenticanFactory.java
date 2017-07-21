package jd.http;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.appwork.net.protocol.http.HTTPConstants;
import org.appwork.utils.Regex;
import org.appwork.utils.StringUtils;

public class DefaultAuthenticanFactory implements AuthenticationFactory {
    protected final String host;
    protected final String realm;

    public String getRealm() {
        return this.realm;
    }

    public String getHost() {
        return this.host;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    protected final String username;
    protected final String password;

    public DefaultAuthenticanFactory(final String host, final String username, final String password) {
        this(host, null, username, password);
    }

    public DefaultAuthenticanFactory(final String host, final String realm, final String username, final String password) {
        this.host = host;
        this.realm = realm;
        this.username = username;
        this.password = password;
    }

    protected boolean requiresAuthentication(Request request) {
        return request.getHttpConnection().getResponseCode() == 401 && request.getResponseHeader(HTTPConstants.HEADER_RESPONSE_WWW_AUTHENTICATE) != null;
    }

    protected CopyOnWriteArrayList<Authentication> authentications = new CopyOnWriteArrayList<Authentication>();

    public boolean containsAuthentication(Authentication authentication) {
        return authentication != null && this.authentications.contains(authentication);
    }

    public boolean addAuthentication(Authentication authentication) {
        return authentication != null && this.authentications.addIfAbsent(authentication);
    }

    public boolean removeAuthentication(Authentication authentication) {
        return authentication != null && this.authentications.remove(authentication);
    }

    public List<Authentication> getAuthentications() {
        return this.authentications;
    }

    /**
     * use URL.getUserInfo() for authentication
     */
    public DefaultAuthenticanFactory() {
        this(null, null, null, null);
    }

    protected Authentication buildBasicAuthentication(Browser browser, Request request, final String realm) {
        if (this.getHost() == null) {
            final String userInfo[] = new Regex(request.getURL().getUserInfo(), "^(.*?)(:(.*?))?$").getRow(0);
            if (userInfo != null) {
                return new BasicAuthentication(request.getURL().getHost(), userInfo[0], userInfo[2], realm);
            }
        } else if (this.matchesHost(request) && (this.getRealm() == null || this.matchesRealm(realm))) {
            if (StringUtils.isNotEmpty(this.getUsername()) || StringUtils.isNotEmpty(this.getPassword())) {
                return new BasicAuthentication(this.getHost(), this.getUsername(), this.getPassword(), realm);
            }
        }
        return null;
    }

    protected boolean matchesHost(final Request request) {
        return StringUtils.endsWithCaseInsensitive(request.getURL().getHost(), this.getHost());
    }

    protected boolean matchesRealm(final String realm) {
        return StringUtils.equalsIgnoreCase(realm, this.getRealm());
    }

    protected Authentication buildDigestAuthentication(Browser browser, Request request, final String realm) {
        final String wwwAuthenticate = request.getResponseHeader(HTTPConstants.HEADER_RESPONSE_WWW_AUTHENTICATE);
        final String qop = new Regex(wwwAuthenticate, "qop\\s*=\\s*\"(.*?)\"").getMatch(0);
        if (StringUtils.equalsIgnoreCase(qop, "auth")) {
            final String nonce = new Regex(wwwAuthenticate, "nonce\\s*=\\s*\"(.*?)\"").getMatch(0);
            String algorithm = new Regex(wwwAuthenticate, "algorithm\\s*=\\s*(MD5|SHA-256)").getMatch(0);
            if (algorithm == null) {
                algorithm = new Regex(wwwAuthenticate, "algorithm\\s*=\\s*\"(MD5|SHA-256)\"").getMatch(0);
            }
            if (nonce != null && algorithm != null) {
                final String opaque = new Regex(wwwAuthenticate, "opaque\\s*=\\s*\"(.*?)\"").getMatch(0);
                if (this.getHost() == null) {
                    final String userInfo[] = new Regex(request.getURL().getUserInfo(), "^(.*?)(:(.*?))?$").getRow(0);
                    if (userInfo != null) {
                        return new DigestAuthentication(request.getURL().getHost(), userInfo[0], userInfo[2], realm, nonce, algorithm, qop, opaque);
                    }
                } else if (this.matchesHost(request) && (this.getRealm() == null || this.matchesRealm(realm))) {
                    if (StringUtils.isNotEmpty(this.getUsername()) || StringUtils.isNotEmpty(this.getPassword())) {
                        return new DigestAuthentication(request.getURL().getHost(), this.getUsername(), this.getPassword(), realm, nonce, algorithm, qop, opaque);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean retry(Authentication authentication, Browser browser, Request request) {
        return authentication != null && this.containsAuthentication(authentication) && authentication.retry(browser, request);
    }

    protected String getRealm(Request request) {
        final String wwwAuthenticate = request.getResponseHeader(HTTPConstants.HEADER_RESPONSE_WWW_AUTHENTICATE);
        return new Regex(wwwAuthenticate, "realm\\s*=\\s*\"(.*?)\"").getMatch(0);
    }

    @Override
    public Authentication buildAuthentication(Browser browser, Request request) {
        if (request.getAuthentication() == null && this.requiresAuthentication(request)) {
            final String wwwAuthenticate = request.getResponseHeader(HTTPConstants.HEADER_RESPONSE_WWW_AUTHENTICATE);
            if (wwwAuthenticate != null) {
                final String realm = new Regex(wwwAuthenticate, "realm\\s*=\\s*\"(.*?)\"").getMatch(0);
                if (wwwAuthenticate.matches("(?i)^\\s*Basic.*")) {
                    final Authentication ret = this.buildBasicAuthentication(browser, request, realm);
                    this.addAuthentication(ret);
                    return ret;
                } else if (wwwAuthenticate.matches("(?i)^\\s*Digest.*")) {
                    final Authentication ret = this.buildDigestAuthentication(browser, request, realm);
                    this.addAuthentication(ret);
                    return ret;
                }
            }
        }
        return null;
    }

    @Override
    public Authentication authorize(Browser browser, Request request) {
        for (final Authentication authentication : this.getAuthentications()) {
            if (authentication.authorize(browser, request)) {
                return authentication;
            }
        }
        return null;
    }
}
