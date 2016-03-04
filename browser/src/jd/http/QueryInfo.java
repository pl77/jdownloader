package jd.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jd.nutils.encoding.Encoding;

import org.appwork.utils.KeyValueStringEntry;
import org.appwork.utils.StringUtils;

public class QueryInfo {

    private final List<KeyValueStringEntry> list = new ArrayList<KeyValueStringEntry>();

    public QueryInfo() {
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        for (final KeyValueStringEntry s : this.list) {
            if (ret.length() > 0) {
                ret.append("&");
            }
            ret.append(s.getKey()).append("=").append(s.getValue());
        }
        return ret.toString();
    }

    public void add(String key, String value) {
        this.list.add(new KeyValueStringEntry(key, value));
    }

    /**
     * Gets the first entry for the key . The result is probably urlEncoded
     *
     * @param key
     * @return
     */
    public String get(String key) {
        for (final KeyValueStringEntry s : this.list) {
            if (StringUtils.equals(s.getKey(), key)) {
                return s.getValue();
            }
        }
        return null;
    }

    public String getDecoded(String key) throws UnsupportedEncodingException {
        return this.getDecoded(key, "UTF-8");
    }

    public String getDecoded(String key, String encoding) throws UnsupportedEncodingException {
        if (StringUtils.isEmpty(encoding)) {
            encoding = "UTF-8";
        }
        for (final KeyValueStringEntry s : this.list) {
            if (StringUtils.equals(s.getKey(), key)) {
                return URLDecoder.decode(s.getValue(), encoding);
            }
        }
        return null;
    }

    public List<KeyValueStringEntry> list() {
        return java.util.Collections.unmodifiableList(this.list);
    }

    public static QueryInfo get(Map<String, String> post) {
        final QueryInfo ret = new QueryInfo();
        if (post != null) {
            for (final Entry<String, String> es : post.entrySet()) {
                ret.add(es.getKey(), es.getValue());
            }
        }
        return ret;
    }

    public QueryInfo addAndReplace(String key, String value) {
        final int index = this.remove(key);
        if (index < 0) {
            // add new
            this.add(key, value);
        } else {
            // replace
            this.list.add(index, new KeyValueStringEntry(key, value));
        }
        return this;
    }

    /**
     * Removes all entries for the key and returns the index of the first removed one
     *
     * @param key
     * @return
     */
    public int remove(String key) {
        int first = -1;
        int i = 0;
        for (final Iterator<KeyValueStringEntry> it = this.list.iterator(); it.hasNext();) {
            final KeyValueStringEntry value = it.next();
            if (StringUtils.equals(value.getKey(), key)) {
                it.remove();
                if (first < 0) {
                    first = i;
                }
            }
            i++;
        }
        return first;
    }

    public boolean containsKey(String key) {
        for (final KeyValueStringEntry es : this.list) {
            if (StringUtils.equals(es.getKey(), key)) {
                return true;
            }
        }
        return false;
    }

    public boolean addIfNoAvailable(String key, String value) {
        if (this.containsKey(key)) {
            return false;
        }
        this.add(key, value);
        return true;
    }

    /**
     * Tries to split the information if a key is used several times.
     *
     *
     * @return
     */
    public List<QueryInfo> split() {
        final ArrayList<QueryInfo> ret = new ArrayList<QueryInfo>();
        final ArrayList<KeyValueStringEntry> lst = new ArrayList<KeyValueStringEntry>(this.list);
        while (true) {
            final QueryInfo map = new QueryInfo();
            for (final Iterator<KeyValueStringEntry> it = lst.iterator(); it.hasNext();) {
                final KeyValueStringEntry es = it.next();
                if (!map.containsKey(es.getKey())) {
                    map.add(es.getKey(), es.getValue());
                    it.remove();
                }
            }
            if (map.size() > 0) {
                ret.add(map);
            }
            if (lst.size() == 0) {
                break;
            }
        }
        return ret;
    }

    private int size() {
        return this.list.size();
    }

    public static QueryInfo get(List<KeyValueStringEntry> post) {
        final QueryInfo ret = new QueryInfo();
        ret.addAll(post);
        return ret;
    }

    public void addAll(List<KeyValueStringEntry> post) {
        this.list.addAll(post);
    }

    public LinkedHashMap<String, String> toMap() {
        final LinkedHashMap<String, String> ret = new LinkedHashMap<String, String>();
        for (KeyValueStringEntry e : this.list) {
            ret.put(e.getKey(), e.getValue());
        }
        return ret;
    }

    public QueryInfo append(String key, String value, boolean urlencode) {
        this.addAndReplace(key, urlencode ? Encoding.urlEncode(value) : value);
        return this;
    }

}