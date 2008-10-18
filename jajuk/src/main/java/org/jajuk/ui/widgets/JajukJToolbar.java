/*
 *  Jajuk
 *  Copyright (C) 2003-2008 The Jajuk Team
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
package org.jajuk.ui.widgets;

import javax.swing.JToolBar;

/**
 * Jajuk specific toolbar : non opaque and non floatable
 */
public class JajukJToolbar extends JToolBar {

  private static final long serialVersionUID = 3947108459544670564L;

  public JajukJToolbar() {
    this(JToolBar.HORIZONTAL);
  }

  public JajukJToolbar(int i) {
    super(i);
    setFloatable(false);
    setOpaque(false);
    setRollover(true);
    setBorder(null);
  }

}