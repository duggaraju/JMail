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
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import jmail.MessageHelper;

import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.jEdit;


public class SendPanel extends JPanel
{

  private final class HeaderViewer extends HeaderPanel
  {
    HeaderViewer()
    {
      layoutHeaders(HEADERS, values);
    }
  }
  
  private final class AttachmentModel extends AbstractListModel
  {
    private ArrayList list = new ArrayList();
    public int getSize()
    {
      return list.size();
    }
    
    public Object getElementAt(int i)
    {
      return list.get(i);
    }
    
    void add(String[] values)
    {
      for (int i = 0 ; i < values.length; ++i)
        list.add(values[i]);
      fireContentsChanged(this, 0, list.size());
    }
    
    void remove(int index)
    {
      list.remove(index);
      fireIntervalRemoved(this, index, index);
    }
  }

  private static final Icon ATTACHMENT_ICON = GUIUtils.createIcon("paperclip");
  
  private final class AttachmentRenderer extends DefaultListCellRenderer
  {
    public Component getListCellRendererComponent(JList list, Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
    {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      File file = new File ((String)value);
      setText(file.getName());
      setToolTipText(file.getAbsolutePath());
      setIcon(ATTACHMENT_ICON);
      return this;
    }
    
  }

  private final class AttachmentViewer extends JPanel implements ActionListener
  {
    AttachmentViewer()
    {
      super(new BorderLayout());
      
      attachments = new JList(model);
      attachments.setCellRenderer(new AttachmentRenderer());
      attachments.setVisibleRowCount(2);
      
      JScrollPane scroller = new JScrollPane(attachments);
      add (BorderLayout.NORTH, new JLabel("<html><b>Attachments:</b></html>"));
      add(BorderLayout.CENTER, scroller);
      
      add = new JButton("Add");
      add.addActionListener(this);
      
      remove = new JButton("remove");
      remove.addActionListener(this);
      
      JPanel panel = new JPanel(new GridLayout(0,1));
      panel.add(add);
      panel.add(remove);
      add(BorderLayout.EAST, panel);
    }
    
    public void actionPerformed(ActionEvent event)
    {
      if (event.getSource() == add)
      {
        String[] files = 
          GUIUtilities.showVFSFileDialog(jEdit.getActiveView(), null, VFSBrowser.OPEN_DIALOG, true);
        if (files != null)
          model.add(files);
      }
      else
      {
        int index = attachments.getSelectedIndex();
        if (index != -1)
          model.remove(index);
      }
    }
    
    private final JList attachments;
    private final JButton add;
    private final JButton remove;
  }
  
  public SendPanel(Message message)
  {
    super(new BorderLayout());
    this.message = message;
    
    try
    {
      setFromMessage();
    }
    catch(MessagingException me)
    {
      GUIUtils.showErrorMessage(me);
    }
    
    add(BorderLayout.CENTER, new HeaderViewer());
    add(BorderLayout.SOUTH, viewer);
  }
  
  private void updateFromUI() throws MessagingException
  {
    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to.getText()));
    message.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc.getText()));
    message.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc.getText()));
    message.setSubject(subject.getText());    
  }
  
  private void setFromMessage() throws MessagingException
  {
    to.setText(InternetAddress.toString(message.getRecipients(Message.RecipientType.TO)));
    cc.setText(InternetAddress.toString(message.getRecipients(Message.RecipientType.CC)));
    bcc.setText(InternetAddress.toString(message.getRecipients(Message.RecipientType.BCC)));
    subject.setText(message.getSubject());
  }
  
  public final Message getMessage() throws MessagingException, IOException
  {
    updateFromUI();
    int count = model.list.size();
    for (int i = 0 ; i < count; ++i)
    {
      final String fileName = (String)model.list.get(i);
      final File file = new File(fileName);
      final String id = "attachment" + i;
      MessageHelper.addAttachment(message, file, id);
    }
    return message;
  }
 
  private static final String[] HEADERS = 
  {
    "To:",
    "CC:",
    "BCC:",
    "Subject:"
  };

  private final JTextField to = new JTextField();
  private final JTextField cc = new JTextField();
  private final JTextField bcc = new JTextField();
  private final JTextField subject = new JTextField();

  private final AttachmentModel model =new AttachmentModel();
  private final AttachmentViewer viewer = new AttachmentViewer();
  
  private final JComponent[] values = {
    to,
    cc,
    bcc,
    subject
  };
  
  private final Message message;
}