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

import java.util.Date;

import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;

import javax.swing.table.AbstractTableModel;

/**
 * Maps the messages in a Folder to the Swing's Table Model
 */

public class MessageListModel extends AbstractTableModel
{
    
    private Folder	folder;
    private Message[]	messages;
    private MessageCache[] cache;
    private final StatusListener listener;
    
    public MessageListModel(StatusListener listener)
    {
      this.listener = listener;
    }

    static final String[] COLUMN_NAMES = { 
      "Flags",
			"Date",
			"From",
			"Subject",
			"Size"
	}; 

    static final Class[] COLUMN_TYPES = {
      Flags.class,
			Date.class, 
			Address.class,
			String.class,
			Integer.class
	}; 

  public void setFolder(Folder what)
	{
    if (what != null && what == folder)
    {
      return;
    }
    
    try
    {
      if (folder != null && folder.isOpen() )
        folder.close(true);
        
      folder = what;
      if (folder != null)
      {
          refresh();
      }
      else
      {
        messages = null;
        cache = null;
        fireTableDataChanged();
      }
    }
    catch(MessagingException me)
    {
      GUIUtils.showErrorMessage(me);
    }
  }
  
  public final void refresh() throws MessagingException
  {
    if (folder != null)
    {
      listener.setStatus("Loading messages from " + folder);
      final Worker worker = new Worker();
      worker.start();
    }
  }
  
  private final class Worker extends SwingWorker
  {
    public final Object construct()
    {
      try
      {
        if (!folder.isOpen())
          folder.open(Folder.READ_ONLY);
        messages = folder.getMessages();
        
        FetchProfile profile = new FetchProfile();
        profile.add(FetchProfile.Item.ENVELOPE);
        profile.add(FetchProfile.Item.FLAGS);
        
        folder.fetch(messages, profile);
        cache = new MessageCache[messages.length];      
      }
      catch(MessagingException me)
      {
        GUIUtils.showErrorMessage(me);
      }
      return null;
    }
    
    public final void finished()
    {
      fireTableDataChanged();      
      listener.clearStatus();      
    }
  }
    
  public final Message getMessage(int which)
	{
		return messages[which];
  }

    //---------------------
    // Implementation of the TableModel methods
    //---------------------

  public String getColumnName(int column)
	{
    return COLUMN_NAMES[column];
  }
    
  public Class getColumnClass(int column)
	{
		return COLUMN_TYPES[column];
  }
    

  public int getColumnCount()
	{
    return COLUMN_NAMES.length; 
  }

  public int getRowCount()
	{
		if (messages == null)
		    return 0;
    return messages.length;
  }
 
  public Object getValueAt(int aRow, int aColumn)
	{
    MessageCache what = getCachedData(aRow);
    Object retValue = null;
    
		if (what != null)
    {
      switch(aColumn)
      {
        case 0:
          return what.flags;
        case 1:
          retValue = what.date;
          break;
        case 2:
          retValue = what.from;
          break;
        case 3:
          retValue = what.subject;
          break;
        case 4:
          retValue = what.size;
          break;
      }
    }
    else
    {
      switch(aColumn)
      {
        case 0:
          return null;
        case 1:	// date
        case 2: // From		String[] what = getCachedData(aRow);
        case 3: // Subject
          return "";
        case 4: // Size
        default:
          return new Integer(0);
      }
    }
    
    return retValue;
  }

  protected MessageCache getCachedData(int row)
	{
		if (cache[row] == null)
    {
      try
      {
        Message m = messages[row];
        cache[row] = new MessageCache(m);
			}
      catch (MessagingException e)
      {
				e.printStackTrace();
			}
		}
	
		return cache[row];
	}
  
  private static final class MessageCache
  {
    public MessageCache(Message message) throws MessagingException
    {
      subject = message.getSubject();
      size = new Integer(message.getSize());
      date = message.getReceivedDate();
      if (date == null)
        date = message.getSentDate();
      Address[] sender = message.getFrom();
      from =  sender == null ? null : sender[0] ;
      flags = message.getFlags();
    }
    
    Flags flags;
    Integer size;
    Address from;
    Date date;
    String subject;
  }
  
  public final void deleteMessasge(int index) throws MessagingException
  {
    messages[index].setFlag(Flags.Flag.DELETED, true);
    cache[index] = null;
    fireTableRowsUpdated(index, index);
  }
  
  public final void undeleteMessage(int index) throws MessagingException
  {
    messages[index].setFlag(Flags.Flag.DELETED, false);
    cache[index] = null;
    fireTableRowsUpdated(index, index);
  }
  
}