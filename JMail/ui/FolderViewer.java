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

import javax.mail.Folder;
import javax.mail.MessagingException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import jmail.JMailMessage;
import jmail.JMailPlugin;

import jmail.ui.tree.FolderTreeNode;
import jmail.ui.tree.StoreTreeNode;
import jmail.ui.treetable.JTreeTable;

import org.gjt.sp.jedit.ActionSet;
import org.gjt.sp.jedit.EditAction;
import org.gjt.sp.jedit.EditBus;
import org.gjt.sp.jedit.View;

public class FolderViewer extends JPanel implements TreeSelectionListener
{

  private JTreeTable table;
  private FolderModel model;
  private JPopupMenu folderPopup;
  private JPopupMenu storePopup;
  
  public FolderViewer(FolderModel model, StatusListener listener)
	{
		super(new BorderLayout());
    this.model = model;
    
		table = new JTreeTable(model);
		table.setShowGrid(false);
    table.setAutoResizeMode(JTreeTable.AUTO_RESIZE_OFF);
    table.getColumnModel().addColumnModelListener(
      new TableColumnWidthTracker(table, FOLDER_TABLE_WIDTH));
    GUIUtils.loadTableColumnGeometry(table, FOLDER_TABLE_WIDTH);
    
    final JTree tree = table.getTree();
    tree.setRootVisible(false);
    tree.setShowsRootHandles(true);
    tree.addTreeExpansionListener(new ExpansionListener(model, listener));
    tree.addTreeSelectionListener(this);
    
    final JScrollPane scrollpane = new JScrollPane(table); 		
    scrollpane.getViewport().setBackground(Color.WHITE);
		add(scrollpane,BorderLayout.CENTER);
    
    table.addMouseListener(new MouseListener());
    ActionSet actions = new ActionSet();
    for (int i = 0; i < ACTIONS.length; i++)
    {
      actions.addAction(new MenuAction(ACTIONS[i]));
    }
    
    folderPopup = GUIUtils.createPopupMenu("jmail.folder.popup", actions);
    storePopup = GUIUtils.createPopupMenu("jmail.store.popup", actions);
  }
  
  public final void valueChanged(TreeSelectionEvent event)
  {
    Object node = event.getPath().getLastPathComponent();
    Folder folder = null;
    if (node instanceof FolderTreeNode)
    {
      folder = ((FolderTreeNode)node).getFolder();
    }
    JMailMessage message = new JMailMessage(JMailPlugin.getInstance(), JMailMessage.FOLDER_SELECTED, folder);
    EditBus.send(message);
  }
  
  class MouseListener extends MouseAdapter
  {
    public void mousePressed(MouseEvent event)
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
        TreePath path = table.getTree().getSelectionPath();
        if (path != null)
        {
          Object node = path.getLastPathComponent();
          if (node instanceof FolderTreeNode)
          {
            folderPopup.show(table, event.getX(), event.getY());
          }
          else if (node instanceof StoreTreeNode)
          {
            storePopup.show(table, event.getX(), event.getY());
          }
        }
      }
    }
  }
  
  private final void renameFolder(View view, FolderTreeNode treeNode)
  {
    String newName = JOptionPane.showInputDialog(view, "Enter new name", "Rename Folder", JOptionPane.QUESTION_MESSAGE);
    if (newName != null)
    {
      try
      {
        treeNode.rename(newName);          
      }
      catch(MessagingException me)
      {
        GUIUtils.showErrorMessage(me);
      }
    }
  }
  
  private final void deleteFolder(View view, FolderTreeNode treeNode)
  {
    int result = JOptionPane.showConfirmDialog(view, "Do you want to delete Folder?", "Delete Folder", JOptionPane.YES_NO_OPTION);
    if (result == JOptionPane.YES_OPTION)
    try
    {
      treeNode.delete();
    }
    catch(MessagingException me)
    {
      GUIUtils.showErrorMessage(me);
    }
  }
  
  private final void refreshFolder(FolderTreeNode treeNode)
  {
    try
    {
      treeNode.refresh();
    }
    catch(MessagingException me)
    {
      GUIUtils.showErrorMessage(me);
    }    
  }
  
  private final void createFolder(View view, FolderTreeNode treeNode)
  {
    String newName = JOptionPane.showInputDialog(view, "Enter name", "Create Folder", JOptionPane.QUESTION_MESSAGE);
    if (newName != null)
    {
      try
      {
        treeNode.create(newName);          
      }
      catch(MessagingException me)
      {
        GUIUtils.showErrorMessage(me);
      }
    }    
  }
  
  
  class MenuAction extends EditAction
  {
    public MenuAction(String name)
    {
      super(name);
    }
    
    public String getCode() { return "";}
    
    public void invoke(View view)
    {
      final TreePath path = table.getTree().getSelectionPath();
      Object node = path.getLastPathComponent(); 
      String name = getName();
      if (node instanceof FolderTreeNode)
      {
        FolderTreeNode treeNode = (FolderTreeNode)node;
        if (name.equals(ACTIONS[0]))
        {
          deleteFolder(view, treeNode);
        }
        else if (name.equals(ACTIONS[1]))
        {
          renameFolder(view, treeNode);
        }
        else if (name.equals(ACTIONS[2]))
        {
          refreshFolder(treeNode);
        }
        else if (name.equals(ACTIONS[3]))
        {
          createFolder(view, treeNode);
        }
        model.update(treeNode);
      }
      else if (node instanceof StoreTreeNode)
      {
        final StoreTreeNode storeNode = (StoreTreeNode)node;
        try
        {
          if (name.equals(ACTIONS[4]))
          {
            storeNode.connect();
          }
          else if (name.equals(ACTIONS[5]))
          {
            table.getTree().collapsePath(path);
            model.update(storeNode);
            storeNode.disconnect();
          }
        }
        catch(MessagingException me)
        {
          GUIUtils.showErrorMessage(me);
        }
      }
    }
  }

  public static final String[] ACTIONS =
  {
    "jmail.delete",
    "jmail.rename",
    "jmail.refresh",
    "jmail.newfolder",
    "jmail.connect",
    "jmail.disconnect"
  };
  
  static final String FOLDER_TABLE_WIDTH = "options.jmail.folders.tablewidth";
}
