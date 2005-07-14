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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import org.jajuk.i18n.Messages;
import org.jajuk.util.ConfigurationManager;
import org.jajuk.util.Util;

/**
 *  A track
 *<p> Logical item
 * @Author   Bertrand Florat
 * @created    17 oct. 2003
 */
public class Track extends PropertyAdapter implements Comparable{

	/**Track album**/
	private Album album;
	/**Track style*/
	private Style style;
	/**Track author in sec*/
	private Author author;
	/**Track length*/
	private long length;
	/**Track year*/
	private String sYear;
	/**Track rate*/
	private long lRate;
	/**Track type*/
	private Type type;
	/**Track hits number*/
	private int iHits;
	/**Track addition date format:YYYYMMDD*/
	private String sAdditionDate;
	/**Track associated files*/
	private ArrayList alFiles = new ArrayList(1);
	/**Track compare hash for perfs*/
	private String sHashCompare;
	/** Number of hits for current jajuk session */
	private int iSessionHits = 0;
    /**Date format*/
    private static SimpleDateFormat sdf= new SimpleDateFormat(DATE_FILE);
    
	/**
	 *  Track constructor
	 * @param sId
	 * @param sName
	 * @param album
	 * @param style
	 * @param author
	 * @param length
	 * @param sYear
	 * @param type
	 * @param sAdditionDate
	 */
	public Track(String sId,String sName,Album album,Style style,Author author,long length,String sYear,Type type) {
        super(sId,sName);
            //album
        	this.album = album;
            alConstructorElements.add(XML_ALBUM);
            setProperty(XML_ALBUM,album.getId());
            //style
            this.style = style;
            alConstructorElements.add(XML_STYLE);
            setProperty(XML_STYLE,style.getId());
            //author
            this.author = author;
            setProperty(XML_AUTHOR,author.getId());
            alConstructorElements.add(XML_AUTHOR);
            //Length
            this.length = length;
            setProperty(XML_TRACK_LENGTH,Long.toString(length));
            alConstructorElements.add(XML_TRACK_LENGTH);
            //Type
            this.type = type;
            setProperty(XML_TYPE,type.getId());
            alConstructorElements.add(XML_TYPE);
            //Year
            this.sYear = sYear;
            setProperty(XML_TRACK_YEAR,sYear);
            alConstructorElements.add(XML_TRACK_YEAR);
            //Rate
            this.lRate = 0;
            setProperty(XML_TRACK_RATE,Long.toString(lRate));
            setProperty(XML_FILES,null); //need this to respect attributes order
            //Hits
            this.iHits = 0;
            setProperty(XML_TRACK_HITS,Integer.toString(iHits));
            //Addition date
            this.sAdditionDate = sdf.format(new Date());
            setProperty(XML_TRACK_ADDED,sAdditionDate);
            //Hashcode
            this.sHashCompare = new StringBuffer(style.getName2()).append(author.getName2()).append(album.getName2()).append(sName).toString();
    }
        
	
	
	/**
	 * toString method
	 */
	public String toString() {
		String sOut = "Track[ID="+sId+" Name=" + getName() + " "+album+" "+style+" "+author+" Length="+length+" Year="+sYear+" Rate="+lRate+" "+type+" Hits="+iHits+" Addition date="+sAdditionDate+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$ //$NON-NLS-9$ //$NON-NLS-10$ //$NON-NLS-11$ //$NON-NLS-12$
		for (int i=0;i<alFiles.size();i++){
			sOut += '\n'+alFiles.get(i).toString();
		}
		return sOut; 
	}

	/**
	 *Alphabetical comparator used to display ordered lists of tracks
	 *@param other track to be compared
	 *@return comparaison result 
	 */
	public int compareTo(Object o){
		Track otherTrack = (Track)o;
		return  sHashCompare.compareToIgnoreCase(otherTrack.getHashCompare());
	}
	
	/**
	 * @return
	 */
	public Album getAlbum() {
		return album;
	}

	/**
	 * @return
	 */
	public ArrayList getFiles() {
		return alFiles;
	}

	/**
	 * Get additionned size of all files this track map to
	 * @return the total size
	 */
	public long getTotalSize(){
		long l = 0;
		Iterator it = alFiles.iterator();
		while ( it.hasNext()){
			File file = (File)it.next();
			l += file.lSize;
		}
		return l;
	}
	
	
    /**
     * @param bHideUnmounted : get even unmounted files?
     * @return best file to play for this track
     */
    public File getPlayeableFile(boolean bHideUnmounted) {
        File file = getPlayeableFile();
        if (file == null && !bHideUnmounted){
            file = (File)getFiles().get(0); //take the first file we find
        }
        return file;
    }
    
