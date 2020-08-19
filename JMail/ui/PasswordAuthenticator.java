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

import java.net.InetAddress;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import jmail.JMailPlugin;
import jmail.options.ServerInfo;
import org.gjt.sp.jedit.jEdit;

public final class PasswordAuthenticator extends Authenticator
{

  protected PasswordAuthentication getPasswordAuthentication()
  {
  
    String username = "";
    
		// protocol
		String protocol = getRequestingProtocol();
		if (protocol == null)
			protocol = "{Unknown protocol}";

		// get the host
		InetAddress inet = getRequestingSite();
    String host = inet != null ? inet.getHostName() : "{Unknown host}";

		// port
		int port = getRequestingPort();

    ServerInfo info = getMatchingServer(host, port, protocol);
    if (info != null)
    {     
      username = info.getUsername();
      String password = info.getPassword();
      if (username != null && password != null && username.length() > 0 && password.length() > 0)
      {
        return new PasswordAuthentication(username, password);
      }
    }
    
		// Build the info string
    String[] args = { protocol, host, String.valueOf(port) };
		String loginInfo = MessageFormat.format(LOGIN_INFO, args);
                
		// given a prompt?
		String title = getRequestingPrompt();
		if (title == null)
			title = "Login info ...";

    return askAuthentication(title, username, loginInfo);
  }

  private PasswordAuthentication askAuthentication(String title, String username, String info)
  {
    GridBagLayout layout = new GridBagLayout();
		JPanel panel = new JPanel(layout);
    
    GridBagConstraints constraints = new GridBagConstraints();
    constraints.fill = GridBagConstraints.BOTH;

    constraints.gridx = 0;
    constraints.gridy = 0;    
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    JLabel label = new JLabel(info);
    layout.setConstraints(label, constraints);
    panel.add(label);

    constraints.gridx = 0;
    constraints.gridy = 1;    
    constraints.gridwidth = 1;
    constraints.weightx = 0;
		label = new JLabel(jEdit.getProperty("jmail.username"));
    layout.setConstraints(label, constraints);
		panel.add(label);
    
    constraints.gridx = 1;
    constraints.gridy = 1;  
    constraints.weightx = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
		JTextField userField = new JTextField(username);
    layout.setConstraints(userField, constraints);
		panel.add(userField);

    constraints.gridx = 0;
    constraints.gridy = 2;    
    constraints.weightx = 0;
    constraints.gridwidth = 1;
		label = new JLabel(jEdit.getProperty("jmail.password"));
    layout.setConstraints(label, constraints);
		panel.add(label);
		
    constraints.gridx = 1;
    constraints.gridy = 2;    
    constraints.weightx = 1;
    constraints.gridwidth = GridBagConstraints.REMAINDER;
    JPasswordField passField = new JPasswordField("");
    layout.setConstraints(passField, constraints);
		panel.add(passField);

		if (username != null && username.length() > 0)
    {
		  passField.grabFocus();
    }
		else
    {
		  userField.grabFocus();
    }
	
		int result = JOptionPane.showConfirmDialog(jEdit.getActiveView(), panel, title,
		    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
	
		if (result == JOptionPane.OK_OPTION)
		    return new PasswordAuthentication(userField.getText(),
						new String(passField.getPassword()));
		else
		    return null;    
  }
  
  private ServerInfo getMatchingServer(String host, int port, String protocol)
  {
    List serverList = JMailPlugin.getInstance().getServerList();
    Iterator iterator = serverList.iterator();
    while (iterator.hasNext())
    {
      ServerInfo info = (ServerInfo) iterator.next();
      if (info.matches(host, port, protocol))
      {
        return info;
      }
    }
    return null;
  }
  
  static final String LOGIN_INFO =
  "<html>Enter connection details for {0} mail service on<br>Host: {1}<br>Port:{2}</html>";
}
