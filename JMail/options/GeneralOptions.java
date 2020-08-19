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

import java.awt.GridLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import jmail.ui.GUIUtils;
import jmail.ui.LayoutType;
import org.gjt.sp.jedit.AbstractOptionPane;
import org.gjt.sp.jedit.jEdit;

public class GeneralOptions extends AbstractOptionPane implements MailOptions
{
  public GeneralOptions()
  {
    super("options.jmail.general");
  }
  
  protected void _init()
  {
    name = new JTextField();
    name.setText(jEdit.getProperty(NAME, ""));
    addComponent(jEdit.getProperty("options.jmail.nameLabel", "Name:"), name);
    
    email = new JTextField();
    email.setText(jEdit.getProperty(MAIL_ADDRESS, ""));
    addComponent(jEdit.getProperty("options.jmail.emailLabel", "E-Mail:"), email);
    
    replyOption = new JComboBox(REPLY_OPTIONS);
    replyOption.setSelectedIndex(getIndex(jEdit.getProperty(REPLY_MESSAGE_TYPE, "include")));
    
    addComponent(jEdit.getProperty("options.jmail.replyLabel"), replyOption);
    
    forwardOption = new JComboBox(REPLY_OPTIONS);
    forwardOption.setSelectedIndex(getIndex(jEdit.getProperty(FORWARD_MESSAGE_TYPE, "include")));
    addComponent(jEdit.getProperty("options.jmail.forwardLabel"), forwardOption);
    
    buttonGroup = new ButtonGroup();
    JPanel panel = new JPanel(new GridLayout(1,0));
    
    for (int i = 0; i < LAYOUT_OPTIONS.length; i++)
    {
      String value = LAYOUT_OPTIONS[i].toString();
      Icon icon = GUIUtils.createIcon(value);
      buttons[i] = new JRadioButton(value, icon);
      buttonGroup.add(buttons[i]);
      panel.add(buttons[i]);
    }
    addComponent("Layout:", panel);

    int index = getLayoutIndex(jEdit.getProperty(PANEL_LAYOUT, "left"));
    buttonGroup.setSelected(buttons[index].getModel(), true);
  }
  
  protected void _save()
  {
    jEdit.setProperty(NAME, name.getText());
    jEdit.setProperty(MAIL_ADDRESS, email.getText());
    jEdit.setProperty(REPLY_MESSAGE_TYPE, OPTIONS[replyOption.getSelectedIndex()].toString());
    jEdit.setProperty(FORWARD_MESSAGE_TYPE, OPTIONS[forwardOption.getSelectedIndex()].toString());
    jEdit.setProperty(PANEL_LAYOUT, LAYOUT_OPTIONS[getSelectedLayout()].toString());
  }
  
  public static final int getIndex(String value)
  {
    ReplyType type = ReplyType.fromString(value);
    
    for (int i = 0 ; i < OPTIONS.length; i++)
      if(OPTIONS[i] == type)
        return i;
    return 0;
  }
  
  public static final int getLayoutIndex(String value)
  {
    for (int i = 0 ; i < LAYOUT_OPTIONS.length; i++)
      if(LAYOUT_OPTIONS[i].equals(value))
        return i;
    return 0;    
  }
  
  private final int getSelectedLayout()
  {
    for(int i =0 ; i < buttons.length; i++)
      if (buttons[i].isSelected())
        return i;
    return 0;
  }
  
  static final ReplyType[] OPTIONS =
  {
    ReplyType.INCLUDE,
    ReplyType.ATTACH,
    ReplyType.INDENT,
    ReplyType.PREFIX
  };
  
  static final LayoutType[] LAYOUT_OPTIONS =
  {
    LayoutType.LEFT,
    LayoutType.RIGHT,
    LayoutType.STACKED,
    LayoutType.MULTI,
    LayoutType.TOP
  };
  
  static final String[] REPLY_OPTIONS = new String[OPTIONS.length];
  
  static
  {
    for (int i =0; i < OPTIONS.length; i++)
    {
      REPLY_OPTIONS[i] = jEdit.getProperty("options.jmail." + OPTIONS[i], OPTIONS[i].toString());
    }
  }
  
  private JTextField name;
  private JTextField email;
  private JComboBox replyOption;
  private JComboBox forwardOption;
  private ButtonGroup buttonGroup;
  private JRadioButton[] buttons = new  JRadioButton[LAYOUT_OPTIONS.length];
}