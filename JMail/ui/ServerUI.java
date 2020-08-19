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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import jmail.options.ServerInfo;
import jmail.options.ServerType;

public class ServerUI extends JPanel implements ActionListener
{
  public ServerUI()
  {
    createUI();
  }
  
  public ServerUI(ServerInfo info)
  {
    createUI();
    name.setText(info.getName());
    host.setText(info.getHost());
    port.setText(String.valueOf(info.getPort()));
    type.setSelectedItem(info.getType().toString().toUpperCase());
    username.setText(info.getUsername());
    String pass = info.getPassword();
    if (pass == null || pass.length()> 0)
    {
      password.setText(pass);
      password.setEnabled(true);
      storePassword.setSelected(true);
    }
  }
  
  private void addComponent(String name, JComponent comp, int offset)
  {
    constraints.gridx = 0;
    constraints. gridy = offset;    
    constraints.gridwidth = 1;
    constraints.weightx = 0;
    JLabel label =  new JLabel(name);
    layout.setConstraints(label, constraints);
    add(label);
    
    constraints.gridx = 1;
    constraints.gridy = offset;    
    constraints.weightx = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    layout.setConstraints(comp, constraints);
    add(comp);

  }
  
  private void createUI()
  {
    setLayout( layout = new GridBagLayout());
    constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.BOTH;
    
    name = new JTextField();
    addComponent("Name", name, 1);
    
    host = new JTextField();
    addComponent("Host:", host, 2);

    port = new JTextField(4);
    addComponent("Port:", port, 3);
    
    type = new JComboBox(SERVER_TYPES);
    addComponent("Type:", type, 0);

    username = new JTextField();
    addComponent("Username:", username, 4);

    password = new JPasswordField();
    password.setEnabled(false);
    addComponent("Password:", password, 5);

    storePassword = new JCheckBox("Remember password");
    addComponent("", storePassword, 6);
    
    storePassword.addActionListener(this);

  }
  
  public final ServerInfo getServerInfo()
  {
    if (info == null)
    {
      final String sName = name.getText();
      final String sHost = host.getText();
      int sPort = new Integer(port.getText()).intValue();
      final String stype = type.getSelectedItem().toString().toLowerCase();
      final ServerType sType = ServerType.fromString(stype);
      
      final String sUser = username.getText();
      final String sPass = new String(password.getPassword());
      
      if (sName.length() > 0 && sHost.length() > 0 && sPort > 0)
      {
        info = new ServerInfo(sName, sHost, sPort, sType, sUser, sPass);
      }
    }
    return info;
  }
  
  public void actionPerformed(ActionEvent event)
  {
    if (storePassword.isSelected())
    {
      password.setEnabled(true);      
    }
    else
    {
      password.setText("");
      password.setEnabled(false);
    }
  }
  final String[] SERVER_TYPES = 
  {
    "POP3",
    "IMAP",
    "NNTP"
  };
  
  private JTextField name;
  private JTextField host;
  private JTextField port;
  private JComboBox type;
  private JTextField username;
  private JPasswordField password;
  private JCheckBox storePassword;
  
  private GridBagConstraints constraints;
  private GridBagLayout layout;
  
  private ServerInfo info;
}