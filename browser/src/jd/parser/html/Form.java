//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team support@jdownloader.org
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package jd.parser.html;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jd.nutils.encoding.HTMLEntities;
import jd.parser.Regex;
import jd.utils.EditDistance;

import org.appwork.utils.KeyValueStringEntry;
import org.appwork.utils.StringUtils;
import org.appwork.utils.net.URLHelper;

public class Form {
    public enum MethodType {
        GET,
        POST
    }

    /**
     * Ein Array mit allen Forms dessen Inhalt dem matcher entspricht. Achtung der Matcher bezieht sich nicht auf die Properties einer Form
     * sondern auf den Text der zwischen der Form steht. DafÃ¼r gibt es die formProperties
     */
    public static Form[] getForms(final Object requestInfo) {
        final LinkedList<Form> forms = new LinkedList<Form>();
        // opening and closing within opening tag | opening and closing with traditional tags | opened ended tag (no closing)
        final Pattern pattern = Pattern.compile("<\\s*form\\s+([^>]*)/\\s*>|<\\s*form(?:>|\\s+[^>]*>)(.*?)<\\s*/\\s*form\\s*>|<\\s*form(?:>|\\s+[^>]*>)(.+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        final Matcher formmatcher = pattern.matcher(requestInfo.toString().replaceAll("(?s)<!--.*?-->", ""));
        while (formmatcher.find()) {
            final String total = formmatcher.group(0);
            // System.out.println(inForm);
            final Form form = new Form(total);
            forms.add(form);
        }
        return forms.toArray(new Form[forms.size()]);
    }

    /**
     * Action der Form entspricht auch oft einer URL
     */
    private String                        action;
    private java.util.List<InputField>    inputfields;
    private String                        htmlcode = null;
    private MethodType                    method   = MethodType.GET;
    /* default encoding for http forms */
    private String                        encoding = "application/x-www-form-urlencoded";
    private InputField                    preferredSubmit;
    private final HashMap<String, String> keyValueMap;

    public Form() {
        this.inputfields = new ArrayList<InputField>();
        this.keyValueMap = new HashMap<String, String>();
    }

    public Form(final String total) {
        this();
        this.parse(total);
    }

    public void addInputField(final InputField nv) {
        this.inputfields.add(nv);
    }

    public void addInputFieldAt(final InputField nv, final int i) {
        this.inputfields.add(i, nv);
    }

    /**
     * Gibt zurÃ¼ck ob der gesuchte needle String im html Text bgefunden wurde
     *
     * @param fileNotFound
     * @return
     */
    public boolean containsHTML(final String needle) {
        return new Regex(this.htmlcode, needle).matches();
    }

    public boolean equalsIgnoreCase(final Form f) {
        return this.toString().equalsIgnoreCase(f.toString());
    }

    public String getAction() {
        return this.action;
    }

    public String getAction(final URL base) {
        final String formAction = this.getAction();
        final boolean baseIsHTTPs = base != null && "https".equalsIgnoreCase(base.getProtocol());
        final boolean actionIsHTTPs = formAction != null && formAction.startsWith("https://");
        final boolean actionIsHTTP = formAction != null && formAction.startsWith("http://");
        final String ret;
        if (base != null && StringUtils.isNotEmpty(this.action)) {
            ret = URLHelper.parseLocation(base, this.action);
        } else if (StringUtils.isNotEmpty(this.action) && this.action.matches("^https?://.+")) {
            ret = this.action.replaceAll(" ", "%20");
        } else if (base != null && StringUtils.isEmpty(this.action)) {
            ret = base.toString();
        } else {
            ret = null;
        }
        if (ret != null && baseIsHTTPs) {
            if (actionIsHTTPs || actionIsHTTP == false) {
                /* only keep https when action does use https or not specified, but do NOT change formAction(http) to https */
                return ret.replaceFirst("http://", "https://");
            }
        }
        try {
            return URLHelper.fixPathTraversal(new URL(ret)).toString();
        } catch (MalformedURLException e) {
            return ret;
        }
    }

    /**
     * Gibt den variablennamen der am besten zu varname passt zurÃ¼ck.
     *
     * @param varname
     * @return
     */
    public String getBestVariable(final String varname) {
        String best = null;
        int bestDist = Integer.MAX_VALUE;
        for (final InputField ipf : this.inputfields) {
            final int dist = EditDistance.getLevenshteinDistance(varname, ipf.getKey());
            if (dist < bestDist) {
                best = ipf.getKey();
                bestDist = dist;
            }
        }
        return best;
    }

    public String getEncoding() {
        return this.encoding;
    }

    public String getHtmlCode() {
        return this.htmlcode;
    }

    /**
     * Gets the first inputfiled with this key. REMEMBER. There can be more than one file with this key
     *
     * @param key
     * @return
     */
    public InputField getInputField(final String key) {
        for (final InputField ipf : this.inputfields) {
            if (ipf.getKey() != null && ipf.getKey().equalsIgnoreCase(key)) {
                return ipf;
            }
        }
        return null;
    }

    public InputField getInputFieldByName(final String name) {
        for (final InputField ipf : this.inputfields) {
            if (StringUtils.equalsIgnoreCase(ipf.getKey(), name)) {
                return ipf;
            }
        }
        return null;
    }

    public InputField getInputFieldByType(final String type) {
        for (final InputField ipf : this.inputfields) {
            if (StringUtils.equalsIgnoreCase(ipf.getType(), type)) {
                return ipf;
            }
        }
        return null;
    }

    public java.util.List<InputField> getInputFields() {
        return this.inputfields;
    }

    public java.util.List<InputField> getInputFieldsByType(final String type) {
        final java.util.List<InputField> ret = new ArrayList<InputField>();
        for (final InputField ipf : this.inputfields) {
            if (ipf.getType() != null && org.appwork.utils.Regex.matches(ipf.getType(), type)) {
                ret.add(ipf);
            }
        }
        return ret;
    }

    public MethodType getMethod() {
        return this.method;
    }

    public InputField getPreferredSubmit() {
        return this.preferredSubmit;
    }

    /**
     * GIbt alle variablen als propertyString zurÃ¼ck
     *
     * @return
     */
    public String getPropertyString() {
        final StringBuilder stbuffer = new StringBuilder();
        boolean first = true;
        for (final InputField ipf : this.inputfields) {
            /* nameless key-value are not being sent, see firefox */
            if (ipf.getKey() == null) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                stbuffer.append("&");
            }
            stbuffer.append(ipf.getKey());
            stbuffer.append("=");
            stbuffer.append(ipf.getValue());
        }
        return stbuffer.toString();
    }

