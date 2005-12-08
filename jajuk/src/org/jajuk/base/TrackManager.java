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
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

import org.jajuk.i18n.Messages;
import org.jajuk.util.ConfigurationManager;
import org.jajuk.util.MD5Processor;
import org.jajuk.util.error.JajukException;
import org.jajuk.util.error.NoneAccessibleFileException;
import org.jajuk.util.log.Log;

/**
 * Convenient class to manage Tracks
 * @author Bertrand Florat 
 * @created 17 oct. 2003
 * @TODO Refactor this error detection system (isChangePbm)
 */
public class TrackManager extends ItemManager implements Observer{
    /**Self instance*/
    private static TrackManager singleton;
    
     /**Unmounted tracks flag*/
    boolean bChangePbm = false;
    
    /**Sorted tracks (perfs)*/
    TreeSet<Track> sortedTracks = new TreeSet();
  
    /**
     * No constructor available, only static access
     */
    private TrackManager() {
        super();
        //---register properties---
        //ID
        registerProperty(new PropertyMetaInformation(XML_ID,false,true,false,false,false,String.class,null,null));
        //Name
        registerProperty(new PropertyMetaInformation(XML_NAME,false,true,true,true,false,String.class,null,null));
        //Album
        registerProperty(new PropertyMetaInformation(XML_ALBUM,false,true,true,true,true,String.class,null,null));
        //Style
        registerProperty(new PropertyMetaInformation(XML_STYLE,false,true,true,true,true,String.class,null,null));
        //Author
        registerProperty(new PropertyMetaInformation(XML_AUTHOR,false,true,true,true,true,String.class,null,null));
        //Length
        registerProperty(new PropertyMetaInformation(XML_TRACK_LENGTH,false,true,true,false,false,Long.class,null,null));
        //Type
        registerProperty(new PropertyMetaInformation(XML_TRACK_TYPE,false,true,true,false,false,Long.class,null,null));
        //Year
        registerProperty(new PropertyMetaInformation(XML_TRACK_YEAR,false,true,true,true,true,Long.class,null,0));
        //Rate
        registerProperty(new PropertyMetaInformation(XML_TRACK_RATE,false,false,true,true,true,Long.class,null,0));
        //Files
        registerProperty(new PropertyMetaInformation(XML_FILES,false,false,true,false,false,String.class,null,null));
        //Hits
        registerProperty(new PropertyMetaInformation(XML_TRACK_HITS,false,false,true,false,false,Long.class,null,0));
        //Addition date
        registerProperty(new PropertyMetaInformation(XML_TRACK_ADDED,false,false,true,false,false,Date.class,new SimpleDateFormat(ADDITION_DATE_FORMAT),null));
        //Comment
        registerProperty(new PropertyMetaInformation(XML_TRACK_COMMENT,false,false,true,true,true,String.class,null,null));
        //Track order
        registerProperty(new PropertyMetaInformation(XML_TRACK_ORDER,false,true,true,true,false,Long.class,null,null));
        //---subscriptions---
        ObservationManager.register(EVENT_FILE_NAME_CHANGED,this);
    }
    
    /**
     * @return singleton
     */
    public static TrackManager getInstance(){
        if (singleton == null){
            singleton = new TrackManager();
        }
        return singleton;
    }
        
    /**
     * Register an Track
     */
    public synchronized Track registerTrack(String sName, Album album, Style style, Author author, long length, long lYear, long lOrder,Type type) {
            String sId = getHashcode(sName, album, style, author, length, lYear,lOrder,type);
        return registerTrack(sId, sName, album, style, author, length, lYear, lOrder, type);
    }
   
    
    /**
     * Return hashcode for a track
     * @param track
     * @return
     */
    public static String getHashcode(String sName, Album album, Style style, Author author, long length, long lYear, long lOrder,Type type){
        StringBuffer sb = new StringBuffer(200);
        sb.append(style.getName()).append(author.getName()).append(album.getName())
        .append(lYear).append(length).append(lYear).append(lOrder).append(type.getName()).append(sName);
        return MD5Processor.hash(sb.toString());
    }
    
    /**
     * Register an Track with a known id
     * 
     * @param sName
     */
    public synchronized Track registerTrack(String sId, String sName, Album album, Style style, Author author, long length, long lYear, long lOrder, Type type) {
        if (hmItems.containsKey(sId)) {
            return (Track)hmItems.get(sId);
        }
        Track track = null;
        if (hmIdSaveItems.containsKey(sId) && !ConfigurationManager.getBoolean(CONF_TAGS_DEEP_SCAN)){
            track = (Track)hmIdSaveItems.get(sId);
        }
        else{
            track = new Track(sId, sName, album, style, author, length, lYear, lOrder, type);
            saveItem(track);
        }
        hmItems.put(sId, track);
        sortedTracks.add(track);
        return track;
    }
    
