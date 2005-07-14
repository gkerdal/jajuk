/*
 *  Jajuk
 *  Copyright (C) 2003 Bertrand Florat
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU General Public License
 *  as published by the Free Software Foundation; either version 2
 *  of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *  $Revision$
 */

package org.jajuk.base;

import org.jajuk.util.MD5Processor;

/**
 * Convenient class to manage styles
 * @author Bertrand Florat 
 * @created 17 oct. 2003
 */
public class StyleManager extends ItemManager {
    /**Self instance*/
    private static StyleManager singleton;

	/**
	 * No constructor available, only static access
	 */
	private StyleManager() {
		super();
	}

    /**
     * @return singleton
     */
    public static StyleManager getInstance(){
      if (singleton == null){
          singleton = new StyleManager();
      }
        return singleton;
    }

	/**
	 * Register a style
	 * 
	 * @param sName
	 */
	public  synchronized Style registerStyle(String sName) {
		String sId = MD5Processor.hash(sName.trim().toLowerCase());
		return registerStyle(sId, sName);
	}

	/**
	 * Register a style with a known id
	 * 
	 * @param sName
	 */
	public  synchronized Style registerStyle(String sId, String sName) {
		if (hmItems.containsKey(sId)) {
			return (Style) hmItems.get(sId);
		}
		Style style = new Style(sId, sName);
		hmItems.put(sId, style);
        postRegistering(style);
		return style;
	}
		
	/**
	 * Format the Style name to be normalized :
	 * <p>
	 * -no underscores or other non-ascii characters
	 * <p>
	 * -no spaces at the begin and the end
	 * <p>
	 * -All in upper case
	 * <p>
	 * exemple: "ROCK"
	 * 
	 * @param sName
	 * @return
	 */
	private static synchronized String format(String sName) {
		String sOut;
		sOut = sName.trim(); //supress spaces at the begin and the end
		sOut.replace('-', ' '); //move - to space
		sOut.replace('_', ' '); //move _ to space
		sOut = sOut.toUpperCase();
		return sOut;
	}
	
 /* (non-Javadoc)
     * @see org.jajuk.base.ItemManager#getIdentifier()
     */
    public String getIdentifier() {
        return XML_STYLES;
    }

}