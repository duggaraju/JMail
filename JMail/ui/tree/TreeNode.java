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
 
package jmail.ui.tree;

import java.util.Enumeration;
import javax.swing.tree.DefaultMutableTreeNode;

import java.util.List;
import jmail.ui.GUIUtils;

/**
 * A Simple TreeNode class with support for lazy Loading of children.
 *
 */

public class TreeNode extends DefaultMutableTreeNode
{
  public static final Object DUMMY_NODE = "Unknown Object";
  protected boolean loaded = false;
  protected String name = null;

  public TreeNode(Object obj)
  {
    super(obj);
  }

  public final void addChildren(List list)
  {
    addChildren(list, 0);
  }

  public final void addChildren(List list, int startIndex)
  {
    addChildren(list.toArray(), startIndex);
    loaded = true;
  }
  
  public final void addChildren(Object[] children, int startIndex)
  {
    for (int i = 0; i < children.length; i++)
    {
      addChild(children[i], startIndex++);
    }
    loaded = true;
  }

  public void addChild(Object obj, int index)
  {
    insert(TreeNodeFactory.createNode(obj), index);
  }

  /**
   * Add the children to this node.
   * Subclasses need to overrride this method.
   */

  protected void populateChildren() throws Exception {};

  public final boolean isLoaded()
  {
    return loaded;
  }
  
  public final void loadChildren()
  {
    if (!loaded)
    {
      try
      {
        populateChildren();
        loaded = true;
      }
      catch(Exception ex)
      {
        GUIUtils.showErrorMessage(ex);
      }
    }
  }
  
  public String toString()
  {
    if (name == null)
    {
      return getUserObject() == null ? "" : getUserObject().toString();
    }
    return name;
  }

  public void updateInfo(List list)
  {
    removeAllChildren();
    addChildren(list);
  }

  /** Tells whether the given node is leaf or not.
   *
   */

  public final void reaload()
  {
    setLoaded(false);
  }
  
  protected void setLoaded(boolean value)
  {
    loaded  = value;

    //If children were already loaded unload them.
    if (!loaded)
    {
      removeAllChildren();
    }
    
  }
  
  public final boolean equals(Object obj)
  {
    if (obj instanceof TreeNode)
    {
      TreeNode node = (TreeNode)obj;
      return getUserObject().equals(node.getUserObject());
    }
    return false;
  }

  public final TreeNode getChild(Object obj)
  {
    Enumeration children = children();
    while (children.hasMoreElements())
    {
      TreeNode node = (TreeNode) children.nextElement();
      if (node.getUserObject().equals(obj))
      {
        return node;
      }
    }
    return null;    
  }

  public final Object getParentObject()
  {
    TreeNode parent = (TreeNode)getParent();
    return parent == null ? null : parent.getUserObject();
  }

}