    /**
     * Change a track album 
     * @param old track
     * @param new album name
     * @param filter files we want to deal with
     * @return new track
     *
     */
    public synchronized Track changeTrackAlbum(Track track,String sNewAlbum,HashSet filter)  throws JajukException{
        //check there is actually a change
        if (track.getAlbum().getName2().equals(sNewAlbum)){
            return track;
        }
        ArrayList<File> alReady = null;
        //check if files are accessible
        alReady = track.getReadyFiles(filter); 
        if (alReady.size() == 0){
            throw new NoneAccessibleFileException("010"); //$NON-NLS-1$
        }
        //change tag in files
        for (File file:alReady){
            Tag tag = new Tag(file.getIO());
            tag.setAlbumName(sNewAlbum);
            tag.commit();
        }
        //register the new album
        Album newAlbum = AlbumManager.getInstance().registerAlbum(sNewAlbum);
        Track newTrack = registerTrack(track.getName(),newAlbum,track.getStyle(),track.getAuthor(),track.getLength(),
            track.getYear(),track.getOrder(),track.getType());
        postChange(track,newTrack,filter);
        AlbumManager.getInstance().cleanup(track.getAlbum()); //remove this album if no more references
        return newTrack;
    }
    
    /**
     * Change a track author 
     * @param old track
     * @param new author name
     * @param filter files we want to deal with
     * @return new track
     */
    public synchronized Track changeTrackAuthor(Track track,String sNewAuthor,HashSet filter)  throws JajukException{
        //check there is actually a change
        if (track.getAuthor().getName2().equals(sNewAuthor)){
            return track;
        }
        ArrayList<File> alReady = null;
        //check if files are accessible
        alReady = track.getReadyFiles(filter); 
        if (alReady.size() == 0){
            throw new NoneAccessibleFileException("010"); //$NON-NLS-1$
        }
        //change tag in files
        for (File file:alReady){
            Tag tag = new Tag(file.getIO());
            tag.setAuthorName(sNewAuthor);
            tag.commit();
        }
        //register the new item
        Author newAuthor = AuthorManager.getInstance().registerAuthor(sNewAuthor);
        Track newTrack = registerTrack(track.getName(),track.getAlbum(),track.getStyle(),
            newAuthor,track.getLength(),track.getYear(),track.getOrder(),track.getType());
        postChange(track,newTrack,filter);
        AuthorManager.getInstance().cleanup(track.getAuthor()); //remove this item if no more references
        return newTrack;
    }
    
    /**
     * Change a track style 
     * @param old item
     * @param new item name
     * @param filter files we want to deal with
     * @return new track
     */
    public synchronized Track changeTrackStyle(Track track,String sNewStyle,HashSet filter) throws JajukException{
        //check there is actually a change
        if (track.getStyle().getName2().equals(sNewStyle)){
            return track;
        }
        ArrayList<File> alReady = null;
        //check if files are accessible
        alReady = track.getReadyFiles(filter); 
        if (alReady.size() == 0){
            throw new NoneAccessibleFileException("010"); //$NON-NLS-1$
        }
        //change tag in files
        for (File file:alReady){
            Tag tag = new Tag(file.getIO());
            tag.setStyleName(sNewStyle);
            tag.commit();
        }
        //register the new item
        Style newStyle = StyleManager.getInstance().registerStyle(sNewStyle);
        Track newTrack = registerTrack(track.getName(),track.getAlbum(),newStyle,
            track.getAuthor(),track.getLength(),track.getYear(),track.getOrder(),track.getType());
        postChange(track,newTrack,filter);
        StyleManager.getInstance().cleanup(track.getStyle()); //remove this item if no more references
        return newTrack;
    }
    
