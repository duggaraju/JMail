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

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import jmail.options.ServerInfo;

/**
 * Node which represents a Store in the javax.mail apis. 
 */

public class StoreTreeNode extends TreeNode
{
  private Store store;
  
  public StoreTreeNode(ServerInfo info)
  {
    super(info);
    name = info.getName();
  }
  
  public final void connect() throws MessagingException
  {
    if (store == null)
    {
      Session session = (Session)getParentObject();
      ServerInfo info = (ServerInfo)getUserObject();
      URLName url = new URLName(info.getURL());
      store = session.getStore(url);
    }
    if (!store.isConnected())
      store.connect();
  }
  
  public final void disconnect() throws MessagingException
  {
    if (store != null && store.isConnected())
    {
      store.close();
    }
    removeAllChildren();
    loaded = false;
  }

  public final boolean isLeaf()
	{
		return false;
  }
   
  /**
   * Load the folders for this store.
   */
  protected void populateChildren() throws MessagingException
  {
      connect();
      
      Folder[] sub = store.getDefaultFolder().list();
      addChildren(sub, 0);
  }

}

