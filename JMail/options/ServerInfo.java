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

import org.gjt.sp.jedit.jEdit;

public final class ServerInfo implements MailOptions
{
  public ServerInfo(String name, String host, int port, ServerType type,
    String username, String password)
  {
    this.name = name;
    this.host = host;
    this.port = port;
    this.type = type;
    this.username = username;
    this.password = password;
  }
  
  public ServerInfo(int prefix)
  {
    load(prefix);
  }
  
  public final String getName()
  {
    return name;
  }
  
  public final String getHost()
  {
    return host;
  }
  
  public final int getPort()
  {
    return port;
  }
  
  public final ServerType getType()
  {
    return type;
  }
  
  public final String getUsername()
  {
    return username;
  }
  
  public final String getPassword()
  {
    return password;
  }
  
  public final String getURL()
  {
    StringBuffer buffer = new StringBuffer(type.toString());
    buffer.append("://").append(host).append(':').append(port).append('/');
    return buffer.toString();
  }
  
  public final void load(int prefix)
  {
    name = jEdit.getProperty(INCOMING_SERVER + prefix + SERVER_NAME, "");
    host = jEdit.getProperty(INCOMING_SERVER + prefix + SERVER_HOST,  "");
    port = jEdit.getIntegerProperty(INCOMING_SERVER + prefix + SERVER_PORT, 0);
    String sType = jEdit.getProperty(INCOMING_SERVER + prefix + SERVER_TYPE, "imap");    
    type = ServerType.fromString(sType);
    username = jEdit.getProperty(INCOMING_SERVER + prefix + SERVER_USERNAME,  "");
    password = jEdit.getProperty(INCOMING_SERVER + prefix + SERVER_PASSWORD,  "");
  }
  
  public final void save(int prefix)
  {
    jEdit.setProperty(INCOMING_SERVER + prefix + SERVER_NAME, name);
    jEdit.setProperty(INCOMING_SERVER + prefix + SERVER_HOST, host);
    jEdit.setIntegerProperty(INCOMING_SERVER + prefix + SERVER_PORT, port);
    jEdit.setProperty(INCOMING_SERVER + prefix + SERVER_TYPE, type.toString());
    jEdit.setProperty(INCOMING_SERVER + prefix + SERVER_USERNAME, username);
    jEdit.setProperty(INCOMING_SERVER + prefix + SERVER_PASSWORD, password);
  }
  
  
  public final String toString()
  {
    return name;
  }
  
  public final boolean matches(String host, int port, String protocol)
  {
    return this.host.equals(host) && this.port == port && type.toString().equals(protocol);
  }
  
  private String name;
  private String host;
  private ServerType type;
  private int port;
  private String username;
  private String password;
}