    /**
     * Gibt ein RegexObject bezÃ¼glich des Form htmltextes zurÃ¼ck
     *
     * @param compile
     * @return
     */
    public Regex getRegex(final Pattern compile) {
        return new Regex(this.htmlcode, compile);
    }

    /**
     * Gibt ein RegexObject bezÃ¼glich des Form htmltextes zurÃ¼ck
     *
     * @param compile
     * @return
     */
    public Regex getRegex(final String string) {
        return new Regex(this.htmlcode, string);
    }

    /**
     * Returns a list of request variables
     *
     * @return
     */
    public java.util.List<KeyValueStringEntry> getRequestVariables() {
        final List<KeyValueStringEntry> ret = new ArrayList<KeyValueStringEntry>();
        for (final InputField ipf : this.inputfields) {
            // Do not send not prefered Submit types
            if (this.getPreferredSubmit() != null && ipf.getType() != null && ipf.getType().equalsIgnoreCase("submit") && this.getPreferredSubmit() != ipf) {
                continue;
            }
            if (ipf.getKey() == null || ipf.getValue() == null) {
                /*
                 * nameless key-value are not being sent, see firefox
                 */
                continue;
            }
            if (StringUtils.equalsIgnoreCase("image", ipf.getType())) {
                final InputField x = this.getInputField(ipf.getKey() + ".x");
                if (x == null || x.getValue() == null) {
                    ret.add(new KeyValueStringEntry(ipf.getKey() + ".x", new Random().nextInt(100) + ""));
                }
                final InputField y = this.getInputField(ipf.getKey() + ".y");
                if (y == null || y.getValue() == null) {
                    ret.add(new KeyValueStringEntry(ipf.getKey() + ".y", new Random().nextInt(100) + ""));
                }
            } else {
                ret.add(new KeyValueStringEntry(ipf.getKey(), ipf.getValue()));
            }
        }
        return ret;
    }

