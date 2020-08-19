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
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.io.File;
import javax.mail.MessagingException;
import javax.mail.Part;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.SwingConstants;

import jmail.JMailMessage;
import jmail.JMailPlugin;
import jmail.MessageHelper;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.browser.VFSBrowser;
import org.gjt.sp.jedit.jEdit;

public class AttachmentViewer extends JPanel
{

  private HeaderViewer header;
  private JList list;
  private JPopupMenu popupMenu;

  public AttachmentViewer(AttachmentModel model)
  {
    super(new BorderLayout());

    list = new JList(model);
    list.setLayoutOrientation(JList.VERTICAL_WRAP);
    list.setVisibleRowCount(1);

    list.setCellRenderer(new AttachmentRenderer());
    
    JScrollPane scroller = new JScrollPane(list);
    scroller.getViewport().setBackground(Color.WHITE);
    add(scroller, BorderLayout.CENTER);
    
    header = new HeaderViewer();
    model.setHeaderListener(header);
    
    list.addMouseListener(new MouseListener());
    
    ActionSet actions = new ActionSet();
    for (int i = 0; i < ACTIONS.length; i++)
    {
      actions.addAction(new MenuAction(ACTIONS[i]));
    }
    popupMenu = GUIUtils.createPopupMenu("jmail.attachment.popup", actions);
    

    JScrollPane top = new JScrollPane(header, JScrollPane.VERTICAL_SCROLLBAR_NEVER,
      JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    add(top, BorderLayout.NORTH);
  }
  
  public final void viewAttachment(Object part)
  {
    if (part != null)
    {
      JMailMessage message = new JMailMessage(JMailPlugin.getInstance(),
        JMailMessage.PART_SELECTED, part);
      EditBus.send(message);
    }    
  }

  private static Icon attachment;
  private static Icon mesg;
  
  static
  {
    attachment = GUIUtils.createIcon("attachment");
    mesg = GUIUtils.createIcon("message");
  }
  
  static class AttachmentRenderer extends DefaultListCellRenderer
  {
    public AttachmentRenderer()
    {
      setVerticalAlignment(SwingConstants.TOP);
      setVerticalTextPosition(JLabel.BOTTOM);
      setHorizontalTextPosition(JLabel.CENTER);
    }
    
    public Component getListCellRendererComponent(JList list, Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
    {
      super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
      
      Part part = (Part)value;
      try
      {
        final String name = part.getFileName();
        if (name != null)
          setText(new File(name).getName());
        else
          setText(null);
          
        setToolTipText(part.getContentType());
        String disposition = part.getDisposition();
        if (disposition != null && disposition.equals(Part.ATTACHMENT))
        {
          setIcon(attachment);
        }
        else
        {
          setIcon(mesg);
        }
      }
      catch (MessagingException e)
      {
        setText("Unknown");
        setToolTipText(e.getMessage());
      }
      return this;
    }
  }
  
  private final class MouseListener extends MouseAdapter
  {
    public void mousePressed(MouseEvent event)
    {
      doPopup(event);      
    }
    
    public void mouseReleased(MouseEvent event)
    {
      doPopup(event);
    }
    
    private void doPopup(MouseEvent event)
    {
      if (event.isPopupTrigger())
      {
        popupMenu.show(list, event.getX(), event.getY());      
      }
    }
  }
  
  private final class MenuAction extends EditAction
  {
    public MenuAction(String name)
    {
      super(name);
    }
    
    public final String getCode() { return ""; }
    
    private final void save(View view, Part part, String file)
    {
      try
      {
        MessageHelper.saveAttachment(part, file);
      }
      catch(Exception ex)
      {
        GUIUtils.showErrorMessage(ex);
      }
    }
    
    public final void invoke(View view)
    {
      final String name = getName();
      if (name.equals("jmail.view"))
      {
        viewAttachment(list.getSelectedValue()); 
      }
      else if (name.equals("jmail.save"))
      {
        String[] files = 
          GUIUtilities.showVFSFileDialog(jEdit.getActiveView(), null, VFSBrowser.SAVE_DIALOG, false);
        if (files != null)
        {
          Part part = (Part) list.getSelectedValue();
          save(view, part, files[0]);
        }
      }
      else
      {
        String[] dirs = 
          GUIUtilities.showVFSFileDialog(jEdit.getActiveView(), null, VFSBrowser.CHOOSE_DIRECTORY_DIALOG, false);
        if (dirs != null)
        {
          ListModel model = list.getModel();
          for (int i = 0; i < model.getSize(); i++)
          {
            Part part = (Part) model.getElementAt(i);
            save(view, part, dirs[0]);
          }
        }
      }
    }
  }
  
  static final String[] ACTIONS = 
  {
    "jmail.view",
    "jmail.save",
    "jmail.saveall"
  };
}