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
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.ABuilderPane;
import starcorp.client.gui.ADataEntryWindow;
import starcorp.client.gui.widgets.Hyperlink;
import starcorp.client.gui.windows.SearchLawsWindow;
import starcorp.common.entities.Colony;

/**
 * starcorp.client.gui.SearchLawBuilder
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class SearchLawBuilder extends ABuilderPane {
	private final SearchLawsWindow searchWindow;
	
	public SearchLawBuilder(ADataEntryWindow mainWindow) {
		super(mainWindow);
		this.searchWindow = (SearchLawsWindow) mainWindow;
		
	}

	@Override
	protected void createWidgets(List<Widget> widgets) {
		super.createWidgets(widgets);
		String show = "Showing " + searchWindow.countFilteredItems() + " of " + searchWindow.countAllItems();
		getParent().setText(show);
		
		Set<Colony> colonies = searchWindow.getReport().getColonies();
		final Combo coloniesCombo = createEntitySelection(getParent(), widgets, colonies, searchWindow.getFilterColony(), "Colony:"); 

		final Button btnClear = createButton(getParent(), widgets, "Clear");
		btnClear.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				searchWindow.set(null);
			}
		});

		final Button btnFilter = createButton(getParent(), widgets, "Filter");
		btnFilter.addListener(SWT.Selection, new Listener() {
			public void handleEvent (Event event) {
				Colony filterColony = (Colony) getComboValue(coloniesCombo);
				searchWindow.set(filterColony);
			}
		});
		
		
	}
	
}
