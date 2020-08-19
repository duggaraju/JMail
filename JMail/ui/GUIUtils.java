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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.URL;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import jmail.JMailPlugin;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.GUIUtilities;
import org.gjt.sp.jedit.View;
import org.gjt.sp.jedit.jEdit;

public final class GUIUtils
{
  private GUIUtils() {}
  
  public static Icon createIcon(String name)
  {
    String image = jEdit.getProperty(name + ".icon");
    if (image != null)
    {
      URL url = JMailPlugin.class.getResource("icons/" +image);
      if (url != null)
      {
        return new ImageIcon(url);
      }
    }
    else
    {
      System.err.println("Error Loading " + name);
    }
    return null;
  }


  public  static JMenu createSubMenu(String name, ActionSet actions)
  {
    String mname = jEdit.getProperty(name + ".label", "");
    String items = jEdit.getProperty(name, "");

    JMenu menu = new JMenu(mname);
    StringTokenizer tokenizer = new StringTokenizer(items);
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      if (token.equals("-")) {
        menu.addSeparator();
      }
      else if (token.charAt(0) == '+')
      {
        menu.add(createSubMenu(token.substring(1), actions));
      }
      else
      {
        menu.add(createMenuItem(token, actions));
      }
    }

    return menu;
  }

  public static JMenuItem createMenuItem(String name, ActionSet actions)
  {
    //JMenuItem mi = GUIUtilities.loadMenuItem(name);
    JMenuItem mi = new JMenuItem(jEdit.getProperty(name + ".label", name));
    if (actions.getAction(name) != null)
    {
      mi.addActionListener(new EditActionWrapper(actions.getAction(name)));
      mi.setEnabled(true);
    }
    return mi;
  }

  public static JComponent[] createMenuItems(String name)
  {
    ArrayList popupItems = new ArrayList(3);
    String entries = jEdit.getProperty(name, "");
    StringTokenizer tokenizer = new StringTokenizer(entries);
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      if (token.equals("-"))
      {
        popupItems.add(new javax.swing.JPopupMenu.Separator());
        continue;
      }
      JMenuItem mi = GUIUtilities.loadMenuItem(token);
      popupItems.add(mi);

    }
    JComponent[] retVal = new JComponent[popupItems.size()];
    popupItems.toArray(retVal);
    return retVal;
  }
  
  public static JPopupMenu createPopupMenu(String name, ActionSet actions)
  {
    JPopupMenu popupMenu = new JPopupMenu();
    JMenu menu = createSubMenu(name, actions);
    Component[] items = menu.getMenuComponents();
    for (int i = 0 ; i < items.length; i++)
    {
      popupMenu.add(items[i]);
    }
    return popupMenu;
  }

  public static void showErrorMessage(Exception exception)
  {
    exception.printStackTrace();
    JOptionPane.showMessageDialog(jEdit.getActiveView(), exception.getMessage(),
      "Error", JOptionPane.ERROR_MESSAGE);
  }

  public static void showWarningMessage(Exception exception)
  {
    JOptionPane.showMessageDialog(jEdit.getActiveView(), exception.getMessage(),
      "Error", JOptionPane.WARNING_MESSAGE);
  }

  static class EditActionWrapper implements ActionListener
  {
    EditActionWrapper(EditAction action)
    {
      this.action = action;
    }
    public void actionPerformed(ActionEvent event)
    {
      View view = GUIUtilities.getView((Component)event.getSource());
      action.invoke(view);
    }
    
    private EditAction action;
  }
  
  public static void saveTableColumnGeometry(JTable table, String name)
  {
    TableColumnModel columnModel = table.getColumnModel();
    StringBuffer buffer = new StringBuffer();
    final int count = columnModel.getColumnCount();
    for (int i = 0 ; i < count ;  ++i)
    {
      if (i != 0)
        buffer.append(',');
      buffer.append(columnModel.getColumn(i).getWidth());
    }
    jEdit.setProperty(name, buffer.toString());
  }

  public static void loadTableColumnGeometry(JTable table, String name)
  {
    String value = jEdit.getProperty(name);
    if (value != null)
    {
      String[] values = value.split("\\,");
      TableColumnModel columnModel = table.getColumnModel();

      for (int i =0 ; i < values.length; ++i)
      {
        int width = Integer.valueOf(values[i]).intValue();
        columnModel.getColumn(i).setPreferredWidth(width);
        columnModel.getColumn(i).setWidth(width);
      }
    }
  }
  
}
