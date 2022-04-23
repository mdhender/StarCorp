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
package starcorp.client.gui.panes;

import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.ADataEntryWindow;
import starcorp.client.gui.ATablePane;
import starcorp.client.gui.widgets.Hyperlink;
import starcorp.client.gui.windows.SearchItemsWindow;
import starcorp.client.gui.windows.SearchLawsWindow;
import starcorp.common.entities.AGovernmentLaw;
import starcorp.common.entities.Colony;

/**
 * starcorp.client.gui.LawTable
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class LawTable extends ATablePane {
	private final SearchLawsWindow searchWindow;
	
	public LawTable(ADataEntryWindow mainWindow) {
		super(mainWindow);
		this.searchWindow = (SearchLawsWindow) mainWindow;
	}

	@Override
	protected int countColumns() {
		return 2;
	}

	@Override
	protected String getColumnName(int index) {
		switch(index) {
		case 0 : return "Colony";
		case 1 : return "Description";
		}
		return "";
	}

	@Override
	protected boolean isEditable() {
		return false;
	}

	@Override
	public void populate() {
		int total = searchWindow.countFilteredItems();
		if(total < 1) {
			String[] values = {"No items found"};
			createRow(values);
		}
		else {
			int page = searchWindow.getPage();
			int start = (page - 1) * SearchItemsWindow.ITEMS_PER_PAGE;
			int end = start + SearchItemsWindow.ITEMS_PER_PAGE;
			if(start < 0) start = 0;
			if(end > total) end = total;
			for(int n = start; n < end; n++) {
				AGovernmentLaw law = searchWindow.get(n);
				if(law == null)
					return;
				Colony colony = searchWindow.getReport().getColony(law.getColony()); 
				String[] values = new String[countColumns()];
				values[0] = colony.getDisplayName();
				values[1] = law.getDisplayName();
				createRow(values);
			}
		}
	}

	@Override
	protected int getColumnWidth(int index) {
		switch(index) {
		case 0 : return 150;
		case 1 : return 300;
		}
		return super.getColumnWidth(index);
	}

	@Override
	protected void createWidgets(List<Widget> widgets) {
		super.createWidgets(widgets);
		if(searchWindow.countPages() > 1) { 
			Group grpPages = createGroup(getParent(), widgets, "Pages");
			grpPages.setLayout(new GridLayout(searchWindow.countPages(),true));
			for(int i = 1; i <= searchWindow.countPages(); i++) {
				if(i == searchWindow.getPage()) {
					createLabel(grpPages, widgets, String.valueOf(i));
				}
				else {
					Hyperlink lnk = createHyperlink(grpPages, widgets, String.valueOf(i));
					final int selected = i;
					lnk.addSelectionListener(new SelectionListener() {
						public void widgetDefaultSelected(SelectionEvent e) {
							searchWindow.setPage(selected);
						}
						public void widgetSelected(SelectionEvent e) {
							searchWindow.setPage(selected);
						}
					});
				}
			}
		}
	}
}
