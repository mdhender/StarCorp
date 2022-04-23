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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.ABuilderPane;
import starcorp.client.gui.ADataEntryWindow;
import starcorp.client.gui.widgets.Hyperlink;
import starcorp.client.gui.windows.SearchMarketWindow;
import starcorp.common.entities.Colony;
import starcorp.common.types.AItemType;

/**
 * starcorp.client.gui.SearchMarketBuilder
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class SearchMarketBuilder extends ABuilderPane {
	private final SearchMarketWindow searchWindow;
	
	public SearchMarketBuilder(ADataEntryWindow mainWindow) {
		super(mainWindow);
		this.searchWindow = (SearchMarketWindow) mainWindow;
		
	}

	@Override
	protected void createWidgets(List<Widget> widgets) {
		super.createWidgets(widgets);
		String show = "Showing " + searchWindow.countFilteredItems() + " of " + searchWindow.countAllItems();
		getParent().setText(show);
		
		final Text txtName = createTextInput(getParent(), widgets, "Name:");
		if(searchWindow.getFilterName() != null) {
			txtName.setText(searchWindow.getFilterName());
		}
		txtName.setSize(100, 20);
		List<AItemType> types = AItemType.listTypes();
		final Combo typesCombo = createTypeSelection(getParent(), widgets, types, searchWindow.getFilterType(), "Type:");
		Set<Colony> colonies = searchWindow.getReport().getColonies();
		final Combo coloniesCombo = createEntitySelection(getParent(), widgets, colonies, searchWindow.getFilterColony(), "Colony:"); 
		final Text txtQty = createIntegerInput(getParent(), widgets, "Min. Quantity:");
		if(searchWindow.getFilterQuantity() > 0) {
			txtQty.setText(String.valueOf(searchWindow.getFilterQuantity()));
		}
		txtQty.setSize(100, 20);
		final Text txtPrice = createIntegerInput(getParent(), widgets, "Max. Price:");
		if(searchWindow.getFilterPrice() > 0) {
			txtPrice.setText(String.valueOf(searchWindow.getFilterPrice()));
		}
		
		final Button btnClear = createButton(getParent(), widgets, "Clear");
		btnClear.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				searchWindow.set(0, -1, "", null, null);
			}
		});

		final Button btnFilter = createButton(getParent(), widgets, "Filter");
		btnFilter.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				String filterName = txtName.getText();
				if(filterName != null && filterName.length() < 1) filterName = null;
				int filterQuantity = getIntegerTextValue(txtQty);
				int filterPrice = getIntegerTextValue(txtPrice);
				AItemType filterType = (AItemType) getComboValue(typesCombo);
				Colony filterColony = (Colony) getComboValue(coloniesCombo);
				System.out.println("Filter: name=" + filterName + " : qty=" + filterQuantity + " : type=" + filterType + " : colony=" + filterColony );
				searchWindow.set(filterQuantity, filterPrice, filterName, filterType, filterColony);
			}
		});
		
		
	}
	
}