    /**
     * Change a track year 
     * @param old item
     * @param new item name
     * @param filter files we want to deal with
     * @return new track or null if wronf format
     */
    public synchronized Track changeTrackYear(Track track,long lNewItem,HashSet filter)  throws JajukException{
        //check there is actually a change
        if (track.getYear() == lNewItem){
            return track;
        }
        if (lNewItem <0 || lNewItem > 10000){
            Messages.showErrorMessage("137"); //$NON-NLS-1$
            throw new JajukException("137"); //$NON-NLS-1$
        }
        ArrayList<File> alReady = null;
        //check if files are accessible
        alReady = track.getReadyFiles(filter); 
        if (alReady.size() == 0){
            throw new NoneAccessibleFileException("010"); //$NON-NLS-1$
        }
        //change tag in files
        for (File file:alReady){
            Tag tag = new Tag(file.getIO());
            tag.setYear(lNewItem);
            tag.commit();
        }
        //Register new item
        Track newTrack = registerTrack(track.getName(),track.getAlbum(),track.getStyle(),
            track.getAuthor(),track.getLength(),lNewItem,track.getOrder(),track.getType());
        postChange(track,newTrack,filter);
        return newTrack;
    }
    
    /**
     * Change a track comment 
     * @param old item
     * @param new item name
     * @param filter files we want to deal with
     * @return new track or null if wronf format
     */
    public synchronized Track changeTrackComment(Track track,String sNewItem,HashSet filter)  throws JajukException{
        //check there is actually a change
        if (track.getComment().equals(sNewItem)){
            return track;
        }
        ArrayList<File> alReady = null;
        //check if files are accessible
        alReady = track.getReadyFiles(filter); 
        if (alReady.size() == 0){
            throw new NoneAccessibleFileException("010"); //$NON-NLS-1$
        }
        //change tag in files
        for (File file:alReady){
            Tag tag = new Tag(file.getIO());
            tag.setComment(sNewItem);
            tag.commit();
        }
        track.setComment(sNewItem);
        return track;
    }
    
    
    /**
     * Change a track rate 
     * @param old item
     * @param new item name
     * @return new track or null if wrong format
     */
    public synchronized Track changeTrackRate(Track track,long lNew)  throws JajukException{
        //check there is actually a change
        if (track.getRate() == lNew){
            return track;
        }
        //check format
        if (lNew <0 ){
            Messages.showErrorMessage("137"); //$NON-NLS-1$
            throw new JajukException("137"); //$NON-NLS-1$
        }
        track.setRate(lNew);
        return track;
    }
    
    /**
     * Change a track order 
     * @param old item
     * @param new item order
     * @param filter files we want to deal with
     * @return new track or null if wronf format
     */
    public synchronized Track changeTrackOrder(Track track,long lNewOrder,HashSet filter) throws JajukException{
        //check there is actually a change
        if (track.getOrder() == lNewOrder){
            return track;
        }
        //check format
        if (lNewOrder <0){
            Messages.showErrorMessage("137"); //$NON-NLS-1$
            return null;
        }
        ArrayList<File> alReady = null;
        //check if files are accessible
        alReady = track.getReadyFiles(filter); 
        if (alReady.size() == 0){
            throw new NoneAccessibleFileException("010"); //$NON-NLS-1$
        }
        //change tag in files
        for (File file:alReady){
            Tag tag = new Tag(file.getIO());
            tag.setOrder(lNewOrder);
            tag.commit();
        }
        Track newTrack = registerTrack(track.getName(),track.getAlbum(),track.getStyle(),
            track.getAuthor(),track.getLength(),track.getYear(),lNewOrder,track.getType());
        postChange(track,newTrack,filter);
        return newTrack;
    }
    
    /**
     * Change a track name 
     * @param old item
     * @param new item name
     * @param filter files we want to deal with
     * @return new track
     */
    public synchronized Track changeTrackName(Track track,String sNewItem,HashSet filter) throws JajukException{
        //check there is actually a change
        if (track.getName().equals(sNewItem)){
            return track;
        }
        ArrayList<File> alReady = null;
        //check if files are accessible
        alReady = track.getReadyFiles(filter); 
        if (alReady.size() == 0){
            throw new NoneAccessibleFileException("010"); //$NON-NLS-1$
        }
        //change tag in files
        for (File file:alReady){
            Tag tag = new Tag(file.getIO());
            tag.setTrackName(sNewItem);
            tag.commit();
        }
        Track newTrack = registerTrack(sNewItem,track.getAlbum(),track.getStyle(),
            track.getAuthor(),track.getLength(),track.getYear(),track.getOrder(),track.getType());
        postChange(track,newTrack,filter);
        return newTrack;
    }

    
    private void updateFilesReferences(Track oldTrack,Track newTrack,HashSet filter){
        //Reset files property before adding new files
        for (File file:oldTrack.getReadyFiles(filter)){
            file.setTrack(newTrack);//set new track for the changed file
            newTrack.addFile(file); //add changed file
            oldTrack.removeFile(file); //remove file from old track
        }
    }
    
    
    private void postChange(Track track,Track newTrack,HashSet filter){
        //re apply old properties from old item
        newTrack.cloneProperties(track);
        //update files references
        updateFilesReferences(track,newTrack,filter);
        if (track.getFiles().size() == 0){ //normal case: old track has no more associated tracks, remove it
            removeItem(track.getId());//remove old track
            sortedTracks.remove(track);
            bChangePbm = false;
        }
        else{ // some files have not been changed because located on unmounted devices
            bChangePbm = true;
        }
    }   
         
