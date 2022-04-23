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
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import starcorp.client.gui.AEntityPane;
import starcorp.client.gui.windows.MainWindow;
import starcorp.common.entities.AColonists;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Facility;
import starcorp.common.entities.FactoryQueueItem;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.AItemType;
import starcorp.common.types.Factory;

/**
 * starcorp.client.gui.FacilityPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class FacilityPane extends AEntityPane {
	private Facility facility;
	
	public FacilityPane(MainWindow mainWindow, Facility facility) {
		super(mainWindow, facility);
		this.facility = facility;
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
		Colony colony = getTurnReport().getColony(facility.getColony());
		createColonyLink(grp, widgets, colony, null);

		createLabel(grp, widgets, "Owner:");
		Corporation corp = getTurnReport().getCorporation(facility.getOwner());
		createCorporationLink(grp, widgets, corp, null);

		createLabel(grp, widgets, "Type:");
		createFacilityTypeLink(grp, widgets, facility.getTypeClass(), null);

		createLabel(grp, widgets, "Open:");
		createLabel(grp, widgets, facility.isOpen() ? "yes" : "no");

		createLabel(grp, widgets, "Powered:");
		createLabel(grp, widgets, facility.isPowered() ? "yes" : "no");

		createLabel(grp, widgets, "Built:");
		createLabel(grp, widgets, facility.getBuiltDate().toString());

		if(facility.getServiceCharge() > 0) {
			createLabel(grp, widgets, "Service Charge:");
			createLabel(grp, widgets, "\u20a1 " + format(facility.getServiceCharge()));
		}
		
		Corporation player = getTurnReport().getTurn().getCorporation();
		if(player.getID() == facility.getOwner()) {
			Group grpEmployees = createGroup(getParent(), widgets, "Employees");
			grpEmployees.setLayout(new GridLayout(1,false));
			
			Set<AColonists> employees = getTurnReport().getEmployees(facility);
			double efficiency = facility.getEfficiency(employees);

			for(AColonists colonist : employees) {
				createColonistLink(grpEmployees, widgets, colonist, null);
			}
			
			createLabel(grpEmployees, widgets, "Efficiency: " + format(efficiency) + "%");

			if(facility.getTypeClass() instanceof Factory) {
				Group grpOrders = createGroup(getParent(), widgets, "Factory Orders");
				grpOrders.setLayout(new GridLayout(5,false));
				List<AItemType> types = ((Factory)facility.getTypeClass()).canBuild();
				final Combo c = createTypeSelection(grpOrders, widgets, types, "Item:");
				final Text txt = createIntegerInput(grpOrders, widgets, "Quantity:");
				Button add = createButton(grpOrders, widgets, "Add");
				add.addListener(SWT.Selection, new Listener() {
					public void handleEvent (Event event) {
						AItemType type = (AItemType) getComboValue(c);
						int qty = getIntegerTextValue(txt);
						TurnOrder order = buildOrder(facility, type, qty);
						mainWindow.addTurnOrder(order);
					}
				});
				List<FactoryQueueItem> queue = getTurnReport().getQueue(facility);
				if(queue.size() > 0) {
					Group grpQueue = createGroup(getParent(), widgets, "Factory Queue");
					grpQueue.setLayout(new GridLayout(2,false));
					
					for(int i = (queue.size() - 1); i > -1; i--) {
						FactoryQueueItem item = queue.get(i);
						createLabel(grpQueue, widgets, item.getPosition() + ":");
						createItemLink(grpQueue, widgets, item.getItem(), null);
					}
				}
			}
		}
		
	}
}
