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
import starcorp.client.gui.ASearchWindow;
import starcorp.client.gui.ATablePane;
import starcorp.client.gui.panes.LawTable;
import starcorp.client.gui.panes.SearchLawBuilder;
import starcorp.common.entities.AGovernmentLaw;
import starcorp.common.entities.Colony;
import starcorp.common.entities.FacilityLease;
import starcorp.common.turns.TurnReport;

/**
 * starcorp.client.gui.SearchLawsWindow
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class SearchLawsWindow extends ASearchWindow {
	// TODO sorting by column
	private Colony filterColony;
	private long filterLicensee;
	private Class filterType;
	private List<AGovernmentLaw> filteredLaws;

	private final List<AGovernmentLaw> allLaws;
	
	private SearchLawBuilder builder; 
	
	public SearchLawsWindow(MainWindow mainWindow) {
		this(mainWindow,-1,null,null);
	}
	
	public SearchLawsWindow(MainWindow mainWindow, long filterLicensee, Colony filterColony, Class filterType) {
		super(mainWindow);
		this.filterLicensee = filterLicensee;
		this.filterColony = filterColony;
		this.filterType = filterType;
		this.allLaws = getReport().getLaws();
		this.filteredLaws = new ArrayList<AGovernmentLaw>(allLaws);
		filter();
	}

	public int countAllItems() {
		return allLaws.size();
	}
	
	public int countFilteredItems() {
		return (filteredLaws == null ? 0 : filteredLaws.size());
	}
	
	protected void filter() {
		if(filteredLaws == null) {
			filteredLaws = new ArrayList<AGovernmentLaw>();
		}
		else {
			filteredLaws.clear();
		}
		
		for(AGovernmentLaw law : allLaws) {
			boolean filter = false;
			long colonyId = law.getColony();
			
			if(!filter && filterType != null) {
				if(!(law.getClass().equals(filterType))) filter = true;
			}
			
			if(!filter && filterLicensee > -1) {
				if(law instanceof FacilityLease) {
					FacilityLease lease = (FacilityLease) law;
					if(lease.getLicensee() != filterLicensee) filter = true;
				}
			}
			
			if(!filter && filterColony != null) {
				if(filterColony.getID() != colonyId) filter = true;
			}
			
			if(!filter) {
				filteredLaws.add(law);
			}
		}
	}
	
	public void set(long filterLicensee, Class<?> filterType, Colony filterColony) {
		this.filterColony = filterColony;
		this.filterType = filterType;
		this.filterLicensee = filterLicensee;
		filter();
		setPage(1);
		reload();
	}
	
	public AGovernmentLaw get(int index) {
		return filteredLaws.get(index);
	}

	public Colony getFilterColony() {
		return filterColony;
	}

	public Class<?> getFilterType() {
		return filterType;
	}

	public long getFilterLicensee() {
		return filterLicensee;
	}

	public List<AGovernmentLaw> getAllLaws() {
		return allLaws;
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
		builder = new SearchLawBuilder(this);
		return builder;
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

	@Override
	public void clear() {
		set(-1, null, null);
	}

	@Override
	public void search() {
		set(builder.getFilterLicensee(), builder.getFilterType(), builder.getFilterColony());
		
	}

}
