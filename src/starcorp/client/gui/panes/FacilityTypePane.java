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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.ATypePane;
import starcorp.client.gui.windows.MainWindow;
import starcorp.common.types.AFacilityType;
import starcorp.common.types.Items;
import starcorp.common.types.Population;
import starcorp.common.types.PopulationClass;

/**
 * starcorp.client.gui.panes.FacilityTypePane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 26 Sep 2007
 */
public class FacilityTypePane extends ATypePane {
	private final AFacilityType type;
	
	/**
	 * @param mainWindow
	 * @param type
	 */
	public FacilityTypePane(MainWindow mainWindow, AFacilityType type) {
		super(mainWindow, type);
		this.type = type;
	}
	
	@Override
	protected void createWidgets(List<Widget> widgets) {
		super.createWidgets(widgets);
		Group grp = createGroup(getParent(), widgets, "");
		GridLayout layout = new GridLayout(2,false);
		layout.marginWidth=20;
		layout.marginHeight=10;
		grp.setLayout(layout);
		
		createLabel(grp, widgets, "Category:");
		createLabel(grp, widgets, type.getCategory());
		
		createLabel(grp, widgets, "Sub-Category:");
		createLabel(grp, widgets, type.getSubCategory());
		
		createLabel(grp,widgets,"Power Required:");
		createLabel(grp, widgets, format(type.getPowerRequirement()));
		
		Group grpWorkers = createGroup(getParent(), widgets, "Required Workers");
		GridData data = new GridData();
		data.minimumWidth=200;
		data.grabExcessHorizontalSpace=true;
		data.horizontalSpan=2;
		grpWorkers.setLayoutData(data);
		grpWorkers.setLayout(new GridLayout(1,true));
		
		for(PopulationClass popClass : type.getWorkerRequirement().keySet()) {
			Population pop = type.getWorkerRequirement(popClass);
			createLabel(grpWorkers, widgets, pop.toString());
		}
		
		Group grpBuilding = createGroup(getParent(), widgets, "Building Modules");
		grpBuilding.setLayoutData(data);
		grpBuilding.setLayout(new RowLayout(SWT.VERTICAL));
		
		for(Items item : type.getBuildingRequirement()) {
			createItemLink(grpBuilding, widgets, item, null);
		}
	}

}
