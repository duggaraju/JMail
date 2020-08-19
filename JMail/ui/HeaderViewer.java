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
import javax.swing.JLabel;


public class HeaderViewer extends HeaderPanel implements HeaderListener
{
  public HeaderViewer()
  {
    for (int i =0 ; i < HEADERS.length; ++i)
      values[i] = new JLabel();
    layoutHeaders(HEADERS, values);
  }
  
  public final void headersChanged(String[] headers)  
  {
    for (int i = 0; i < headers.length; ++i)
      values[i].setText(headers[i]);
    repaint();
  }
  
  static final String[] HEADERS = 
  {
    "From:",
    "To:",
    "CC:",
    "Subject:"
  };
  
  final JLabel[] values = new JLabel[HEADERS.length];
}