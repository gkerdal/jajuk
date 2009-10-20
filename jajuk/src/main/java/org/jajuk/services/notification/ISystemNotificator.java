/*
 *  Jajuk
 *  Copyright (C) 2003-2009 The Jajuk Team
 *  http://jajuk.info
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
 *  $Revision: 3132 $
 */
package org.jajuk.services.notification;

/**
 * System notification in Jajuk means informing the user about things like newly
 * started tracks via a system specific notification mechanism.
 * 
 * On Windows the System Tray can take care of this.
 * 
 * On Linux, especially Linux there are more sophisticated notification
 * mechanisms available which can be used as well.
 * 
 * Base interface for multiple different notification services that we can use.
 */
public interface ISystemNotificator {
  /**
   * Indicates if this notificator is available on the current platform.
   * 
   * @return true if this notificator can display notifications on this
   *         installation, false otherwise.
   */
  boolean isAvailable();

  /**
   * Use the provided information to display a notification using this
   * notificator.
   * 
   * This method should only be called if @link isAvailable() returns true!
   * 
   * @param title
   *          The title of the notification, usually what happens, e.g.
   *          "Now playing".
   * @param text
   *          The text of the notification, e.g. while title started to play
   *          now.
   */
  void notify(String title, String text);
}