    /**
	 * @return best file to play for this track
	 */
	public File getPlayeableFile() {
		File fileOut = null;
		ArrayList alMountedFiles = new ArrayList(2);
		//firstly, keep only mounted files
		Iterator it = alFiles.iterator();
		while ( it.hasNext()){
			File file = (File)it.next();
			if (file.isReady()){
				alMountedFiles.add(file);
			}
		}
		//then keep best quality
		if (alMountedFiles.size() > 0){
			it = alMountedFiles.iterator();
			fileOut = (File)it.next();  //for the moment, the out file is the first found
			while ( it.hasNext()){
				File file = (File)it.next();
				int iQuality = 0;
				int iQualityOut = 0; //quality for out file
				try {
					iQuality = Integer.parseInt(file.getQuality());
				}
				catch(NumberFormatException nfe){}//quality string can be something like "error", in this case, we considere quality=0
				try{
					iQualityOut = Integer.parseInt(fileOut.getQuality());
				}
				catch(NumberFormatException nfe){}//quality string can be something like "error", in this case, we considere quality=0
				
				if (iQuality > iQualityOut){
					fileOut = file;
				}
			}
		}
		return fileOut;
	}

	
	/**
	 * @return
	 */
	public int getHits() {
		return iHits;
	}

	/**
	 * @return
	 */
	public String getYear() {
		return sYear;
	}
    
	/**
     * Return year, dealing with unkwnown for any language
     * @return year
     *  */
    public String getYear2() {
        String sOut = getYear();
        if (sOut.equals(UNKNOWN_YEAR)){ 
            sOut = Messages.getString(UNKNOWN_YEAR); 
        }
        return sOut;
    }

	/**
	 * @return length in sec
	 */
	public long getLength() {
		return length;
	}

	/**
	 * @return
	 */
	public long getRate() {
		return lRate;
	}

	/**
	 * @return
	 */
	public String getAdditionDate() {
		return sAdditionDate;
	}

	/**
	 * @return
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * Equal method to check two tracks are identical
	 * @param otherTrack
	 * @return
	 */
	public boolean equals(Object otherTrack){
		return this.getId().equals(((Track)otherTrack).getId() );
	}	
	
	/**
	 * Track hashcode ( used by the equals method )
	 */
	public int hashCode(){
		return getId().hashCode();
	}
	
	
	/**
	 * @return
	 */
	public Author getAuthor() {
		return author;
	}

	/**
	 * @return
	 */
	public Style getStyle() {
		return style;
	}
	
	/**
	 * Add an associated file 
	 * @param file
	 */
	public void addFile(File file){
		if (!alFiles.contains(file)){
			alFiles.add(file);	
			String sFiles = file.getId();
			if (this.containsProperty(XML_FILES)){ //already some files 
				sFiles += ","+getValue(XML_FILES);
			}
			setProperty(XML_FILES,sFiles);
		}
	}
	
	/**
	 * Remove an associated file 
	 * @param file
	 */
	public void removeFile(File file){
		alFiles.remove(file);	
	}


	/**
	 * @param hits The iHits to set.
	 */
	public void setHits(int hits) {
		this.iHits = hits;
        setProperty(XML_TRACK_HITS,Integer.toString(hits));
	}
	
		public void incHits() {
			setHits(getHits()+1);
		}


	/**
	 * @param rate The lRate to set.
	 */
	public void setRate(long rate) {
		this.lRate = rate;
        setProperty(XML_TRACK_RATE,Long.toString(rate));
	}

	

	/**
	 * @param additionDate The sAdditionDate to set.
	 */
	public void setAdditionDate(String additionDate) {
		this.sAdditionDate = additionDate;
        setProperty(XML_TRACK_ADDED,additionDate);
	}

	

	/**
	 * @return Returns the sHashCompare.
	 */
	public String getHashCompare() {
		return sHashCompare;
	}
	
	/**
	 * @return Returns the iSessionHits.
	 */
	public int getSessionHits() {
		return iSessionHits;
	}

	/**
	 * @param sessionHits The iSessionHits to inc.
	 */
	public void incSessionHits() {
		iSessionHits ++;
	}
	
	/**
	 * Return whether this item should be hidden with hide option
	 * @return whether this item should be hidden with hide option
	 */
	public boolean shouldBeHidden(){
		if (getPlayeableFile() != null
			 ||ConfigurationManager.getBoolean(CONF_OPTIONS_HIDE_UNMOUNTED) == false){ //option "only display mounted devices "
			return false;
		}
		return true;
	}

