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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.AEntityPane;
import starcorp.client.gui.windows.MainWindow;
import starcorp.common.entities.AColonists;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Facility;
import starcorp.common.entities.Workers;
import starcorp.common.turns.TurnOrder;

/**
 * starcorp.client.gui.panes.ColonistPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 28 Sep 2007
 */
public class ColonistPane extends AEntityPane {
	private final AColonists colonist;
	
	/**
	 * @param mainWindow
	 * @param entity
	 */
	public ColonistPane(MainWindow mainWindow, AColonists entity) {
		super(mainWindow, entity);
		this.colonist = entity;
	}
	
	@Override
	protected void createWidgets(List<Widget> widgets) {
		super.createWidgets(widgets);
		Group grp = createGroup(getParent(), widgets, "");
		GridLayout layout = new GridLayout(2,false);
		layout.marginWidth=20;
		layout.marginHeight=10;
		grp.setLayout(layout);
		
		createLabel(grp, widgets, "Colony:");
		Colony colony = getTurnReport().getColony(colonist.getColony());
		createColonyLink(grp, widgets, colony, null);
		
		createLabel(grp,widgets, "Type:");
		createLabel(grp,widgets, colonist.getPopClass().getName());
		
		createLabel(grp,widgets,"Quantity:");
		createLabel(grp,widgets,format(colonist.getQuantity()));

		createLabel(grp,widgets,"Happiness:");
		createLabel(grp,widgets,format(colonist.getHappiness()) + "%");
		
		if(colonist instanceof Workers) {
			final Workers w = (Workers) colonist;
			createLabel(grp, widgets, "Employer:");
			Facility f = getTurnReport().getFacility(w.getFacility());
			createFacilityLink(grp, widgets, f, null);
			
			final Text txtSalary = createIntegerInput(grp,widgets, "Salary: \u20a1");
			txtSalary.setText(String.valueOf(w.getSalary()));
			
			final Button btnSet = createButton(grp, widgets, "Set Salary");
			btnSet.addListener(SWT.Selection, new Listener() {
				public void handleEvent (Event event) {
					int salary = getIntegerTextValue(txtSalary);
					TurnOrder order = setSalaryOrder(w,salary);
					mainWindow.addTurnOrder(order);
				}
			});
		}
			
		
	}

}
