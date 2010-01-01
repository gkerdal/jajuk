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
 *  $Revision$
 */

package org.jajuk.ui.helpers;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;

import org.jajuk.base.Item;
import org.jajuk.ui.widgets.IconLabel;
import org.jajuk.util.IconLoader;
import org.jajuk.util.JajukIcons;
import org.jajuk.util.Messages;

/**
 * Jajuk table model, adds identifier to model.
 */
public abstract class JajukTableModel extends DefaultTableModel {

  /** Generated serialVersionUID. */
  private static final long serialVersionUID = -7296786603161189590L;

  /** Column identifiers. */
  volatile protected List<String> idList = new ArrayList<String>(10);

  /** Rows number. */
  protected int iRowNum;

  /** Values table*. */
  protected Object[][] oValues;

  // Play icon in cache
  /** The Constant PLAY_ICON.  DOCUMENT_ME */
  protected static final ImageIcon PLAY_ICON = IconLoader.getIcon(JajukIcons.PLAY_TABLE);

  // Unmount Play icon in cache
  /** The Constant UNMOUNT_PLAY_ICON.  DOCUMENT_ME */
  protected static final ImageIcon UNMOUNT_PLAY_ICON = IconLoader.getIcon(JajukIcons.UNKNOWN);

  /** Objects. */
  protected Item[] oItems;

  /** Number of standard columns. */
  protected int iNumberStandardCols;

  /** Cell editable flag. */
  protected boolean[][] bCellEditable;

  /** Column names. */
  protected List<String> vColNames = new ArrayList<String>(10);

  /** Last value used for undo. */
  private Object oLast = null;

  /** Editable flag. */
  boolean bEditable = false;

  /** Tree selection. */
  protected Set<Item> treeSelection;

  /** DOCUMENT_ME. */
  protected IconLabel play_icon = null;

  /** DOCUMENT_ME. */
  protected IconLabel unmount_play_icon = null;

  /**
   * The Constructor.
   * 
   * @param iNumberStandardCols Number of columns of this model (without custom properties)
   */
  public JajukTableModel(int iNumberStandardCols) {
    super();

    this.iNumberStandardCols = iNumberStandardCols;
  }

  /**
   * Default constructor.
   */
  public JajukTableModel() {
    super();

    this.iNumberStandardCols = 0;
  }

  /**
   * Gets the identifier.
   * 
   * @param sColName DOCUMENT_ME
   * 
   * @return Column identifier for a given column title
   */
  public String getIdentifier(String sColName) {
    return idList.get(vColNames.indexOf(sColName));
  }

  /**
   * Return item at given position.
   * 
   * @param iRow DOCUMENT_ME
   * 
   * @return the item at
   */
  public Item getItemAt(int iRow) {
    return oItems[iRow];
  }

  /**
   * Set item at given position.
   * 
   * @param iRow DOCUMENT_ME
   * @param item DOCUMENT_ME
   */
  public void setItemAt(int iRow, Item item) {
    oItems[iRow] = item;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.DefaultTableModel#getValueAt(int, int)
   */
  @Override
  public Object getValueAt(int rowIndex, int columnIndex) {
    // We need to test this as UI may request it before table is populated
    if (oValues == null || oValues.length == 0 || rowIndex >= oValues.length) {
      return null;
    }
    return oValues[rowIndex][columnIndex];
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.DefaultTableModel#setValueAt(java.lang.Object, int, int)
   */
  @Override
  public void setValueAt(Object oValue, int rowIndex, int columnIndex) {
    oLast = oValues[rowIndex][columnIndex];
    oValues[rowIndex][columnIndex] = oValue;
    fireTableCellUpdated(rowIndex, columnIndex);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  @Override
  public int getColumnCount() {
    return vColNames.size();
  }

  /**
   * Undo last change.
   * 
   * @param rowIndex DOCUMENT_ME
   * @param columnIndex DOCUMENT_ME
   */
  public void undo(int rowIndex, int columnIndex) {
    if (oLast != null) {
      oValues[rowIndex][columnIndex] = oLast;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.DefaultTableModel#getColumnName(int)
   */
  @Override
  public String getColumnName(int column) {
    return vColNames.get(column);
  }

  /**
   * Gets the identifier.
   * 
   * @param column DOCUMENT_ME
   * 
   * @return the identifier
   */
  public String getIdentifier(int column) {
    return idList.get(column);
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.DefaultTableModel#getRowCount()
   */
  @Override
  public int getRowCount() {
    // iRowNum is set in concrete classes
    return iRowNum;
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.DefaultTableModel#isCellEditable(int, int)
   */
  @Override
  public boolean isCellEditable(int rowIndex, int columnIndex) {
    return bEditable && bCellEditable[rowIndex][columnIndex];
  }

  /*
   * (non-Javadoc)
   * 
   * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
   */
  @Override
  public Class<? extends Object> getColumnClass(int columnIndex) {
    Object o = getValueAt(0, columnIndex);
    if (o != null) {
      return o.getClass();
    } else {
      return null;
    }
  }

  /**
   * Fill model with data using an optional filter property and pattern.
   * 
   * @param sProperty Property (column) to filter
   * @param sPattern pattern
   * @param columnsToShow List of elements to show in the table (like files,hits...). This
   * is useful for models for memory performances as model doesn't fill
   * values for hidden columns
   */
  public abstract void populateModel(String sProperty, String sPattern, List<String> columnsToShow);

  /**
   * Fill model with data.
   * 
   * @param columnsToShow DOCUMENT_ME
   */
  public synchronized void populateModel(List<String> columnsToShow) {
    populateModel(null, null, columnsToShow);
  }

  /**
   * Set this model editable state.
   * 
   * @param b whether model is editable or not
   */
  public void setEditable(boolean b) {
    this.bEditable = b;
  }

  /**
   * Gets the tree selection.
   * 
   * @return the tree selection
   */
  public Set<Item> getTreeSelection() {
    return this.treeSelection;
  }

  /**
   * Sets the tree selection.
   * 
   * @param treeSelection the new tree selection
   */
  public void setTreeSelection(Set<Item> treeSelection) {
    this.treeSelection = treeSelection;
  }

  /**
   * Gets the icon.
   * 
   * @param unmount DOCUMENT_ME
   * 
   * @return the icon
   */
  protected IconLabel getIcon(boolean unmount) {
    if (!unmount) {
      if (play_icon == null) {
        play_icon = new IconLabel(PLAY_ICON, "", null, null, null, Messages
            .getString("TracksTableView.7"));
      }
      return play_icon;
    } else {
      if (unmount_play_icon == null) {
        unmount_play_icon = new IconLabel(UNMOUNT_PLAY_ICON, "", null, null, null, Messages
            .getString("TracksTableView.7")
            + Messages.getString("AbstractTableView.10"));
      }
      return unmount_play_icon;
    }
  }
}
