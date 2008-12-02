//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team jdownloader@freenet.de
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

package jd.config;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;

import jd.nutils.io.JDIO;

public class CFGConfig extends SubConfiguration implements Serializable {

    private static HashMap<String, CFGConfig> CONFIGS = new HashMap<String, CFGConfig>();
    /**
     * 
     */
    private static final long serialVersionUID = 9187069483565313810L;

    /**
     * Diese Klasse stellt Die Configuration als eine Configfile zur verfügung. Diese infos werden mit Absicht! nicht in der Datenbank gespeichert!
     * @param name
     */
    @SuppressWarnings("unchecked")
    private CFGConfig(String name) {
        this.name = name;
        File file;
        Object props = JDIO.loadObject(null, file = JDUtilities.getResourceFile("config/" + name + ".cfg"), false);
        file.getParentFile().mkdirs();
        if (props != null) {
            setProperties((HashMap<String, Object>) props);
        }
    }

    public void save() {
        JDIO.saveObject(null, getProperties(), JDUtilities.getResourceFile("config/" + name + ".cfg"), null, null, false);
    }

    public static CFGConfig getConfig(String string) {
        if (CONFIGS.containsKey(string)) return CONFIGS.get(string);
        CFGConfig ret = new CFGConfig(string);
        CONFIGS.put(string, ret);
        return ret;
    }

}