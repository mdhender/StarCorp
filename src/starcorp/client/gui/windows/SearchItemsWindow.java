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
import starcorp.client.gui.ATablePane;
import starcorp.client.gui.ASearchWindow;
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
public class SearchItemsWindow extends ASearchWindow {
	protected final List<ColonyItem> allItems;
	private int filterQuantity;
	private String filterName;
	private AItemType filterType;
	private Colony filterColony;
	private List<ColonyItem> filteredItems;

	private SearchItemsBuilder builder;
	
	public SearchItemsWindow(MainWindow mainWindow) {
		this(mainWindow,1,null,null,null);
	}
	
	public SearchItemsWindow(MainWindow mainWindow, int filterQuantity, String filterName, AItemType filterType, Colony filterColony) {
		super(mainWindow);
		this.filterQuantity = filterQuantity;
		this.filterName = filterName;
		this.filterType = filterType;
		this.filterColony = filterColony;
		this.allItems = getReport().getItems();
		filter();
	}

	protected void filter() {
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
			
			if(!filter) {
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
	@Override
	protected void close() {
		super.close();
		mainWindow.searchItemsWindow = null;
	}

	@Override
	protected ABuilderPane createBuilder() {
		builder = new SearchItemsBuilder(this);
		return builder;
	}

	@Override
	protected ATablePane createTable() {
		return new ItemsTable(this);
	}
	
	public void set(int filterQuantity, String filterName, AItemType filterType, Colony filterColony) {
		this.filterQuantity = filterQuantity;
		this.filterName = filterName;
		this.filterType = filterType;
		this.filterColony = filterColony;
		filter();
		setPage(1);
		reload();
	}
	
	public ColonyItem get(int index) {
		return filteredItems.get(index);
	}

	public String getFilterName() {
		return filterName;
	}

	public AItemType getFilterType() {
		return filterType;
	}

	public Colony getFilterColony() {
		return filterColony;
	}

	public List<ColonyItem> getAllItems() {
		return allItems;
	}

	public List<ColonyItem> getFilteredItems() {
		return filteredItems;
	}

	@Override
	public void open(Composite parent) {
		super.open(parent);
		String name = getReport().getTurn().getCorporation().getDisplayName();
		shell.setText("StarCorp: Items for " + name);
	}

	@Override
	public int countAllItems() {
		return allItems.size();
	}

	@Override
	public int countFilteredItems() {
		return filteredItems.size();
	}

	public int getFilterQuantity() {
		return filterQuantity;
	}

	@Override
	public void clear() {
		set(1, "", null, null);
	}

	@Override
	public void search() {
		set(builder.getFilterQuantity(), builder.getFilterName(), builder.getFilterType(), builder.getFilterColony());
	}
}
