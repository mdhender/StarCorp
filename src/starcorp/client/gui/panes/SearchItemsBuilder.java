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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.ABuilderPane;
import starcorp.client.gui.ADataEntryWindow;
import starcorp.client.gui.windows.SearchItemsWindow;
import starcorp.common.entities.Colony;
import starcorp.common.types.AItemType;

/**
 * starcorp.client.gui.SearchItemsBuilder
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class SearchItemsBuilder extends ABuilderPane {
	private final SearchItemsWindow searchWindow;
	
	private Text txtName;
	private Combo typesCombo;
	private Combo coloniesCombo;
	private Text txtQty;
	
	public SearchItemsBuilder(ADataEntryWindow mainWindow) {
		super(mainWindow);
		this.searchWindow = (SearchItemsWindow) mainWindow;
		
	}

	@Override
	protected void createWidgets(List<Widget> widgets) {
		super.createWidgets(widgets);
		String show = "Showing " + searchWindow.countFilteredItems() + " of " + searchWindow.countAllItems();
		getParent().setText(show);
		
		txtName = createTextInput(getParent(), widgets, "Name:");
		if(searchWindow.getFilterName() != null) {
			txtName.setText(searchWindow.getFilterName());
		}
		txtName.setSize(100, 20);
		List<AItemType> types = AItemType.listTypes();
		typesCombo = createTypeSelection(getParent(), widgets, types, searchWindow.getFilterType(), "Type:");
		Set<Colony> colonies = searchWindow.getReport().getColonies();
		coloniesCombo = createEntitySelection(getParent(), widgets, colonies, searchWindow.getFilterColony(), "Colony:"); 
		txtQty = createIntegerInput(getParent(), widgets, "Min. Quantity:");
		if(searchWindow.getFilterQuantity() > 0) {
			txtQty.setText(String.valueOf(searchWindow.getFilterQuantity()));
		}
		txtQty.setSize(100, 20);

	}
	
	public Colony getFilterColony() {
		return (Colony) getComboValue(coloniesCombo);
	}
	
	public String getFilterName() {
		String filterName = txtName.getText();
		if(filterName != null && filterName.length() < 1) filterName = null;
		return filterName;
	}
	
	public int getFilterQuantity() {
		return getIntegerTextValue(txtQty);
	}
	
	public AItemType getFilterType() {
		return (AItemType) getComboValue(typesCombo);
	}
	
}
