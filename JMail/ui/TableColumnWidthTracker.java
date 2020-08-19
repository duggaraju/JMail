/*
 * A plugin for jEdit which implements a mail client.
 * Copyright (C) 2005  Krishna Prakash Duggaraju
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package jmail.ui;

import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

public class TableColumnWidthTracker implements TableColumnModelListener
{
  private final JTable table;
  private final String propertyName;
  
  public TableColumnWidthTracker(JTable table, String name)
  {
    this.table = table;
    propertyName = name;
  }
  
  public final void columnAdded(TableColumnModelEvent e) {} 
  public final void columnRemoved(TableColumnModelEvent e) {} 
  public final void columnMoved(TableColumnModelEvent e) {} 
  public final void columnSelectionChanged(ListSelectionEvent e) {} 
  
  public final void columnMarginChanged(ChangeEvent e)
  {
    GUIUtils.saveTableColumnGeometry(table, propertyName);
  } 
}