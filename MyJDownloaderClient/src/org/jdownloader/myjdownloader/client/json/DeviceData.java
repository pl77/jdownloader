/**
 * 
 * ====================================================================================================================================================
 * 	    "MyJDownloaderClient" License
 * 	    The "MyJDownloaderClient" will be called [The Product] from now on.
 * ====================================================================================================================================================
 * 	    Copyright (c) 2009-2015, AppWork GmbH <e-mail@appwork.org>
 * 	    Schwabacher Straße 117
 * 	    90763 Fürth
 * 	    Germany   
 * === Preamble ===
 * 	This license establishes the terms under which the [The Product] Source Code & Binary files may be used, copied, modified, distributed, and/or redistributed.
 * 	The intent is that the AppWork GmbH is able to provide  their utilities library for free to non-commercial projects whereas commercial usage is only permitted after obtaining a commercial license.
 * 	These terms apply to all files that have the [The Product] License header (IN the file), a <filename>.license or <filename>.info (like mylib.jar.info) file that contains a reference to this license.
 * 	
 * === 3rd Party Licences ===
 * 	Some parts of the [The Product] use or reference 3rd party libraries and classes. These parts may have different licensing conditions. Please check the *.license and *.info files of included libraries
 * 	to ensure that they are compatible to your use-case. Further more, some *.java have their own license. In this case, they have their license terms in the java file header. 	
 * 	
 * === Definition: Commercial Usage ===
 * 	If anybody or any organization is generating income (directly or indirectly) by using [The Product] or if there's as much as a 
 * 	sniff of commercial interest or aspect in what you are doing, we consider this as a commercial usage. If you are unsure whether your use-case is commercial or not, consider it as commercial.
 * === Dual Licensing ===
 * === Commercial Usage ===
 * 	If you want to use [The Product] in a commercial way (see definition above), you have to obtain a paid license from AppWork GmbH.
 * 	Contact AppWork for further details: e-mail@appwork.org
 * === Non-Commercial Usage ===
 * 	If there is no commercial usage (see definition above), you may use [The Product] under the terms of the 
 * 	"GNU Affero General Public License" (http://www.gnu.org/licenses/agpl-3.0.en.html).
 * 	
 * 	If the AGPL does not fit your needs, please contact us. We'll find a solution.
 * ====================================================================================================================================================
 * ==================================================================================================================================================== */
package org.jdownloader.myjdownloader.client.json;

public class DeviceData {
    private String id;

    private String type;
    private String name;

    public DeviceData(/* storable */) {

    }

    @Override
    public boolean equals(final Object obj) {

        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DeviceData)) {
            return false;
        }
        if (!equalsString(id, ((DeviceData) obj).id)) {
            return false;
        }
        if (!equalsString(type, ((DeviceData) obj).type)) {
            return false;
        }
        if (!equalsString(name, ((DeviceData) obj).name)) {
            return false;
        }
        return true;
    }

    public static boolean equalsString(final String pass, final String pass2) {
        if (pass == pass2) { return true; }
        if (pass == null && pass2 != null) { return false; }
        return pass.equals(pass2);
    }

    @Override
    public int hashCode() {
        return id == null ? 0 : id.hashCode();
    }

    public DeviceData(final String deviceid, final String deviceType, final String deviceName) {
        id = deviceid;
        type = deviceType;
        name = deviceName;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return name + " (" + id + ")";
    }

}
