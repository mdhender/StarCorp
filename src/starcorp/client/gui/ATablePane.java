/**
 *  Copyright 2007 Seyed Razavi
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. 
 *  You may obtain a copy of the License at 
 *
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and limitations under the License. 
 */
package starcorp.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * starcorp.client.gui.ATablePane
 * 
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public abstract class ATablePane extends AWindowPane {

	private Table table;
	private List<TableColumn> columns = new ArrayList<TableColumn>();
	private List<TableItem> items = new ArrayList<TableItem>();
	
	public ATablePane(AWindow mainWindow) {
		super(mainWindow);
	}
	
	public void dispose() {
		for(TableItem item : items) {
			if(!item.isDisposed())
				item.dispose();
		}
		for(TableColumn c : columns) {
			if(!c.isDisposed())
				c.dispose();
		}
		table.dispose();
		super.dispose();
	}
	
	protected int getTableStyle() {
		return SWT.BORDER | SWT.MULTI | SWT.CHECK;
	}
	
	protected boolean isHeaderVisible() {
		return true;
	}
	
	protected boolean isLinesVisible() {
		return true;
	}
	

	protected void createWidgets(List<Widget> widgets) {
		getParent().setLayout(new RowLayout(SWT.VERTICAL));
		table = new Table(getParent(),getTableStyle());
		table.setHeaderVisible(isHeaderVisible());
		table.setLinesVisible(isLinesVisible());
		for(int i = 0; i < countColumns(); i++) {
			TableColumn col = new TableColumn(table, SWT.CENTER);
			col.setText(getColumnName(i));
			col.setWidth(getColumnWidth(i));
			columns.add(col);
		}
		populate();
		
		if(isEditable()) {
			createEditor();
		}
	}
	
	protected int getColumnWidth(int index) {
		return 80;
	}
	
	private void createEditor() {
		 final TableEditor editor = new TableEditor(table);
		    editor.horizontalAlignment = SWT.LEFT;
		    editor.grabHorizontal = true;
		    table.addListener(SWT.MouseDown, new Listener() {
		      public void handleEvent(Event event) {
		        Rectangle clientArea = table.getClientArea();
		        Point pt = new Point(event.x, event.y);
		        int index = table.getTopIndex();
		        while (index < table.getItemCount()) {
		          boolean visible = false;
		          final TableItem item = table.getItem(index);
		          for (int i = 0; i < table.getColumnCount(); i++) {
		            Rectangle rect = item.getBounds(i);
		            if (rect.contains(pt)) {
		              final int row = index;
		              final int column = i;
		              final Text text = new Text(table, SWT.NONE);
		              Listener textListener = new Listener() {
		                public void handleEvent(final Event e) {
		                  switch (e.type) {
		                  case SWT.FocusOut:
		                    item.setText(column, text.getText());
		                    columnEdited(row, column, text.getText());
		                    text.dispose();
		                    break;
		                  case SWT.Traverse:
		                    switch (e.detail) {
		                    case SWT.TRAVERSE_RETURN:
		                      item
		                          .setText(column, text
		                              .getText());
		                    // FALL THROUGH
		                    case SWT.TRAVERSE_ESCAPE:
		                      text.dispose();
		                      e.doit = false;
		                    }
		                    break;
		                  }
		                }
		              };
		              text.addListener(SWT.FocusOut, textListener);
		              text.addListener(SWT.Traverse, textListener);
		              editor.setEditor(text, item, i);
		              text.setText(item.getText(i));
		              text.selectAll();
		              text.setFocus();
		              return;
		            }
		            if (!visible && rect.intersects(clientArea)) {
		              visible = true;
		            }
		          }
		          if (!visible)
		            return;
		          index++;
		        }
		      }
		    });
	}
	
	protected void columnEdited(int row, int column, String value) {
		// do nothing
	}

	protected TableItem createRow(String[] values) {
		final TableItem item = new TableItem(table,SWT.NONE);
		item.setText(values);
		items.add(item);
		return item;
	}
	
	public void delete(int row) {
		table.remove(row);
		items.remove(row);
	}
	
	public void clear() {
		table.removeAll();
		items.clear();
	}
	
	protected void createTableEditor(Control c, int row, int column) {
		final TableItem item = items.get(row);
		final TableEditor editor = new TableEditor(table);
		editor.grabHorizontal=true;
		editor.minimumHeight=c.getSize().y;
		editor.minimumWidth=c.getSize().x;
		editor.setEditor(c,item,column);
	}
	
	protected boolean isEditable() {
		return true;
	}
	
	public void populate() {}
	
	protected abstract int countColumns();
	
	protected abstract String getColumnName(int index);
	
	protected Table getTable() {
		return table;
	}
	
	protected TableItem row(int index) {
		return items.get(index);
	}
	
	protected TableColumn column(int index) {
		return columns.get(index);
	}

	public void redraw() {
		table.pack();
		table.redraw();
	}

	public Point computeSize() {
		return table.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	}
	
	public List<Integer> getChecked() {
		List<Integer> checked = new ArrayList<Integer>();
		TableItem[] items = table.getItems();
		int i = 0;
		for(TableItem item : items) {
			if(item.getChecked()) {
				checked.add(i);
			}
			i++;
		}
		return checked;
	}

}
