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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import java.text.MessageFormat;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

public abstract class HeaderPanel extends JPanel
{

  public HeaderPanel()
  {
    setLayout(layout = new GridBagLayout());
    constraints = new GridBagConstraints();
  }
  
  public final void layoutHeaders(String[] labels, JComponent[] values)
  {
		constraints.gridheight = 1;
		constraints.gridwidth = 1;
    constraints.fill = GridBagConstraints.BOTH;
    
    String[]args = new String[1];
    
    for (int i = 0 ; i < labels.length; i++)
    {
      args[0] = labels[i];
      JLabel label = new JLabel(MessageFormat.format(LABEL_TEMPLATE, args));
      addRow(label, values[i], i);
    }
  }
  
  private final void addRow(JLabel name, JComponent value, int offset)
  {
    constraints.gridx = 0;
    constraints.gridy = offset;    
    constraints.weightx = 0.0;
    constraints.gridwidth = 1;
    layout.setConstraints(name, constraints);
		add(name);
    
    constraints.gridx = 1;
    constraints.gridy = offset;
    constraints.weightx = 1.0;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(value, constraints);
		add(value);
  }

  private final GridBagConstraints constraints;
  private final GridBagLayout layout;
  private static final String LABEL_TEMPLATE = "<html><b>{0}</b></html>";  
}