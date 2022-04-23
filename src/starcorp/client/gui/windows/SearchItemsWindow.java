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
package starcorp.client.gui.windows;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.widgets.Composite;

import starcorp.client.gui.ABuilderPane;
import starcorp.client.gui.ADataEntryWindow;
import starcorp.client.gui.ATablePane;
import starcorp.client.gui.panes.ItemsTable;
import starcorp.client.gui.panes.SearchItemsBuilder;
import starcorp.common.entities.Colony;
import starcorp.common.entities.ColonyItem;
import starcorp.common.turns.TurnReport;
import starcorp.common.types.AItemType;
import starcorp.common.types.Items;

/**
 * starcorp.client.gui.SearchItemsWindow
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class SearchItemsWindow extends ADataEntryWindow {
	// TODO sorting by column
	public static final int ITEMS_PER_PAGE = 20;
	
	private int page;
	private int filterQuantity;
	private String filterName;
	private AItemType filterType;
	private Colony filterColony;
	private List<ColonyItem> filteredItems;

	private final List<ColonyItem> allItems;
	private final TurnReport report;
	
	public SearchItemsWindow(MainWindow mainWindow) {
		this(mainWindow,1,0,null,null,null);
	}
	
	public SearchItemsWindow(MainWindow mainWindow, int page, int filterQuantity, String filterName, AItemType filterType, Colony filterColony) {
		super(mainWindow);
		this.page = page;
		this.filterQuantity = filterQuantity;
		this.filterName = filterName;
		this.filterType = filterType;
		this.filterColony = filterColony;
		this.report = mainWindow.getTurnReport();
		this.allItems = report.getItems();
		this.filteredItems = new ArrayList<ColonyItem>(allItems);
	}

	@Override
	protected ABuilderPane createBuilder() {
		return new SearchItemsBuilder(this);
	}

	@Override
	protected ATablePane createTable() {
		return new ItemsTable(this);
	}
	
	public int countAllItems() {
		return allItems.size();
	}
	
	public int countFilteredItems() {
		return (filteredItems == null ? 0 : filteredItems.size());
	}
	
	public int countPages() {
		int total;
		if(filteredItems == null) {
			total = allItems.size();
		}
		else {
			total = filteredItems.size();
		}
		int pages = total / ITEMS_PER_PAGE;
		if(total % ITEMS_PER_PAGE > 0)
			pages++;
		return pages;
	}
	
	private void filter() {
		if(filteredItems == null) {
			filteredItems = new ArrayList<ColonyItem>();
		}
		else {
			filteredItems.clear();
		}
		
		for(ColonyItem item : allItems) {
			boolean filter = false;
			Items items = item.getItem();
			AItemType type = items.getTypeClass();
			int quantity = items.getQuantity();
			long colonyId = item.getColony();
			String name = type.getName();
			
			if(!filter && filterQuantity > 0) {
				if(quantity < filterQuantity) filter = true;
			}
			if(!filter && filterName != null) {
				if(name.indexOf(filterName) == -1) filter = true;
			}
			if(!filter && filterType != null) {
				if(!filterType.equals(type)) filter = true;
			}
			if(!filter && filterColony != null) {
				if(filterColony.getID() != colonyId) filter = true;
			}
			
			if(!filter) {
				filteredItems.add(item);
			}
		}
	}
	
	public void set(int filterQuantity, String filterName, AItemType filterType, Colony filterColony) {
		this.filterQuantity = filterQuantity;
		this.filterName = filterName;
		this.filterType = filterType;
		this.filterColony = filterColony;
		this.page = 1;
		filter();
		reload();
	}
	
	@Override
	protected void close() {
		super.close();
		mainWindow.searchItemsWindow = null;
	}
	
	public ColonyItem get(int index) {
		return filteredItems.get(index);
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		if(page > countPages()) {
			page = countPages();
		}
		else if(page < 1) {
			page = 1;
		}
		this.page = page;
		reload();
	}

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
		filter();
		reload();
	}

	public AItemType getFilterType() {
		return filterType;
	}

	public void setFilterType(AItemType filterType) {
		this.filterType = filterType;
		filter();
		reload();
	}

	public Colony getFilterColony() {
		return filterColony;
	}

	public void setFilterColony(Colony filterColony) {
		this.filterColony = filterColony;
		filter();
		reload();
	}

	public List<ColonyItem> getAllItems() {
		return allItems;
	}

	public TurnReport getReport() {
		return report;
	}

	public List<ColonyItem> getFilteredItems() {
		return filteredItems;
	}

	public int getFilterQuantity() {
		return filterQuantity;
	}

	public void setFilterQuantity(int filterQuantity) {
		this.filterQuantity = filterQuantity;
		filter();
		reload();
	}

	@Override
	public void open(Composite parent) {
		super.open(parent);
		String name = report.getTurn().getCorporation().getDisplayName();
		shell.setText("StarCorp: Items for " + name);
	}
}