    /* (non-Javadoc)
     * @see org.jajuk.base.IPropertyable#getIdentifier()
     */
    public String getIdentifier() {
        return XML_TRACK;
    }
   

     /**
     * @param comment
     */
    public void setComment(String sComment) {
        setProperty(XML_COMMENT,sComment);
        Iterator it = alFiles.iterator();
        while (it.hasNext()){
            File file = (File)it.next();
            Tag tag = new Tag(file.getIO());
            tag.setComment(sComment);
            tag.commit();
        }
    }
  
    /**
     * Get item description
     */
    public String getDesc(){
        return Util.formatPropertyDesc(Messages.getString("Item_Track")+" : "+getName());
    }

    /* (non-Javadoc)
     * @see org.jajuk.base.IPropertyable#isPropertyEditable()
     */
    public boolean isPropertyEditable(String sProperty){
        if (XML_ID.equals(sProperty)){
            return false;
        }
        else if (XML_NAME.equals(sProperty)){
            return true;
        }
        else if (XML_ALBUM.equals(sProperty)){
            return true;
        }
        else if (XML_STYLE.equals(sProperty)){
            return true;
        }
        else if (XML_AUTHOR.equals(sProperty)){
            return true;
        }
        else if (XML_TRACK_LENGTH.equals(sProperty)){
            return false;
        }
        else if (XML_TYPE.equals(sProperty)){
            return false;
        }
        else if (XML_TRACK_YEAR.equals(sProperty)){
            return true;
        }
        else if (XML_TRACK_RATE.equals(sProperty)){
            return true;
        }
        else if (XML_FILES.equals(sProperty)){
            return false;
        }
        else if (XML_TRACK_HITS.equals(sProperty)){
            return false;
        }
        else if (XML_EXPANDED.equals(sProperty)){
            return true;
        }
        else if (XML_TRACK_ADDED.equals(sProperty)){
            return false;
        }
        else{
            return true;
        }
    }
    
    
/* (non-Javadoc)
     * @see org.jajuk.base.IPropertyable#getHumanValue(java.lang.String)
     */
    public String getHumanValue(String sKey){
        if (XML_ALBUM.equals(sKey)){
            return ((Album)AlbumManager.getInstance().getItem(getValue(sKey))).getName2();
        }
        else if (XML_AUTHOR.equals(sKey)){
            return ((Author)AuthorManager.getInstance().getItem(getValue(sKey))).getName2();
        }
        else if (XML_STYLE.equals(sKey)){
            return ((Style)StyleManager.getInstance().getItem(getValue(sKey))).getName2();
        }
        else if (XML_TRACK_LENGTH.equals(sKey)){
            return Util.formatTimeBySec(length,false);
        }
        else if (XML_TYPE.equals(sKey)){
            return ((Type)TypeManager.getInstance().getItem(getValue(sKey))).getName();
        }
        else if (XML_TRACK_YEAR.equals(sKey)){
            return getYear2();
        }
        else if (XML_FILES.equals(sKey)){
            StringBuffer sbOut = new StringBuffer();
            Iterator it = alFiles.iterator();
            while (it.hasNext()){
                File file = (File)it.next();
                sbOut.append(file.getAbsolutePath()+",");
            }
            return sbOut.substring(0,sbOut.length()-1); //remove last ','
        }
        else if (XML_TRACK_ADDED.equals(sKey)){
            return sAdditionDate.substring(0,4)+"/"+sAdditionDate.substring(4,6)+"/"+sAdditionDate.substring(6,8);
        }
        else if (XML_ANY.equals(sKey)){
            return getAny();
        }
        else{//default
            return getValue(sKey);
        }
    }
    
    /* (non-Javadoc)
     * @see org.jajuk.base.IPropertyable#getAny()
     */
    public String getAny(){
        if (bNeedRefresh){
            //rebuild any
            StringBuffer sb  = new StringBuffer();
            Track track = (Track)this;
            sb.append(track.getName());
            sb.append(track.getStyle().getName2());
            sb.append(track.getAuthor().getName2());
            sb.append(track.getAlbum().getName2());
            sb.append(track.getLength());
            sb.append(track.getRate());
            sb.append(track.getValue(XML_COMMENT));
            //custom properties now
            Iterator it = TrackManager.getInstance().getCustomProperties().iterator();
            while (it.hasNext()){
                sb.append((String)it.next());
            }
            this.sAny = sb.toString();
            bNeedRefresh = false;
        }
        return this.sAny;
    }
}
