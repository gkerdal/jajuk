/*
 *  Jajuk
 *  Copyright (C) 2007 The Jajuk Team
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

package org.jajuk.services.lyrics.providers;

import ext.services.network.NetworkUtils;

import java.net.MalformedURLException;
import java.net.URL;

import org.jajuk.util.Const;
import org.jajuk.util.DownloadManager;
import org.jajuk.util.log.Log;

/**
 * GenericProvider is a basic processor for web-based lyrics providers. It
 * doesn't provide fine-grained processing and simply retrieves raw text data
 * from HTML pages.
 * 
 * The GenericProvider is used as a base class by other, more fine-grained
 * specific providers.
 */
public abstract class GenericWebLyricsProvider implements ILyricsProvider {

  private String source = null;
  private String queryUrlTemplate = null;

  public GenericWebLyricsProvider(final String queryUrlTemplate) {
    this.queryUrlTemplate = queryUrlTemplate;
  }

  /*
   * Call the provider @artist non encoded artist @title non encoded title
   * @return query return or null if query fails
   */
  public String callProvider(final String artist, final String title) {
    String text = null;
    try {
      URL url = getActualURL(artist, title);
      text = DownloadManager.getTextFromCachedFile(url, getResponseEncoding());
    } catch (final Exception e) {
      Log.warn("Could not retrieve URL [" + getProviderHostname() + "]");
    }
    return text;
  }

  /**
   * Return query URL template like http://..?artist=%artist&songname=%title
   * 
   * @return query URL template like http://..?artist=%artist&songname=%title
   */
  public String getQueryURLTemplate() {
    return queryUrlTemplate;
  }

  /**
   * Return the hostname of the lyrics provider, used as unique identifier for
   * the provider
   * 
   * @return
   */
  public String getProviderHostname() {
    if (source == null) {
      try {
        source = new URL(queryUrlTemplate).getHost();
      } catch (final MalformedURLException e) {
        Log.warn("Invalid lyrics provider [" + queryUrlTemplate + "]");
      }
    }
    return source;
  }

  /**
   * Build the actual formated and valorized URL to the provider
   * 
   * @param artist
   *          the artist
   * @param title
   *          the title
   * @return URL the final url
   * 
   */
  URL getActualURL(final String artist, final String title) {
    try {
      String queryString = getQueryURLTemplate();

      queryString = queryString.replace(Const.PATTERN_AUTHOR, (artist != null) ? NetworkUtils
          .encodeString(artist) : "");
      queryString = queryString.replace(Const.PATTERN_TRACKNAME, (title != null) ? NetworkUtils
          .encodeString(title) : "");

      return new URL(queryString);
    } catch (MalformedURLException e) {
      Log.error(e);
      return null;
    }
  }
  
  /**
   * Return the URL from where the lyrics can be displayed from out of Jajuk
   * <br>
   * Note that this URL can be different from the jajuk used url for example if
   * a provider provides a web service interface (jajuk then uses the
   * corresponding URL) and a Web page (this is this URL that is returned from
   * this method)
   * 
   * @return the Web URL or null if a problem occurred
   */
  public abstract java.net.URL getWebURL(String artist, String title);

}
