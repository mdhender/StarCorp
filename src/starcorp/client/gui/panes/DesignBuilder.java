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

import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.ABuilderPane;
import starcorp.client.gui.windows.StarshipDesignWindow;
import starcorp.common.turns.Turn;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.AItemType;
import starcorp.common.types.Items;
import starcorp.common.types.OrderType;
import starcorp.common.types.StarshipHulls;

/**
 * starcorp.client.gui.panes.DesignBuilder
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 1 Oct 2007
 */
public class DesignBuilder extends ABuilderPane {
	private final StarshipDesignWindow designWindow;
	/**
	 * @param mainWindow
	 */
	public DesignBuilder(StarshipDesignWindow mainWindow) {
		super(mainWindow);
		this.designWindow = mainWindow;
	}
	@Override
	protected void createWidgets(List<Widget> widgets) {
		super.createWidgets(widgets);
		
		List<AItemType> types = AItemType.listTypes(StarshipHulls.class);
		Iterator<AItemType> i = types.iterator();
		while(i.hasNext()) {
			StarshipHulls type = (StarshipHulls) i.next();
			if(type.isCommand() || type.isCrew()) i.remove();
		}
		final Combo cType = createTypeSelection(getParent(), widgets, types, "Hull:");
		final Button btnAdd = createButton(getParent(), widgets, "Add Hull");
		btnAdd.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				AItemType type = (AItemType) getComboValue(cType);
				if(type != null)
					designWindow.addHull(type.getKey());
			}
		});
		final Button btnClear = createButton(getParent(), widgets, "Clear Hulls");
		btnClear.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				designWindow.clearHulls();
			}
		});
		final Text txtName = createTextInput(getParent(), widgets, "Name:");
		final Button btnOrder = createButton(getParent(), widgets, "Issue Design Order");
		btnOrder.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				designWindow.getMainWindow().openTurnWindow();
				Turn turn = designWindow.getMainWindow().getCurrentTurn();
				TurnOrder order = new TurnOrder(turn,OrderType.getType(OrderType.DESIGN_SHIP));
				order.add(txtName.getText());
				for(Items items : designWindow.getDesign().getHulls()) {
					for(int i = 0; i < items.getQuantity(); i++) {
						order.add(items.getType());
					}
				}
				designWindow.getMainWindow().addTurnOrder(order);
			}
		});
	}

}