    public String getStringProperty(final String property) {
        // TODO Auto-generated method stub
        return this.keyValueMap.get(property);
    }

    public HashMap<String, String> getVarsMap() {
        final HashMap<String, String> ret = new HashMap<String, String>();
        for (final InputField ipf : this.inputfields) {
            /* nameless key-value are not being sent, see firefox */
            if (ipf.getKey() == null) {
                continue;
            }
            ret.put(ipf.getKey(), ipf.getValue());
        }
        return ret;
    }

    public boolean hasInputFieldByName(final String name) {
        return this.getInputFieldByName(name) != null;
    }

    private void parse(final String total) {
        this.htmlcode = total;
        // form.baseRequest = requestInfo;
        final String header = new Regex(total, "<[\\s]*form(.*?)>").getMatch(0);
        //
        // <[\\s]*form(.*?)>(.*?)<[\\s]*/[\\s]*form[\\s]*>|<[\\s]*form(.*?)>(.+)
        final String[][] headerEntries = new Regex(header, "(\\w+?)\\s*=\\s*('|\")(.*?)(\\2)").getMatches();
        final String[][] headerEntries2 = new Regex(header, "(\\w+?)\\s*=\\s*([^> \"']+)").getMatches();
        this.parseHeader(headerEntries);
        this.parseHeader(headerEntries2);
        this.parseInputFields(total);
    }

    private void parseHeader(final String[][] headerEntries) {
        if (headerEntries != null) {
            for (final String[] entry : headerEntries) {
                final String key;
                final String value;
                if (entry.length == 4) {
                    key = entry[0];
                    value = entry[2];
                } else {
                    key = entry[0];
                    value = entry[1];
                }
                final String lowvalue = value.toLowerCase(Locale.ENGLISH);
                if (key.equalsIgnoreCase("action")) {
                    this.setAction(HTMLEntities.unhtmlentities(value));
                } else if (key.equalsIgnoreCase("enctype")) {
                    this.setEncoding(value);
                } else if (key.equalsIgnoreCase("method")) {
                    if (lowvalue.matches(".*post.*")) {
                        this.setMethod(MethodType.POST);
                    } else if (lowvalue.matches(".*get.*")) {
                        this.setMethod(MethodType.GET);
                    } else {
                        /* fallback */
                        this.setMethod(MethodType.POST);
                    }
                } else {
                    this.setProperty(key, value);
                }
            }
        }
    }

    private final void parseInputFields(final String htmlCode) {
        this.inputfields = new ArrayList<InputField>();
        String escapedHtmlCode = htmlCode;
        final List<String> values = new ArrayList<String>();
        final long timeStamp = System.nanoTime();
        final Pattern value1 = Pattern.compile("(?s)(?<!\\\\)\"(.*?)(?<!\\\\)\"");
        final Pattern value2 = Pattern.compile("(?s)(?<!\\\\)'(.*?)(?<!\\\\)'");
        boolean matches = false;
        while (true) {
            Matcher matcher = value1.matcher(escapedHtmlCode);
            matches = matcher.find();
            if (!matches) {
                matcher = value2.matcher(escapedHtmlCode);
                matches = matcher.find();
            }
            if (matches) {
                final String replace = matcher.group(0);
                final String value = matcher.group(1);
                if (replace == null) {
                    break;
                } else {
                    final int index = values.size();
                    values.add(value);
                    escapedHtmlCode = escapedHtmlCode.replace(replace, "VALUE-" + timeStamp + "-" + index + " ");
                }
            } else {
                break;
            }
        }
        final Matcher matcher = Pattern.compile("(?s)(<\\s*(input|textarea|select).*?>)", Pattern.CASE_INSENSITIVE).matcher(escapedHtmlCode);
        while (matcher.find()) {
            final InputField nv = InputField.parse(matcher.group(1), timeStamp, values);
            if (nv != null) {
                this.addInputField(nv);
            }
        }
    }

