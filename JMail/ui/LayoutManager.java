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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import org.gjt.sp.jedit.jEdit;

public final class LayoutManager implements PropertyChangeListener
{
  static final String HPANE_PROPERTY = "options.jmail.hpane.location";
  static final String VPANE_PROPERTY = "options.jmail.vpane.location";
  
	private  final JSplitPane hpane,vpane;
  private final JPanel parent;
  private final JPanel[] panels;
  
  public LayoutManager(JPanel parent, JPanel[] panels)
  {
    this.parent = parent;
    this.panels = panels;

		hpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    hpane.setOneTouchExpandable(true);
    hpane.addPropertyChangeListener(this);
    hpane.setDividerLocation(jEdit.getIntegerProperty(HPANE_PROPERTY, 300));

    vpane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    vpane.setOneTouchExpandable(true);
    vpane.addPropertyChangeListener(this);
    vpane.setDividerLocation(jEdit.getIntegerProperty(VPANE_PROPERTY, 400));
  }
  
  public final void propertyChange(PropertyChangeEvent event)
  {
    if (event.getPropertyName() == JSplitPane.DIVIDER_LOCATION_PROPERTY)
    {
      Object source = event.getSource();
      if (source == hpane)
      {
        jEdit.setIntegerProperty(HPANE_PROPERTY, hpane.getDividerLocation());
      }
      else if (source == vpane)
      {
        jEdit.setIntegerProperty(VPANE_PROPERTY, vpane.getDividerLocation());        
      }
    }
  }

  private final void setLeftLayout()
  {
    vpane.setTopComponent(panels[0]);
    vpane.setBottomComponent(panels[1]);
    hpane.setLeftComponent(vpane);
    hpane.setRightComponent(panels[2]);
    parent.add(hpane);    
  }
  
  private final void setRightLayout()
  {
    vpane.setTopComponent(panels[1]);
    vpane.setBottomComponent(panels[2]);
    hpane.setLeftComponent(panels[0]);
    hpane.setRightComponent(vpane);
    parent.add(hpane);    
  }

  private final void setTopLayout()
  {
    hpane.setLeftComponent(panels[0]);
    hpane.setRightComponent(panels[1]);
    vpane.setTopComponent(hpane);
    vpane.setBottomComponent(panels[2]);
    parent.add(vpane);    
  }
  
  private final void setMultiLayout()
  {
    JDesktopPane desktop = new JDesktopPane();     
    for (int i =0 ; i < panels.length; i++)
    {
      JInternalFrame frame = new JInternalFrame(TITLES[i], true, false, true, true);
      frame.getContentPane().add(panels[i]);
      frame.setSize(300, 400);
      frame.setVisible(true);
      desktop.setLayer(frame, 0);
      desktop.add(frame);
    }
    parent.add(desktop);    
  }
  
  private final void setStackedLayout()
  {
    hpane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    hpane.setTopComponent(panels[0]);
    vpane.setTopComponent(panels[1]);
    vpane.setBottomComponent(panels[2]);
    hpane.setBottomComponent(vpane);
    parent.add(hpane);    
  }
  
  public void doLayout(LayoutType type)
  {
    parent.removeAll();
    hpane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    
    if (type == LayoutType.LEFT)
    {
      setLeftLayout();
    }
    else if (type == LayoutType.RIGHT)
    {
      setRightLayout();
    }
    else if (type == LayoutType.MULTI)
    {
      setMultiLayout();
    }
    else if (type == LayoutType.STACKED)
    {
      setStackedLayout();
    }
    else
    {
      setTopLayout();
    }
  }

  static final String[] TITLES = 
  {
    "Folders",
    "Messages",
    "Attachments"
  };
}
