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
import starcorp.client.gui.panes.LawTable;
import starcorp.client.gui.panes.SearchLawBuilder;
import starcorp.common.entities.AGovernmentLaw;
import starcorp.common.entities.Colony;
import starcorp.common.turns.TurnReport;

/**
 * starcorp.client.gui.SearchLawsWindow
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class SearchLawsWindow extends ADataEntryWindow {
	// TODO sorting by column
	public static final int ITEMS_PER_PAGE = 20;
	
	private int page;
	private Colony filterColony;
	private List<AGovernmentLaw> filteredLaws;

	private final List<AGovernmentLaw> allLaws;
	private final TurnReport report;
	
	public SearchLawsWindow(MainWindow mainWindow) {
		this(mainWindow,1,null);
	}
	
	public SearchLawsWindow(MainWindow mainWindow, int page, Colony filterColony) {
		super(mainWindow);
		this.page = page;
		this.filterColony = filterColony;
		this.report = mainWindow.getTurnReport();
		this.allLaws = report.getLaws();
		this.filteredLaws = new ArrayList<AGovernmentLaw>(allLaws);
	}

	public int countAllItems() {
		return allLaws.size();
	}
	
	public int countFilteredItems() {
		return (filteredLaws == null ? 0 : filteredLaws.size());
	}
	
	public int countPages() {
		int total;
		if(filteredLaws == null) {
			total = allLaws.size();
		}
		else {
			total = filteredLaws.size();
		}
		int pages = total / ITEMS_PER_PAGE;
		if(total % ITEMS_PER_PAGE > 0)
			pages++;
		return pages;
	}
	
	private void filter() {
		if(filteredLaws == null) {
			filteredLaws = new ArrayList<AGovernmentLaw>();
		}
		else {
			filteredLaws.clear();
		}
		
		for(AGovernmentLaw law : allLaws) {
			boolean filter = false;
			long colonyId = law.getColony();
			
			if(!filter && filterColony != null) {
				if(filterColony.getID() != colonyId) filter = true;
			}
			
			if(!filter) {
				filteredLaws.add(law);
			}
		}
	}
	
	public void set(Colony filterColony) {
		this.filterColony = filterColony;
		this.page = 1;
		filter();
		reload();
	}
	
	public AGovernmentLaw get(int index) {
		return filteredLaws.get(index);
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

	public Colony getFilterColony() {
		return filterColony;
	}

	public void setFilterColony(Colony filterColony) {
		this.filterColony = filterColony;
		filter();
		reload();
	}

	public List<AGovernmentLaw> getAllLaws() {
		return allLaws;
	}

	public TurnReport getReport() {
		return report;
	}

	public List<AGovernmentLaw> getFilteredLaws() {
		return filteredLaws;
	}

	@Override
	public void open(Composite parent) {
		super.open(parent);
		shell.setText("StarCorp: Laws");
	}
	
	@Override
	protected ABuilderPane createBuilder() {
		return new SearchLawBuilder(this);
	}

	@Override
	protected ATablePane createTable() {
		return new LawTable(this);
	}

	@Override
	protected void close() {
		super.close();
		mainWindow.searchLawsWindow = null;
	}
}
