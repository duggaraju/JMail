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

import java.awt.BorderLayout;
import java.awt.Cursor;

import javax.mail.Part;

import javax.swing.JPanel;

import jmail.JMailMessage;
import jmail.JMailPlugin;
import jmail.MessageHelper;

import jmail.options.MailOptions;

import jmail.ui.tree.SessionTreeNode;

import org.gjt.sp.jedit.EBComponent;
import org.gjt.sp.jedit.EBMessage;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public final class MailClient extends JPanel implements EBComponent
{

  private final FolderModel folderModel;
  private final MessageListModel  messageModel;
  private final AttachmentModel attachmentModel;
  private final View view;
  private final LayoutManager layoutManager;
  private final StatusBar statusBar;
  
  public MailClient(View view)
  {
    super(new BorderLayout());
    this.view = view;
    
    JMailPlugin plugin = JMailPlugin.getInstance();

    statusBar = new StatusBar();
    add(BorderLayout.SOUTH, statusBar);
    
    
    SessionTreeNode root = new SessionTreeNode(plugin.getSession());
    root.updateInfo(plugin.getServerList());
    folderModel = new FolderModel(root);    
    final FolderViewer folderViewer = new FolderViewer(folderModel, statusBar);
    
    messageModel = new MessageListModel(statusBar);
    messageModel.setFolder(null);
    final MessageListViewer messageViewer = new MessageListViewer(messageModel);
    
    attachmentModel = new AttachmentModel(statusBar);
    final AttachmentViewer attachmentViewer = new AttachmentViewer(attachmentModel);
    
    JPanel[] panels = {
      folderViewer,
      messageViewer,
      attachmentViewer
    };
    
    JPanel parent = new JPanel(new BorderLayout());
    add(BorderLayout.CENTER, parent);
    layoutManager = new LayoutManager(parent, panels);
    
    final LayoutType layoutType = 
      LayoutType.fromString(jEdit.getProperty(MailOptions.PANEL_LAYOUT, "left"));    
    layoutManager.doLayout(layoutType);
    
    EditBus.addToBus(this);
  }
  
  public void handleMessage(EBMessage mesg)
  {
    if (mesg instanceof JMailMessage)
    {
      final JMailMessage mailmessage = (JMailMessage) mesg;
      Object reason = mailmessage.getReason();
      if (reason == JMailMessage.SERVERS_CHANGED)
      {
        folderModel.getRoot();
      }
      else if(reason == JMailMessage.FOLDER_SELECTED)
      {
        messageModel.setFolder(mailmessage.getFolder());
        attachmentModel.setMessage(null);    
      }
      else if (reason == JMailMessage.MESSAGE_SELECTED)
      {
        attachmentModel.setMessage(mailmessage.getMessage());
      }
      else if (reason == JMailMessage.PART_SELECTED)
      {
        showMessage(mailmessage.getPart());
      }
    }
  }
  
  
  private final void showMessage(Part message)
  {
    try
    {
      view.setBuffer(MessageHelper.getBuffer(view, message, MESSAGE_FILE));
    }
    catch(Exception ex)
    {
      GUIUtils.showErrorMessage(ex);
    }
  }
  
  /**
   * Helper class to handle busy cursor situtations.
   */
  class CursorController implements Runnable
  {
    private Runnable runnable;
    
    CursorController(Runnable runnable)
    {
      setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      this.runnable = runnable;
    }
    
    public void run()
    {
      try
      {
        runnable.run();
      }
      finally
      {
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
      }
    }
  }
  
  public static final String MESSAGE_FILE = "jmailmessage";
}

