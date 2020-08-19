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

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

import jmail.ui.tree.TreeNode;

public final class ExpansionListener implements TreeExpansionListener
{

  public ExpansionListener(FolderModel model, StatusListener listener)
  {
    this.model = model;
    this.listener = listener;  
  }
  
  public final void treeExpanded(TreeExpansionEvent event)
  {
    TreeNode node = (TreeNode) event.getPath().getLastPathComponent(); 
    if (!node.isLoaded())
    {
      Worker worker = new Worker(node);
      worker.start();
    }
  }
  
  final class Worker extends SwingWorker
  {
    private TreeNode node;
    Worker(TreeNode node)
    {
      this.node = node;
      listener.setStatus("Getting folders for " + node + "...");
    }
    public final Object construct()
    {
      node.loadChildren();
      return null;
    }
    
    public void finished()
    {
      model.update(node);
      listener.clearStatus();
    }
  }
  
  public final void treeCollapsed(TreeExpansionEvent event) {}
  
  private StatusListener listener;  
  private FolderModel model;
}