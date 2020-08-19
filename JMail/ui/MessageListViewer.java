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
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.Part;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import javax.swing.table.TableColumnModel;
import jmail.JMailMessage;
import jmail.JMailPlugin;
import jmail.MessageHelper;
import jmail.MessageType;

import jmail.ui.MessageListModel;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.Buffer;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public final class MessageListViewer extends JPanel
{
  private MessageListModel model;
  private TableSorter sorter;
  private JTable table;
  private JPopupMenu popupMenu;
  
  public MessageListViewer( MessageListModel model)
	{
		super(new BorderLayout());
    this.model = model;
    
		sorter=new TableSorter(model);
		table = new JTable(sorter);
		table.setShowGrid(false);
		sorter.addMouseListenerToHeaderInTable(table);
	
		// find out what is pressed
		table.getSelectionModel().addListSelectionListener(new MessageListener());

    MessageTableRenderer renderer = new MessageTableRenderer();
    final TableColumnModel columnModel = table.getColumnModel();
    final int count = table.getColumnCount();
    for (int i = 0; i < count ; ++i)
      table.setDefaultRenderer(table.getColumnClass(i), renderer);
    
    table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
    GUIUtils.loadTableColumnGeometry(table, MESSAGE_TABLE_WIDTH);
    columnModel.addColumnModelListener(
      new TableColumnWidthTracker(table, MESSAGE_TABLE_WIDTH));
      
    ActionSet actions = new ActionSet();
    for (int i =0 ; i < ACTIONS.length; i++)
    {
      actions.addAction(new MenuAction(ACTIONS[i]));
    }
    popupMenu = GUIUtils.createPopupMenu("jmail.message.popup", actions);    
    
    table.addMouseListener(new MouseListener());

		final JScrollPane scrollpane = new JScrollPane(table); 
    scrollpane.getViewport().setBackground(Color.WHITE);
		add(scrollpane,BorderLayout.CENTER);
  }

  class MessageListener implements ListSelectionListener
  {
  
    public void valueChanged(ListSelectionEvent e)
    {
      if (!e.getValueIsAdjusting())
      {
        ListSelectionModel lm = (ListSelectionModel) e.getSource();
        int which = lm.getMaxSelectionIndex();
        if (which != -1)
        {
          // get the message and display it
          Message msg = model.getMessage(sorter.transformIndex(which));
          JMailMessage message = new JMailMessage(JMailPlugin.getInstance(),
            JMailMessage.MESSAGE_SELECTED, msg);
          EditBus.send(message);
        }
      }
    }
  
  }
  
  class MouseListener extends MouseAdapter 
  {
    public void mousePreseed(MouseEvent event)
    {
      doPopup(event);
    }
    
    public void mouseReleased(MouseEvent event)
    {
      doPopup(event);
    }
    
    private final void doPopup(MouseEvent event)
    {
      if (event.isPopupTrigger())
      {
        Flags flags = (Flags) table.getValueAt(table.getSelectedRow(), 0);
        JMenuItem item = (JMenuItem)popupMenu.getComponent(DELETE_INDEX);
        if (flags.contains(Flags.Flag.DELETED))
        {
          item.setText(jEdit.getProperty("jmail.undelete.label"));
        }
        else
        {
          item.setText(jEdit.getProperty("jmail.delete.label"));          
        }
        popupMenu.show(table, event.getX(), event.getY());
      }
    }
    
  }
  
  private final void viewMessage(View view, Message message, MessageType type) throws Exception
  {
    JMailPlugin.getInstance().setReplyMessage(message, type);
    Part content = MessageHelper.getMailContent(message);
    Buffer buffer = MessageHelper.getReplyBuffer(view, content, MailClient.MESSAGE_FILE, type);
    view.setBuffer(buffer);
  }
  
  final class MenuAction extends EditAction
  {
    public MenuAction(String name)
    {
      super(name);
    }
    
    public final String getCode()
    {
      return "";
    }
    
    public final void invoke(View view)
    {
      final int row = table.getSelectedRow();
      if (row == -1)
        return;
      final Message message = model.getMessage(row);
      final String name = getName();
      try
      {
        if (name.equals(ACTIONS[0]))
        {
          viewMessage(view, message, MessageType.REPLY);
        }
        else if (name.equals(ACTIONS[1]))
        {
          viewMessage(view, message, MessageType.REPLY_ALL);
        }
        else if (name.equals(ACTIONS[2]))
        {
          viewMessage(view, message, MessageType.FORWARD);
        }
        else if(name.equals(ACTIONS[3]))
        {
          if (message.isSet(Flags.Flag.DELETED))
            model.undeleteMessage(row);
          else
            model.deleteMessasge(row);
        }
        else if (name.equals(ACTIONS[4]))
        {
        }
      }
      catch(Exception ex)
      {
        GUIUtils.showErrorMessage(ex);
      }
    }
  }
  
  private static final String[] ACTIONS = 
  {
    "jmail.reply",
    "jmail.replyall",
    "jmail.forward",
    "jmail.delete",
    "jmail.move"
  };
 
  private static final int DELETE_INDEX = 4;
  static final String MESSAGE_TABLE_WIDTH = "options.jmail.messages.tablewidth"; 
}
