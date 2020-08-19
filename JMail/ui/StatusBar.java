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

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public final class StatusBar extends JPanel implements StatusListener
{
  private JLabel label;
  private JProgressBar progressBar;
  
  public StatusBar()
  {
    super(new FlowLayout(FlowLayout.LEFT));
    label = new JLabel();
    progressBar = new JProgressBar();
    add(progressBar);
    add(label);
  }
  
  public final void setStatus(String message)
  {
    progressBar.setIndeterminate(true);
    label.setText(message);
  }
  
  public final void clearStatus()
  {
    label.setText(null);
    progressBar.setIndeterminate(false);
  }
}