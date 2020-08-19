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

public final class LayoutType 
{

  public static final LayoutType LEFT = new LayoutType("left");
  
  public static final LayoutType RIGHT = new LayoutType("right");
  
  public static final LayoutType TOP = new LayoutType("top");
  
  public static final LayoutType STACKED = new LayoutType("stacked");
  
  public static final LayoutType MULTI = new LayoutType("multi");
  
  
  private LayoutType(String value)
  {
    this.value = value;
  }
  
  public final String toString()
  {
    return value;
  }
  
  public static LayoutType fromString(String value)
  {
    LayoutType type = null;
    if (value.equals("left"))
    {
      type = LEFT;
    }
    else if (value.equals("right"))
    {
      type = RIGHT;
    }
    else if (value.equals("top"))
    {
      type = TOP;
    }
    else if (value.equals("stacked"))
    {
      type = STACKED;
    }
    else if (value.equals("multi"))
    {
      type = MULTI;
    }
    return type;
  }
  
  private String value;
}