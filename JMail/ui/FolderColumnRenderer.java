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

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;

import jmail.ui.GUIUtils;

public class FolderColumnRenderer extends JLabel implements TableCellRenderer
{
    
	private static final Icon up;
	private static final Icon down;
	
	private static int sortedColumn = -1;
	private static boolean sortAscending = false;

	public static final void setSortOrder(int column,boolean  ascending)
  {
		sortedColumn = column;
		sortAscending = ascending;
	}
  
  public static final int getSortColumn()
  {
    return sortedColumn;
  }
  
  public static final boolean getSortAscending()
  {
    return sortAscending;
  }

	static
  {
		up = GUIUtils.createIcon("ascending");
		down = GUIUtils.createIcon("descending");
	}

	public Component getTableCellRendererComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               boolean hasFocus,
                                               int row,
                                               int column)
	{
		setText(value.toString());
    //setBorder(new EtchedBorder());
		if (sortedColumn==column )
    {
			Icon icon = sortAscending ? up : down;
			setIcon(icon);
		}
    else
    {
			setIcon(null);
		}
    
		JTableHeader header=table.getTableHeader();
		setBackground(header.getBackground());
		setForeground(header.getForeground());
		setFont(header.getFont());
		setHorizontalTextPosition(JLabel.LEFT);
    setHorizontalAlignment(JLabel.LEFT);
		setBorder(UIManager.getBorder("TableHeader.cellBorder"));
		return this;
	}

}
