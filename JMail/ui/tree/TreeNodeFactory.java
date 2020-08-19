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
 
package jmail.ui.tree;

import javax.mail.Session;
import javax.mail.Folder;
import jmail.options.ServerInfo;

public abstract class TreeNodeFactory
{
  public static TreeNode createNode(Object obj)
  {
    TreeNode retVal = null;
    if (obj instanceof Session)
    {
      retVal = new SessionTreeNode((Session)obj);
    }
    else if (obj instanceof ServerInfo)
    {
      retVal = new StoreTreeNode((ServerInfo)obj);
    }
    else if (obj instanceof Folder)
    {
      retVal =  new FolderTreeNode((Folder) obj);
    }
    else
    {
      retVal = new TreeNode(obj);
    }
    return retVal;
  }
}
