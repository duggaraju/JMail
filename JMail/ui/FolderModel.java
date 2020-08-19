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

import javax.swing.tree.TreePath;
import jmail.ui.tree.FolderTreeNode;
import jmail.ui.tree.TreeNode;
import jmail.ui.treetable.AbstractTreeTableModel;
import jmail.ui.treetable.TreeTableModel;

/**
 * Maps the messages in a Folder to the Swing's Table Model
 */

public class FolderModel extends AbstractTreeTableModel
{
    
  static final String[] COLUMN_NAMES = { 
			"Name",
			"Total",
			"Unread",
	}; 

  static final Class[] COLUMN_TYPES = {
    TreeTableModel.class,
    String.class,
    String.class
	}; 


  public FolderModel(TreeNode root)
  {
    super(root);
  }

  public final String getColumnName(int column)
	{
		return COLUMN_NAMES[column];
  }
    
  public final Class getColumnClass(int column)
	{
		return COLUMN_TYPES[column];
  }
    
  public final int getColumnCount()
	{
    return COLUMN_NAMES.length; 
  }

  public final boolean isLeaf(Object obj)
  {
    return ((TreeNode)obj).isLeaf();
  }
  
  public Object getValueAt(Object obj, int col)
  {
    if (obj instanceof FolderTreeNode)
    {
      FolderTreeNode folder = (FolderTreeNode)obj;
      
      switch(col)
      {
        case 0:
          return obj;
        case 1:
          return String.valueOf(folder.getTotalMessages());
        case 2:
          return String.valueOf(folder.getUnreadMessages());
      }
    }
    switch (col)
    {
      case 0:
        return obj;
      case 1:
        return "";
      case 2:
      default:
        return "";
    }
  }
  
  public int getChildCount(Object obj)
  {
    return ((TreeNode)obj).getChildCount();
  }
  
  public Object getChild(Object obj, int index)
  {
    return ((TreeNode)obj).getChildAt(index);
  }
  
  public final void update(TreePath path)
  {
    fireTreeStructureChanged(this, path.getPath() , null, null);
  }
  
  public final void update(TreeNode node)
  {
    fireTreeStructureChanged(this, node.getPath(), null, null);
  }
}
