//    jDownloader - Downloadmanager
//    Copyright (C) 2008  JD-Team jdownloader@freenet.de
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program  is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSSee the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://wnu.org/licenses/>.


package jd.plugins.decrypt;

import jd.plugins.DownloadLink;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;
import java.util.regex.Pattern;

import jd.plugins.PluginForDecrypt;
import jd.plugins.PluginStep;
import jd.plugins.Regexp;
import jd.plugins.RequestInfo;

// http://www.xup.in/a,7220/Test4JD/list/
// http://www.xup.in/a,7220/Test4JD/mini
// http://www.xup.in/a,7220/Test4JD/
// http://www.xup.in/a,7220

public class XupInFolder extends PluginForDecrypt {

    final 	static 	String 	HOST				= "xup.in";
    final 	static 	String 	NAME				= HOST + " Folder";
    private 		String	VERSION				= "0.1.0";
    private 		String	CODER				= "jD-Team";
    private 		String	ID					= HOST + "/a-" + VERSION;
    private 		Pattern	patternSupported 	= getSupportPattern(
    		"http://[*]xup.in/a,[0-9]+(/[+])?(/(list|mini))?");
    private 		String	LINK_PATTERN		= "href=\"(http://www.xup.in/dl,[0-9]*/.*?/)\"";
    
    public XupInFolder() {
    	
        super();
        steps.add(new PluginStep(PluginStep.STEP_DECRYPT, null));
        currentStep = steps.firstElement();
        
    }

    @Override
    public String getCoder() {
        return CODER;
    }

    @Override
    public String getHost() {
        return HOST;
    }

    @Override
    public String getPluginID() {
        return ID;
    }

    @Override
    public String getPluginName() {
        return NAME;
    }

    @Override
    public Pattern getSupportedLinks() {
        return patternSupported;
    }

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public boolean doBotCheck(File file) {
        return false;
    }
    
    @Override public PluginStep doStep(PluginStep step, String parameter) {
    	
    	if(step.getStep() == PluginStep.STEP_DECRYPT) {
    		
            Vector<DownloadLink> decryptedLinks = new Vector<DownloadLink>();
    		
    		try {
    			
    			RequestInfo requestInfo = getRequest(new URL(parameter));
    			String[][] links = new Regexp(requestInfo.getHtmlCode(), LINK_PATTERN).getMatches();
    			progress.setRange(links.length);
    			
    			for ( String[] link : links ) {
    				
    				decryptedLinks.add(this.createDownloadlink(link[0]));
    				progress.increase(1);
    				
	    		}
				
				step.setParameter(decryptedLinks);
				
    		} catch(IOException e) {
    			e.printStackTrace();
    		}
	        
    	}
    	
    	return null;
    	
    }
    
}