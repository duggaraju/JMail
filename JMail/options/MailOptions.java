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

public interface MailOptions 
{
  public static final String NAME = "options.jmail.name";
  public static final String MAIL_ADDRESS = "options.jmail.email";
  
  public static final String OUTGOING_SERVER = "options.jmail.outgoing.server";
  
  public static final String SERVER_COUNT = "options.jmail.incoming.servers";
  public static final String INCOMING_SERVER = "options.jmail.incoming.server";
  public static final String SERVER_NAME = ".name";
  public static final String SERVER_TYPE = ".type";
  public static final String SERVER_HOST = ".host";
  public static final String SERVER_PORT = ".port";
  public static final String SERVER_USERNAME = ".username";
  public static final String SERVER_PASSWORD = ".password";
  
  public static final String REPLY_MESSAGE_TYPE = "options.jmail.reply.type";
  public static final String FORWARD_MESSAGE_TYPE = "options.jmail.forward.type";
  
  public static final String PANEL_LAYOUT = "options.jmail.layout";
   
  public static final String SERVER_IMAP = "imap";
  public static final String SERVER_POP3 = "pop3";
  
}