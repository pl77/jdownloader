package jd.gui;

import java.io.File;
import java.util.Vector;

import jd.event.ControlEvent;
import jd.event.UIEvent;
import jd.event.UIListener;
import jd.plugins.DownloadLink;
import jd.plugins.Plugin;
import jd.plugins.event.PluginEvent;

/**
 * INterface für alle GUIS
 * @author astaldo
 *
 */
public interface UIInterface {
    /**
     * Liefert alle DownloadLinks zurück
     * @return Alle DownloadLinks
     */
    public Vector<DownloadLink> getDownloadLinks();
    /**
     * Der Benutzer soll den Captcha Code eintippen
     * 
     * @param plugin Das Plugin, daß den Captcha Code benötigt
     * @param captchaAddress Die Adresse des Captchas
     * @return Der erkannte Text
     */
    public String getCaptchaCodeFromUser(Plugin plugin, File captchaAddress);
    /**
     * Legt alle DownloadLinks fest
     * 
     * @param downloadLinks Alle DownloadLinks
     */
    public void setDownloadLinks(Vector<DownloadLink> downloadLinks);
    /**
     * Leitet ein PluginEvent weiter
     * 
     * @param event Ein PluginEvent
     */
    public void delegatedPluginEvent(PluginEvent event);
    /**
     * Leitet ein ControlEvent weiter
     * 
     * @param event ein ControlEvent
     */
    public void delegatedControlEvent(ControlEvent event);
    /**
     * Fügt einen UIListener hinzu
     * 
     * @param listener Ein neuer UIListener
     */
    public void addUIListener(UIListener listener);
    /**
     * Entfernt einen UIListener
     * 
     * @param listener UIListener, der entfernt werden soll
     */
    public void removeUIListener(UIListener listener);
    /**
     * Verteilt ein UIEvent an alle registrierten Listener
     * 
     * @param pluginEvent Das UIEvent, daß verteilt werden soll
     */
    public void fireUIEvent(UIEvent pluginEvent);
    /**
     * Zeigt einen MessageDialog an
     * @param string
     */
    public void showMessageDialog(String string);
    /**
     * Zeigt einen Eingabe Dialog an
     * @param string Die Nachricht, die angezeigt werden soll
     * @return Der vom Benutzer eingegebene Text
     */
    public String showUserInputDialog(String string);
    /**
     * Zeigt einen MessageDialog an
     * @param string Nachricht, die angezeigt werden soll
     * @return Wahr, falls diese Anfrage bestätigt wurde, andernfalls falsch
     */
    public boolean showConfirmDialog(String string);
    /**
     * Fügt Links zum Linkgrabber hinzu
     * @param links
     */
    public void addLinksToGrabber(Vector<DownloadLink> links);
    /**
     * Zeigt einen Textarea dialog an
     * @param string
     */
    public String showTextAreaDialog(String title,String question,String def);
    
    /**
     * Zeigt einen HTML dialog an
     * @param string
     */
    public boolean showHTMLDialog(String title,String htmlQuestion);
    
    /**
     * Zeigt einen HTML dialog an
     * @param string
     */
    public int showHelpMessage(String title,String message, String url);
    /**
     * Wird aufgeruifen sobald die Initialisierung aller Module komplett ist
     */
    public void onJDInitComplete();
 
}
