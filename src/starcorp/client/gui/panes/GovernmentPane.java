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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.AEntityPane;
import starcorp.client.gui.widgets.Hyperlink;
import starcorp.client.gui.windows.MainWindow;
import starcorp.client.gui.windows.SearchItemsWindow;
import starcorp.client.gui.windows.SearchLawsWindow;
import starcorp.client.gui.windows.SearchMarketWindow;
import starcorp.common.entities.AColonists;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Planet;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.AFacilityType;
import starcorp.common.types.PopulationClass;

/**
 * starcorp.client.gui.GovernmentPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class GovernmentPane extends AEntityPane {
	private final Colony colony;
	
	public GovernmentPane(MainWindow mainWindow, Colony entity) {
		super(mainWindow, entity);
		this.colony = entity;
	}

	@Override
	protected void createWidgets(List<Widget> widgets) {
		super.createWidgets(widgets);
		
		Group grp = createGroup(getParent(), widgets, ""); 
		grp.setLayout(new GridLayout(2,false));
		GridData data = new GridData();
		data.horizontalSpan=2;
		grp.setLayoutData(data);
		
		createLabel(grp, widgets, "Hazard Level: " + format(colony.getHazardLevel()));
		createLabel(grp, widgets, "Founded: " + colony.getFoundedDate());
		
		Planet planet = getTurnReport().getPlanet(colony.getPlanet());
		createLabel(grp,widgets,"Location: ");
		createPlanetLink(grp, widgets, planet, planet.getDisplayName() + " @ " + colony.getLocation());

		Hyperlink lnkLaws = createHyperlink(grp, widgets, "View Laws");
		lnkLaws.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				SearchLawsWindow window = mainWindow.openSearchLawWindow();
				window.set(-1, null, colony);
			}
			public void widgetSelected(SelectionEvent e) {
				SearchLawsWindow window = mainWindow.openSearchLawWindow();
				window.set(-1, null, colony);
			}
		});
		
		Hyperlink lnkMarket = createHyperlink(grp, widgets, "View Market");
		lnkMarket.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				SearchMarketWindow window = mainWindow.openSearchMarketWindow();
				window.set(0,-1,null,null,colony);
			}
			public void widgetSelected(SelectionEvent e) {
				SearchMarketWindow window = mainWindow.openSearchMarketWindow();
				window.set(0,-1,null,null,colony);
			}
		});
		
		Hyperlink lnkItems = createHyperlink(grp, widgets, "View Items");
		lnkItems.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent e) {
				SearchItemsWindow window = mainWindow.openSearchItemsWindow();
				window.set(0,null,null,colony);
			}
			public void widgetSelected(SelectionEvent e) {
				SearchItemsWindow window = mainWindow.openSearchItemsWindow();
				window.set(0,null,null,colony);
			}
		});
		data = new GridData();
		data.horizontalSpan=2;
		lnkItems.setLayoutData(data);
		
		createUnemployed(widgets);
		createActions(widgets);
	}
	
	private void createUnemployed(List<Widget> widgets) {
		Group grp = createGroup(getParent(), widgets, "Unemployed");
		GridLayout layout = new GridLayout(3,false);
		grp.setLayout(layout);
		GridData data = new GridData();
		data.horizontalSpan=2;
		grp.setLayoutData(data);
		
		for(AColonists colonist : getTurnReport().getUnemployed(colony.getID())) {
			createColonistLink(grp, widgets, colonist, null);
		}
	}
	
	private void createActions(List<Widget> widgets) {
		Group grp = createGroup(getParent(), widgets, "Issue Laws");
		GridLayout layout = new GridLayout(4,false);
		grp.setLayout(layout);
		GridData data = new GridData();
		data.horizontalSpan=2;
		grp.setLayoutData(data);
		
		List<AFacilityType> types = AFacilityType.listTypes();
		final Combo facilityTypes = createTypeSelection(grp, widgets, types, "Facility:");
		data = new GridData();
		data.horizontalSpan=3;
		facilityTypes.setLayoutData(data);
		final Text txtPrice = createIntegerInput(grp, widgets, "Price:");
		final Button checkbox = createCheckbox(grp, widgets, "For Self Only:");
		final Button issueLease = createButton(grp, widgets, "Issue Lease");
		issueLease.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				AFacilityType type = (AFacilityType) getComboValue(facilityTypes);
				int price = getIntegerTextValue(txtPrice);
				boolean forSelf = checkbox.getSelection();
				TurnOrder order = issueLease(colony, type, price, forSelf);
				mainWindow.addTurnOrder(order);
			}
		});
		data = new GridData();
		data.horizontalSpan=2;
		issueLease.setLayoutData(data);
		final Button issueGrant = createButton(grp, widgets, "Issue Grant");
		issueGrant.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				AFacilityType type = (AFacilityType) getComboValue(facilityTypes);
				int price = getIntegerTextValue(txtPrice);
				TurnOrder order = issueGrant(colony, type, price);
				mainWindow.addTurnOrder(order);
			}
		});
		data = new GridData();
		data.horizontalSpan=2;
		issueGrant.setLayoutData(data);
		
		List<PopulationClass> popClasses = PopulationClass.listTypes(); 
		final Combo popClass = createTypeSelection(grp, widgets, popClasses, "Population:");
		final Text txtCredits = createIntegerInput(grp, widgets, "Credits:");
		final Button issueColonistGrant = createButton(grp, widgets, "Issue Grant");
		issueColonistGrant.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				PopulationClass type = (PopulationClass) getComboValue(popClass);
				int credits = getIntegerTextValue(txtCredits);
				TurnOrder order = issueGrant(colony, type, credits);
				mainWindow.addTurnOrder(order);
			}
		});
		
		data = new GridData();
		data.horizontalSpan=4;
		issueColonistGrant.setLayoutData(data);
		
	}

}
