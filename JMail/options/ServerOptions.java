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

package jmail.options;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import jmail.JMailPlugin;
import jmail.ui.ServerUI;

import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class ServerOptions extends AbstractOptionPane implements ActionListener, MailOptions
{
  public ServerOptions()
  {
    super("options.jmail.server");
  }
  
  protected void _init()
  {
    smtpServer = new JTextField();
    smtpServer.setText(jEdit.getProperty(OUTGOING_SERVER, ""));
    addComponent(jEdit.getProperty("options.jmail.outgoingLabel", "SMTP Server:"), smtpServer);
    
    add = new JButton("Add");
    add.addActionListener(this);
    
    remove = new JButton("Remove");
    remove.addActionListener(this);
    
    edit = new JButton("Edit");
    edit.addActionListener(this);
    
    JPanel panel = new JPanel();
    panel.add(add);
    panel.add(remove);
    panel.add(edit);
    
    serverModel = new ServerListModel();
    servers = new JList(serverModel);
    
    JPanel serverPanel = new JPanel(new BorderLayout());
    serverPanel.add(BorderLayout.CENTER, new JScrollPane(servers));
    serverPanel.add(BorderLayout.SOUTH, panel);
    addComponent(jEdit.getProperty("options.jmail.incomingLabel", "Servers:"), serverPanel);
  }
  
  protected void _save()
  {
    jEdit.setProperty(OUTGOING_SERVER, smtpServer.getText()); 
    serverModel.save();
    
    JMailPlugin.getInstance().updateServers(serverModel.getServerList());
  }
  
  public void actionPerformed(ActionEvent event)
  {
    if (event.getSource() == add)
    {
      ServerUI ui = new ServerUI();
      int result = JOptionPane.showConfirmDialog(jEdit.getActiveView(), ui, "Add Server ...", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION && ui.getServerInfo() != null)
      {
        ServerInfo info = ui.getServerInfo();
        serverModel.add(info);
      }
    }
    else if (event.getSource() == edit)
    {
      int index = servers.getSelectedIndex();
      if (index != -1)
      {
        ServerInfo info = (ServerInfo)serverModel.getElementAt(index);
        ServerUI ui = new ServerUI(info);
        int result = JOptionPane.showConfirmDialog(jEdit.getActiveView(), ui, "Edit Server ...", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION && ui.getServerInfo() != null)
        {
          info = ui.getServerInfo();
          serverModel.update(index, info);
        }
      }
    }
    else if(event.getSource() == remove)
    {
      int index = servers.getSelectedIndex();
      if (index != -1)
      {
        serverModel.remove(index);
      }
    }
  }
  
  private JButton add;
  private JButton remove;
  private JButton edit;
  private JTextField smtpServer;
  private JList servers;
  private ServerListModel serverModel;
}