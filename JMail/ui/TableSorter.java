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

import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

public class TableSorter extends TableMap
{
  private int indexes[];
  private ArrayList sortingColumns = new ArrayList();
  private boolean ascending = true;
  private int compares;

  public TableSorter()
  {
      indexes = new int[0]; // For consistency.        
  }

  public TableSorter(TableModel model)
  {
    setModel(model);
  }

  public void setModel(TableModel model)
  {
    super.setModel(model); 
    reallocateIndexes(); 
  }

  private final class ColumnComparator implements Comparator
  {
    final int column;
    public ColumnComparator(int column)
    {
      this.column = column;
    }
    
    public int compare(Object first, Object second)
    {
      int retValue  = 0;    
      // If both values are null return 0
      if (first == null && second == null)
      {
        retValue = 0; 
      }
      else if (first == null)
      { // Define null less than everything. 
        retValue = -1; 
      } 
      else if (second == null)
      { 
        retValue = 1; 
      }
      else
      {
        Class type = model.getColumnClass(column);
        if (type == Integer.class)
        {
          retValue = ((Integer)first).compareTo((Integer)second);
        }
        else if (type == Float.class)
        {
          retValue = ((Float)first).compareTo((Float)second);
        }
        else if (type == Double.class)
        {
          retValue = ((Double)first).compareTo((Double)second);   
        }
        else if (type == Long.class)
        {
          
        }
        else if (type == Date.class)
        {
          retValue = ((Date)first).compareTo((Date)second);
        }
        else if (type == String.class)
        {
          retValue = ((String)first).compareTo((String)second);
        }
        else if (type == Boolean.class)
        {     
          boolean b1 = ((Boolean)first).booleanValue();
          boolean b2 = ((Boolean)second).booleanValue();
          
          retValue =  (b1 == b2) ? 0 : ( b1 ? 1 : -1);
        }
        else
        {
          retValue = first.toString().compareTo(second.toString());
        }  
      }
      return retValue;
    }
    
    public final int compare(int row1, int row2)
    {
      final Object first = model.getValueAt(row1, column);
      final Object second = model.getValueAt(row2, column);
      return compare(first, second);
    }
  }
  
  public final int compareRowsByColumn(int row1, int row2, int column, Comparator comparator)
  {
    Object first = model.getValueAt(row1, column);
    Object second = model.getValueAt(row2, column);    
    return comparator.compare(first, second);    
  }

  public int compare(int row1, int row2)
  {
    compares++;
    for(int level = 0; level < sortingColumns.size(); level++)
    {
      ColumnComparator comparator = (ColumnComparator) sortingColumns.get(level);
      int result = comparator.compare(row1, row2);
      if (result != 0)
          return ascending ? result : -result;
    }
    return 0;
  }

  public void  reallocateIndexes()
  {
    int rowCount = model.getRowCount();
  
    // Set up a new array of indexes with the right number of elements
    // for the new data model.
    indexes = new int[rowCount];
  
    // Initialise with the identity mapping.
    for(int row = 0; row < rowCount; row++)
        indexes[row] = row;
  }
  
  public void tableChanged(TableModelEvent e)
  {
    reallocateIndexes();
  
    super.tableChanged(e);
  }

  private final void checkModel()
  {
    if (indexes.length != model.getRowCount())
    {
        System.err.println("Sorter not informed of a change in model.");
    }
  }

  public void  sort(Object sender)
  {
    checkModel();    
    compares = 0;
    shuttlesort((int[])indexes.clone(), indexes, 0, indexes.length);
  }

    // This is a home-grown implementation which we have not had time
    // to research - it may perform poorly in some circumstances. It
    // requires twice the space of an in-place algorithm and makes
    // NlogN assigments shuttling the values between the two
    // arrays. The number of compares appears to vary between N-1 and
    // NlogN depending on the initial order but the main reason for
    // using it here is that, unlike qsort, it is stable.
    public void shuttlesort(int from[], int to[], int low, int high)
    {
        if (high - low < 2) {
            return;
        }
        int middle = (low + high)/2;
        shuttlesort(to, from, low, middle);
        shuttlesort(to, from, middle, high);

        int p = low;
        int q = middle;

        /* This is an optional short-cut; at each recursive call,
        check to see if the elements in this subset are already
        ordered.  If so, no further comparisons are needed; the
        sub-array can just be copied.  The array must be copied rather
        than assigned otherwise sister calls in the recursion might
        get out of sinc.  When the number of elements is three they
        are partitioned so that the first set, [low, mid), has one
        element and and the second, [mid, high), has two. We skip the
        optimisation when the number of elements is three or less as
        the first compare in the normal merge will produce the same
        sequence of steps. This optimisation seems to be worthwhile
        for partially ordered lists but some analysis is needed to
        find out how the performance drops to Nlog(N) as the initial
        order diminishes - it may drop very quickly.  */

        if (high - low >= 4 && compare(from[middle-1], from[middle]) <= 0) {
            for (int i = low; i < high; i++) {
                to[i] = from[i];
            }
            return;
        }

        // A normal merge. 

        for(int i = low; i < high; i++) {
            if (q >= high || (p < middle && compare(from[p], from[q]) <= 0)) {
                to[i] = from[p++];
            }
            else {
                to[i] = from[q++];
            }
        }
    }

    public void swap(int i, int j) {
        int tmp = indexes[i];
        indexes[i] = indexes[j];
        indexes[j] = tmp;
    }

    // The mapping only affects the contents of the data rows.
    // Pass all requests to these rows through the mapping array: "indexes".

  public Object getValueAt(int aRow, int aColumn)
  {
    checkModel();
    return model.getValueAt(indexes[aRow], aColumn);
  }
  
  public void setValueAt(Object aValue, int aRow, int aColumn)
  {
    checkModel();
    model.setValueAt(aValue, indexes[aRow], aColumn);
  }
  
  public void sortByColumn(int column)
  {
    sortByColumn(column, true);
  }
  
  public void sortByColumn(int column, boolean ascending)
  {
    this.ascending = ascending;
    sortingColumns.clear();
    sortingColumns.add(new ColumnComparator(column));
    sort(this);
    super.tableChanged(new TableModelEvent(this)); 
  }

    // There is no-where else to put this. 
    // Add a mouse listener to the Table to trigger a table sort 
    // when a column heading is clicked in the JTable. 
    public void addMouseListenerToHeaderInTable(JTable table)
    { 
      final TableSorter sorter = this; 
      final JTable tableView = table; 
      tableView.setColumnSelectionAllowed(false); 
      MouseAdapter listMouseListener = new MouseAdapter()
      {
        public void mouseClicked(MouseEvent e)
        {
          TableColumnModel columnModel = tableView.getColumnModel();
          int viewColumn = columnModel.getColumnIndexAtX(e.getX()); 
          int column = tableView.convertColumnIndexToModel(viewColumn); 
          if(e.getClickCount() == 1 && column != -1)
          {
            boolean ascending = true;
            if (e.getModifiers() == InputEvent.SHIFT_MASK)
              ascending = false;
            //If already sorted .. just reverse the old sort.
            if (FolderColumnRenderer.getSortColumn() == column)
            {
              ascending = !FolderColumnRenderer.getSortAscending();
            }
            FolderColumnRenderer.setSortOrder(column,ascending);
            sorter.sortByColumn(column, ascending); 
          }
        }
      };
      JTableHeader th = tableView.getTableHeader(); 
      th.setDefaultRenderer(new FolderColumnRenderer());
      th.addMouseListener(listMouseListener); 
    }

	public final int transformIndex(int original)
	{
		return indexes[original]; 
	}

}
