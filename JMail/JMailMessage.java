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
 
package jmail;

import java.util.List;
import javax.mail.Folder;
import javax.mail.Message;

import javax.mail.Part;
import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;

/**
 * EditBus message related to the JMail plugin.
 */
public class JMailMessage
   extends EBMessage
{
  public static final String FOLDER_SELECTED = "FOLDER SELECTED";

  public static final String MESSAGE_SELECTED = "MESSAGE SELECTED";
  
  public static final String PART_SELECTED = "PART_SELECTED";

  public static final String SERVERS_CHANGED = "SERVERS CHANGED";

  public JMailMessage(EBComponent source, Object reason, Object info)
  {
    super(source);
    this.reason = reason;
    this.info = info;
  }

  public final Folder getFolder()
  {
    return (Folder)info;
  }

  public final Message getMessage()
  {
    return (Message)info;
  }
  
  public final Part getPart()
  {
    return (Part)info;
  }
  
  public final List getServerList()
  {
    return (List)info;
  }

  public final Object getReason()
  {
    return reason;
  }

  public final String toString()
  {
    return super.toString() + getInfo();
  }
  
  private final String getInfo()
  {
    return "[reason=" + reason + ']';
  }
  
  private Object reason;

  private Object info;

}