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

/**
 * Node which represents a Folder in the javax.mail apis. 
 */

public class FolderTreeNode extends TreeNode
{
    
	private int total = 0;
  private int unread = 0;
  private boolean cached = false;

  /**
   * creates a tree node that points to the particular Store.
   */
  public FolderTreeNode(Folder folder)
	{
		super(folder);
    name = folder.getName();
    updateCache();
  }

    
  /**
   * a Folder is a leaf if it cannot contain sub folders
   */
  public final boolean isLeaf()
	{
		return false;
  }
   
  /**
   * returns the folder for this node
   */
  public final Folder getFolder()
	{
		return (Folder)getUserObject();
  }
    
  protected void populateChildren() throws MessagingException
	{
    Folder[] sub = getFolder().list();
    addChildren(sub, 0);
  }


  private final void updateCache()
  {
    if (!cached)
    {
      try
      {
        Folder folder = getFolder();
        total = folder.getMessageCount();
        unread = folder.getUnreadMessageCount();
      }
      catch (MessagingException ex)
      {
        ex.printStackTrace();
      }
      cached = true;
    }
  }
  
 
	public int getTotalMessages()
	{
    updateCache();
		return total;
	}

	public int getUnreadMessages()
	{
    updateCache();
		return unread;
	}
  
  public final void delete() throws MessagingException
  {
    Folder folder = getFolder();
    if (folder.isOpen())
      folder.close(true);
    getFolder().delete(true);
    removeFromParent();
  }
  
  public final void rename(String newName) throws MessagingException
  {
    Folder oldFolder = getFolder();
    if (oldFolder.isOpen())
      oldFolder.close(false);
      
    Folder parent = oldFolder.getParent();
    Folder newFolder;
    if (parent == null)
    {
      newFolder = oldFolder.getStore().getFolder(newName);
    }
    else
    {
      newFolder = parent.getFolder(newName);      
    }
    
    oldFolder.renameTo(newFolder);
    setUserObject(newFolder);
    name = newFolder.getName();
  }
  
  public final void refresh() throws MessagingException
  {
    Folder folder = getFolder();
    int count = folder.getNewMessageCount();
    if (count == -1)
    {
      folder.open(Folder.READ_ONLY);
      count = folder.getNewMessageCount();
    }
    if (count > 0)
    {
      cached = false;
    }
  }
  
  public final void create(String folderName) throws MessagingException
  {
    Folder folder = getFolder();
    Folder newFolder = folder.getFolder(folderName);
    newFolder.create(Folder.HOLDS_FOLDERS | Folder.HOLDS_MESSAGES);
    addChild(newFolder, getChildCount());
  }
}

