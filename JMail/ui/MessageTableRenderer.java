package jmail.ui;

import java.awt.Component;
import java.awt.Font;
import java.awt.font.TextAttribute;

import java.text.DateFormat;

import java.util.Date;
import java.util.HashMap;

import javax.mail.Flags;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class MessageTableRenderer extends DefaultTableCellRenderer
{
  private Font deletedFont;
  private Font unreadFont;
  
  private final void createFonts(JTable table)
  {
    HashMap map = new HashMap();
    map.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
    map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
    deletedFont = table.getFont().deriveFont(map);
    
    map.clear();
    map.put(TextAttribute.WEIGHT, TextAttribute.WEIGHT_BOLD);
    unreadFont = table.getFont().deriveFont(map);    
  }
  
  public final Component getTableCellRendererComponent(JTable table, Object value,
                        boolean isSelected, boolean hasFocus, int row, int column)
  {
    if (unreadFont == null)
    {
      createFonts(table);
    }
    
    if (value == null)
    {
      value = UNKNOWN;
    }
    
    //defer to base class for most of the processing.
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

    setIcon(null);    
    switch(column)
    {
      case FLAGS_COLUMN:
        {
          Flags flags = (Flags)value;
          setText(null);
          if (flags.contains(Flags.Flag.DELETED))
          {
            setIcon(DELETED);
            setToolTipText("Deleted");
          }
          else if(!flags.contains(Flags.Flag.SEEN))
          {
            setIcon(UNREAD);
            setToolTipText("Unread");    
          }
          else
          {
            setIcon(READ);
            setToolTipText("Read");            
          }
          break;
        }
      case DATE_COLUMN:
        {
          if (value.getClass() == Date.class)
          {
            setText(dateFormat.format(value));
          }
          else
          {
            setText(value.toString());
          }
          setToolTipText(value.toString());
          break;
        }
      default:
        {
          setToolTipText(value.toString());
          break;
        }
    }
    
    Flags flags = (Flags) table.getValueAt(row, FLAGS_COLUMN);
    if (flags.contains(Flags.Flag.DELETED))
    {
      setFont(deletedFont);
    }
    else if (!flags.contains(Flags.Flag.SEEN))
    {
      setFont(unreadFont);
    }
    return this;
  }
  
  private static final DateFormat dateFormat = 
    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);

  private static final Icon UNREAD = GUIUtils.createIcon("unread");
  private static final Icon READ = GUIUtils.createIcon("read");
  private static final Icon DELETED = GUIUtils.createIcon("deleted");

  private static final int FLAGS_COLUMN = 0;
  private static final int DATE_COLUMN = 1;
  
  private static final String UNKNOWN = "<Unknown>";
}