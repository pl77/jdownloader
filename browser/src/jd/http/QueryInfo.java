package jd.http;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.appwork.utils.KeyValueStringEntry;
import org.appwork.utils.StringUtils;

public class QueryInfo {

    private List<KeyValueStringEntry> list;

    public QueryInfo() {
        this.list = new ArrayList<KeyValueStringEntry>();
    }

    @Override
    public String toString() {

        StringBuilder ret = new StringBuilder();
        for (KeyValueStringEntry s : this.list) {
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
        for (KeyValueStringEntry s : this.list) {
            if (StringUtils.equals(s.getKey(), key)) {
                return s.getValue();
            }
        }
        return null;
    }

    public String getDecoded(String key) throws UnsupportedEncodingException {
        for (KeyValueStringEntry s : this.list) {
            if (StringUtils.equals(s.getKey(), key)) {
                return URLDecoder.decode(s.getValue(), "ASCII");
            }
        }
        return null;
    }

    public List<KeyValueStringEntry> list() {
        return java.util.Collections.unmodifiableList(this.list);
    }

    public static QueryInfo get(Map<String, String> post) {
        QueryInfo ret = new QueryInfo();
        if (post != null) {
            for (Entry<String, String> es : post.entrySet()) {
                ret.add(es.getKey(), es.getValue());
            }
        }
        return ret;
    }

    public void addAndReplace(String key, String value) {
        int index = this.remove(key);
        if (index < 0) {
            // add new
            this.add(key, value);
        } else {
            // replace
            this.list.add(index, new KeyValueStringEntry(key, value));
        }

    }

    /**
     * Removes all entries for the key and returns the index of the first removed one
     * 
     * @param key
     * @return
     */
    public int remove(String key) {
        KeyValueStringEntry value;
        int first = -1;
        int i = 0;
        for (Iterator<KeyValueStringEntry> it = this.list.iterator(); it.hasNext();) {
            value = it.next();

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
        for (KeyValueStringEntry es : this.list) {
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
        ArrayList<QueryInfo> ret = new ArrayList<QueryInfo>();

        ArrayList<KeyValueStringEntry> lst = new ArrayList<KeyValueStringEntry>(this.list);
        while (true) {
            QueryInfo map = new QueryInfo();
            KeyValueStringEntry es;

            for (Iterator<KeyValueStringEntry> it = lst.iterator(); it.hasNext();) {
                es = it.next();
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
        QueryInfo ret = new QueryInfo();
        ret.addAll(post);
        return ret;
    }

    public void addAll(List<KeyValueStringEntry> post) {
        this.list.addAll(post);
    }

}