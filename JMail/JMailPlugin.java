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

import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.URLName;

import javax.swing.JOptionPane;

import jmail.options.MailOptions;
import jmail.options.ServerListModel;

import jmail.ui.GUIUtils;
import jmail.ui.PasswordAuthenticator;
import jmail.ui.SendPanel;

import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EBPlugin;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public class JMailPlugin extends EBPlugin
{

  static final String MAIL_DEBUG = "options.jmail.debug";
  
  public JMailPlugin()
  {
    instance = this;
  }
  
  
  public static final JMailPlugin getInstance()
  {
    return instance;
  }
  
  public void start()
  {
    authenticator = new PasswordAuthenticator();
    Properties props = System.getProperties();
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");

    session = Session.getDefaultInstance(props, authenticator);
    session.setDebug(jEdit.getBooleanProperty(MAIL_DEBUG, false));
    
    //Load default server list
    ServerListModel listModel = new ServerListModel();
    serverList = listModel.getServerList();
  }
  
  public final void mailBuffer(View view)
  {
    Buffer buffer = view.getBuffer();
    try
    {
      if (replyMessage == null)
      {
        replyMessage = MessageHelper.createReply(session, null, MessageType.NORMAL);
      }
      MessageHelper.fillMessage(replyMessage, buffer);
      SendPanel panel = new SendPanel(replyMessage);
      int result = JOptionPane.showConfirmDialog(jEdit.getActiveView(), panel,
        "Send Message ...", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION)
      {
        replyMessage = panel.getMessage();
        replyMessage.setSentDate(new Date());
        Transport transport = session.getTransport(getSendURL());
        try
        {
          transport.connect();
          transport.sendMessage(replyMessage, replyMessage.getAllRecipients());
        }
        finally
        {
          transport.close();
        }
      }
    }
    catch(Exception ex)
    {
      GUIUtils.showErrorMessage(ex);
    }
    finally
    {
      replyMessage = null;      
    }
  }
  
  public void stop()
  {
    session = null;
    authenticator = null;
    serverList = null;
  }
  
  public final void handleMessage(EBMessage message) {}
  
  /**
   * Returns the currently active Java Mail session.
   * @return the java mail session.
   */
  public final Session getSession()
  {
    return session;
  }
  
  public final List getServerList()
  {
    return serverList;
  }
  
  public final void updateServers(List newList)
  {
    if (!serverList.equals(newList))
    {
      serverList = newList;
      JMailMessage message = new JMailMessage(this, JMailMessage.SERVERS_CHANGED, serverList);
      EditBus.send(message);
    }
  }
  
  public final void setReplyMessage(Message message, MessageType type)
  {
    try
    {
      replyMessage = MessageHelper.createReply(session, message, type);
    }
    catch(Exception ex)
    {
      GUIUtils.showErrorMessage(ex);
    }
  }
  
  private final URLName getSendURL()
  {
    return new URLName("smtp://" + jEdit.getProperty(MailOptions.OUTGOING_SERVER) +  ":25");    
  }
  
  private Session session;
  private Message replyMessage;
  private List serverList; 
  private PasswordAuthenticator authenticator;
  
  private static JMailPlugin  instance;
  
}
