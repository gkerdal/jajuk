/*
 *  Jajuk
 *  Copyright (C) 2003 bflorat
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

package org.jajuk.ui.views;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import javax.swing.JMenuItem;

import org.jajuk.base.PlaylistFile;
import org.jajuk.base.PlaylistFileManager;
import org.jajuk.i18n.Messages;
import org.jajuk.ui.Observer;
import org.jajuk.ui.PlaylistFileItem;

/**
 * Shows playlist files
 * <p>Physical perspective
 *  * <p>Singleton
 * @author     bflorat
 * @created   29 dec. 2003
 */
public class PhysicalPlaylistRepositoryView extends AbstractPlaylistRepositoryView implements Observer,ActionListener{
	
	/**Self instance*/
	static PhysicalPlaylistRepositoryView ppr;
	
	/**Selected playlist file item */
	private static PlaylistFileItem plfiSelected;
	
	/**Return self instance*/
	public static synchronized PhysicalPlaylistRepositoryView getInstance(){
		if (ppr == null){
			ppr = new PhysicalPlaylistRepositoryView();
		}
		return ppr;
	}	
	/**
	 * Constructor
	 */
	public PhysicalPlaylistRepositoryView() {
		ppr = this;
	}
	
	/* (non-Javadoc)
	 * @see org.jajuk.ui.IView#display()
	 */
	public void display(){
		//commons
		super.display();
		//specifics
		jmiSaveAs = new JMenuItem(Messages.getString("PhysicalPlaylistRepositoryView.2"));  //$NON-NLS-1$
		jmiSaveAs.addActionListener(this);
		jpmenu.add(jmiSaveAs);
		
	}
	
	/* (non-Javadoc)
	 * @see org.jajuk.ui.IView#getDesc()
	 */
	public String getDesc() {
		return Messages.getString("PhysicalPlaylistRepositoryView.6");	 //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.jajuk.ui.IView#getViewName()
	 */
	public String getViewName() {
		return "org.jajuk.ui.views.PhysicalPlaylistRepositoryView"; //$NON-NLS-1$
	}
	
	
	/**
	 * Create playlists from collection 
	 */
	void populate(){
		super.populate();
		//normal playlists
		ArrayList al = PlaylistFileManager.getPlaylistFiles();
		Collections.sort(al);
		Iterator it = al.iterator();
		while ( it.hasNext()){
			PlaylistFile plf = (PlaylistFile)it.next();
			if ( !plf.isReady()){  //don't show playlist files on unmounted devices
				continue;
			}
			PlaylistFileItem plfi = new PlaylistFileItem(PlaylistFileItem.PLAYLIST_TYPE_NORMAL,ICON_PLAYLIST_NORMAL,plf,plf.getName());
			plfi.addMouseListener(ma);
			plfi.setToolTipText(plf.getName());
			jpRoot.add(plfi);
		}
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		super.actionPerformed(ae);
		if(ae.getSource() == jmiSaveAs){
			//TBI
		}
	}
}
