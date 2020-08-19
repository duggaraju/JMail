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

import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractListModel;

import org.gjt.sp.jedit.jEdit;

public class ServerListModel extends AbstractListModel implements MailOptions
{
  
  public ServerListModel()
  {
    load();
  }
  
  public int getSize()
  {
    return servers.size();
  }
  
  public Object getElementAt(int index)
  {
    return servers.get(index);
  }
  
  public final void load()
  {
    final int numServers = jEdit.getIntegerProperty(SERVER_COUNT, 0);
    for (int i = 0 ; i < numServers ; i++)
    {
      ServerInfo info = new ServerInfo(i);
      servers.add(info);
    }    
  }
  
  public final void save()
  {
    final int numServers = servers.size();
    jEdit.setIntegerProperty(SERVER_COUNT, numServers);
    for (int i = 0 ; i < numServers ; i++)
    {
      ServerInfo info = (ServerInfo)servers.get(i);
      info.save(i);
    }
  }
  
  public final void add(ServerInfo info)
  {
    final int length = servers.size();
    servers.add(info);
    fireIntervalAdded(this, length, length);
  }
  
  public final void remove(int index)
  {
    servers.remove(index);
    fireIntervalRemoved(this, index, index);
  }
  
  public final void update(int index, ServerInfo info)
  {
    servers.set(index, info);
    fireContentsChanged(this, index, index);
  }
  
  public final List getServerList()
  {
    return servers;
  }
  
  private final ArrayList servers = new ArrayList();
}