    /**
     * Changes the value of the first filed with the key key to value. if no field exists, a new one is created.
     *
     * @param key
     * @param value
     */
    public void put(final String key, final String value) {
        final InputField ipf = this.getInputField(key);
        if (ipf != null) {
            ipf.setValue(value);
        } else {
            this.inputfields.add(new InputField(key, value));
        }
    }

    /**
     * Removes the first inputfiled with this key. REMEMBER. There can be more than one file with this key
     *
     * @param key
     * @return
     */
    public void remove(final String key) {
        /*
         * inputfields extends hashmap which overrides hashCode, thats why we use iterator here
         */
        final Iterator<InputField> it = this.inputfields.iterator();
        while (it.hasNext()) {
            final InputField ipf = it.next();
            if (ipf.getKey() == null && key == null) {
                it.remove();
                return;
            }
            if (ipf.getKey() != null && ipf.getKey().equalsIgnoreCase(key)) {
                it.remove();
                return;
            }
        }
    }

    public void setAction(final String action) {
        this.action = action;
    }

    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }

    public void setMethod(final MethodType method) {
        this.method = method;
    }

    /**
     * Us the i-th submit field when submitted
     *
     * @param i
     */
    public void setPreferredSubmit(int i) {
        this.preferredSubmit = null;
        for (final InputField ipf : this.inputfields) {
            if (ipf.getType() != null && ipf.getValue() != null && ipf.getType().equalsIgnoreCase("submit") && i-- <= 0) {
                this.preferredSubmit = ipf;
                return;
            }
        }
        throw new IllegalArgumentException("No such Submitfield: " + i);
    }

    /**
     * Tell the form which submit field to use
     *
     * @param preferredSubmit
     */
    public void setPreferredSubmit(final String preferredSubmit) {
        this.preferredSubmit = null;
        for (final InputField ipf : this.inputfields) {
            if (ipf.getType() != null && ipf.getValue() != null && ipf.getType().equalsIgnoreCase("submit") && ipf.getValue().equalsIgnoreCase(preferredSubmit)) {
                this.preferredSubmit = ipf;
                return;
            }
        }
        // org.appwork.utils.logging2.extmanager.LoggerFactory.getDefaultLogger().warning("No exact match for submit found! Trying to find
        // best match now!");
        for (final InputField ipf : this.inputfields) {
            if (ipf.getType() != null && ipf.getValue() != null && ipf.getType().equalsIgnoreCase("submit") && ipf.getValue().contains(preferredSubmit)) {
                this.preferredSubmit = ipf;
                return;
            }
        }
        throw new IllegalArgumentException("No such Submitfield: " + preferredSubmit);
    }

    public void setProperty(final String key, final String value) {
        this.keyValueMap.put(key, value);
    }

    @Override
    public String toString() {
        final StringBuilder ret = new StringBuilder();
        ret.append("Action: ");
        ret.append(this.action);
        ret.append('\n');
        if (this.method == MethodType.POST) {
            ret.append("Method: POST\n");
        } else if (this.method == MethodType.GET) {
            ret.append("Method: GET\n");
        }
        for (final InputField ipf : this.inputfields) {
            ret.append(ipf.toString());
            ret.append('\n');
        }
        ret.append(this.keyValueMap.toString());
        return ret.toString();
    }

    public boolean removeInputField(InputField f) {
        return this.inputfields.remove(f);
    }
}