    /**
     * Perform a track cleanup : delete useless items
     *  
     */
    public synchronized void cleanup() {
        Iterator itTracks = hmItems.values().iterator();
        while (itTracks.hasNext()) {
            Track track = (Track) itTracks.next();
            if ( track.getFiles().size() == 0){ //no associated file
                itTracks.remove();
                sortedTracks.remove(track);
                continue;
            }
            Iterator itFiles = track.getFiles().iterator();
            while (itFiles.hasNext()) {
                org.jajuk.base.File file = (org.jajuk.base.File) itFiles.next();
                if ((File)FileManager.getInstance().getItem(file.getId()) == null) { //test if the file exists in the main file repository
                    itFiles.remove();//no? remove it from the track
                }
            }
            if (track.getFiles().size() == 0) { //the track don't map anymore to any physical item, just remove it
                itTracks.remove();
                sortedTracks.remove(track);
            }
        }
    }
    
    
    /** Return sorted registred Tracks
     * It is computed only if needed. We need to compute it because
     * we don't have a sorted list (do not use a TreeMap as we need duplicates items)
     * and because it is mush simpler to make it this way
     * **/ 
    public synchronized Collection<Track> getSortedTracks() {
        //Cleanup dead tracks if needed
        if (sortedTracks.size() != hmItems.size()){
            Log.debug("** Dead tracks reference in sorted list"); //$NON-NLS-1$
            Iterator it = sortedTracks.iterator();
            while (it.hasNext()){
                Track track = (Track)it.next();
                if (!hmItems.containsKey(track.getId())){
                    it.remove();
                }
            }
        }
        return sortedTracks;
    }
    
    
    /**
     * Format the tracks names to be normalized :
     * <p>
     * -no underscores or other non-ascii characters
     * <p>
     * -no spaces at the begin and the end
     * <p>
     * -All in lower cas expect first letter of first word
     * <p>
     * exemple: "My track title"
     * 
     * @param sName
     * @return
     */
    private synchronized String format(String sName) {
        String sOut;
        sOut = sName.trim(); //supress spaces at the begin and the end
        sOut.replace('-', ' '); //move - to space
        sOut.replace('_', ' '); //move _ to space
        char c = sOut.charAt(0);
        sOut = sOut.toLowerCase();
        StringBuffer sb = new StringBuffer(sOut);
        sb.setCharAt(0, Character.toUpperCase(c));
        return sb.toString();
    }
    
    /* (non-Javadoc)
     * @see org.jajuk.base.ItemManager#getIdentifier()
     */
    public String getIdentifier() {
        return XML_TRACKS;
    }    
    
    /* (non-Javadoc)
     * @see org.jajuk.base.Observer#update(org.jajuk.base.Event)
     */
    public void update(Event event) {
        String subject = event.getSubject();
        if (EVENT_FILE_NAME_CHANGED.equals(subject)){
            Properties properties = event.getDetails();
            File fNew = (File)properties.get(DETAIL_NEW);
            File fileOld = (File)properties.get(DETAIL_OLD);
            Track track =fileOld.getTrack(); 
            track.removeFile(fileOld);
            track.addFile(fNew);
        }
    }
    
    /**
     * Get sorted tracks associated with this track
     * Note that this method must be synchronized if user changes several tag set at the same time
     * @param item
     * @return
     */
    public synchronized ArrayList getAssociatedTracks(IPropertyable item){
        ArrayList alOut = new ArrayList(100);
        Iterator it = sortedTracks.iterator();
        while (it.hasNext()){ //scan each track
            Track track = (Track)it.next();
            if ( (item instanceof Album &&  track.getAlbum().equals(item))
                    || (item instanceof Author &&  track.getAuthor().equals(item))
                    || (item instanceof Style &&  track.getStyle().equals(item)) ){
                alOut.add(track);
            }
        }
        return alOut;
    }

    public synchronized boolean isChangePbm() {
        return bChangePbm;
    }
    
}