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

public final class ServerType 
{
  private static final String _POP3 = "pop3";
  private static final String _IMAP = "imap";
  private static final String _NNTP = "nntp";
  private static final String _SMTP = "smtp";
  
  public static final ServerType POP3 = new ServerType(_POP3);
  public static final ServerType IMAP = new ServerType(_IMAP);
  public static final ServerType NNTP = new ServerType(_NNTP);
  public static final ServerType SMTP = new ServerType(_SMTP);

  private ServerType(String type)
  {
    this.type = type;
  }
  
  public final String toString()
  {
    return type;
  }
  
  public static ServerType fromString(String type)
  {
    if (type.equals(_POP3))
      return POP3;
    else if (type.equals(_IMAP))
      return IMAP;
    else if (type.equals(_NNTP))
      return NNTP;
    else if (type.equals(_SMTP))
      return SMTP;
    return null;
  }
  
  private String type;  
}