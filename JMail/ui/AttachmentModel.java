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

import java.util.ArrayList;

import javax.activation.DataSource;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.MultipartDataSource;
import javax.mail.Part;
import javax.mail.internet.InternetAddress;

import javax.swing.AbstractListModel;

import jmail.JMailMessage;
import jmail.JMailPlugin;

import org.gjt.sp.jedit.EditBus;

public class AttachmentModel extends AbstractListModel
{

  private final StatusListener statusListener;
  private final ArrayList attachments = new ArrayList();
  private final String[] headers = new String[4];

  private HeaderListener headerListener;
  private Message message;
  
  public AttachmentModel(StatusListener statusListener)
  {
    this.statusListener = statusListener;
  }
  
  public final void setHeaderListener(HeaderListener listener)
  {
    headerListener = listener;
  }
  
  public final int getSize()
  {
    return attachments.size();
  }
    
  public Object getElementAt(int index)
  {
    return attachments.get(index);
  }
  
  public final void setMessage(Message message)
  {
    this.message = message;
    clear();
    if (message != null)
    {
      final Worker worker = new Worker();
      worker.start();
    }
    else
    {
      headerListener.headersChanged(headers);
      fireContentsChanged(this, 0, 0);      
    }
  }
  
  private final void clear()
  {
    attachments.clear();
    for (int i = 0 ;i < headers.length; ++i)
      headers[i] = null;
  }
  
  private final class Worker extends SwingWorker
  {
    Worker()
    {
      statusListener.setStatus("Loading message content ...");
    }
    
    public final Object construct()
    {
      try
      {
        Folder folder = message.getFolder();
        if (!folder.isOpen())
          folder.open(Folder.READ_ONLY);
        setHeaders();
        addAttachments(message);
      }
      catch(MessagingException me)
      {
        GUIUtils.showErrorMessage(me);
      }
      return null;
    }

    public final void finished()
    {
      headerListener.headersChanged(headers);
      fireContentsChanged(this, 0, attachments.size() - 1);
      statusListener.clearStatus();  
      JMailMessage message = new JMailMessage(JMailPlugin.getInstance(),
          JMailMessage.PART_SELECTED, attachments.get(0));
      EditBus.send(message);
    }
  
  }
  
  private final void addAttachments(Part part) throws MessagingException
  {
    final DataSource source = part.getDataHandler().getDataSource();
    if (source instanceof MultipartDataSource)
    {
      MultipartDataSource dataSource = (MultipartDataSource)source;
      final int count = dataSource.getCount();
      for (int i = 0; i < count; i++)
      {
        addAttachments(dataSource.getBodyPart(i));
      }
    }
    else
    {
      attachments.add(part);
    }
  }

  private final void setHeaders() throws MessagingException
  {
    headers[0] = InternetAddress.toString(message.getFrom());
    headers[1] = InternetAddress.toString(message.getRecipients(Message.RecipientType.TO));
    headers[2] = InternetAddress.toString(message.getRecipients(Message.RecipientType.CC));
    headers[3] = message.getSubject();    
  }
  
}