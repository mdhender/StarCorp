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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.AEntityPane;
import starcorp.client.gui.widgets.Hyperlink;
import starcorp.client.gui.windows.MainWindow;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.IEntity;
import starcorp.common.entities.MarketItem;
import starcorp.common.entities.Planet;
import starcorp.common.entities.Starship;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.AItemType;

/**
 * starcorp.client.gui.ColonyPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class ColonyPane extends AEntityPane {
	private static final int MARKET_ITEMS_PER_PAGE = 10;
	private final int page;
	private final Colony colony;
	
	public ColonyPane(MainWindow mainWindow, Colony entity) {
		this(mainWindow,entity,1);
	}
	
	public ColonyPane(MainWindow mainWindow, Colony entity, int page) {
		super(mainWindow, entity);
		this.page = page;
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
		
		createLabel(grp, widgets, "Government:");
		Corporation c = getTurnReport().getCorporation(colony.getGovernment());
		createCorporationLink(grp, widgets, c, null);
		
		createLabel(grp, widgets, "Hazard Level: " + format(colony.getHazardLevel()));
		createLabel(grp, widgets, "Founded: " + colony.getFoundedDate());
		
		Planet planet = getTurnReport().getPlanet(colony.getPlanet());
		createLabel(grp,widgets,"Location: ");
		createPlanetLink(grp, widgets, planet, planet.getDisplayName() + " @ " + colony.getLocation());

		createColonyMarket(getParent(), widgets);

		// TODO actions to move a starship to this location
	}

	protected void loadPage(int page) {
		System.out.println("loadPage: " + page);
		window.getMainWindow().set(new ColonyPane(window.getMainWindow(),colony,page));
		window.getMainWindow().focus();
	}
	
	protected Group createColonyMarket(Composite parent, List<Widget> widgets) {
		Group grp = createGroup(parent, widgets, "Colony Market");
		grp.setLayout(new GridLayout(4,false));
		
		createLabel(grp, widgets, "Item");
		createLabel(grp, widgets, "Qty");
		createLabel(grp,widgets, "Price (ea.)");
		createLabel(grp, widgets, "");
		
		List<MarketItem> set = getTurnReport().getMarketByColony().get(colony.getID());
		if(set != null) {
			Iterator<MarketItem> i = set.iterator();
			for(int count = 0; i.hasNext() && count < (MARKET_ITEMS_PER_PAGE * (page - 1));count++) {
				i.next();
			}
			GridData data = new GridData(80,20);
			final Button[] checkboxes = new Button[MARKET_ITEMS_PER_PAGE];
			for(int n = 0; n < MARKET_ITEMS_PER_PAGE; n++) {
				MarketItem item = i.hasNext() ? i.next() : null;
				if(item == null)
					continue;
				AItemType type = item.getItem().getTypeClass();
				String name = item.getItem().getTypeClass().getName();
				String qty = format(item.getItem().getQuantity());
				String price = "\u20a1 " + format(item.getCostPerItem());
				createItemLink(grp, widgets, type, name); // 1
				createLabel(grp, widgets, qty).setLayoutData(data); // 2
				createLabel(grp,widgets, price).setLayoutData(data); // 3
				if(item != null && mainWindow.getCorporation() != null) {
					checkboxes[n] = createCheckbox(grp, widgets, null); // 4
					checkboxes[n].setData(item);
					checkboxes[n].setLayoutData(data);
				}
			}
			int totalPages = set.size() / MARKET_ITEMS_PER_PAGE;
			if(set.size() % MARKET_ITEMS_PER_PAGE > 0) totalPages++;
			Group grpPages = createGroup(grp, widgets, "Pages");
			GridLayout pagesLayout = new GridLayout(totalPages,true);
			pagesLayout.makeColumnsEqualWidth=true;
			pagesLayout.marginWidth=5;
			pagesLayout.marginHeight=5;
			pagesLayout.horizontalSpacing=5;
			grpPages.setLayout(pagesLayout);
			data = new GridData(SWT.CENTER,SWT.TOP,true,true);
			data.horizontalSpan=4;
			data.grabExcessHorizontalSpace=true;
			data.horizontalAlignment=SWT.CENTER;
			grpPages.setLayoutData(data);
			for(int n = 1; n <= totalPages; n++) {
				if(n == page) {
					createLabel(grpPages, widgets, format(n));
				}
				else {
					Hyperlink lnk = createHyperlink(grpPages, widgets, format(n));
					final int selected = n;
					lnk.addSelectionListener(new SelectionListener() {
						public void widgetDefaultSelected(SelectionEvent e) {
							loadPage(selected);
						}
						public void widgetSelected(SelectionEvent e) {
							loadPage(selected);
						}
					});
				}
			}
			if(mainWindow.getCorporation() != null) {
				Group grpBuy = createGroup(grp, widgets, "Buy Checked");
				grpBuy.setLayout(new GridLayout(4,false));
				grpBuy.setLayoutData(data);
				
				List<IEntity> entities = new ArrayList<IEntity>();
				entities.add(mainWindow.getCorporation());
				for(Starship ship : getTurnReport().getPlayerStarshipsInOrOrbitingColony(colony)) {
					entities.add(ship);
				}
				
				final Combo c = createEntitySelection(grpBuy, widgets, entities, null);
				c.select(0);
				final Text txt = createIntegerInput(grpBuy, widgets, "Quantity:");
				final Button btn = createButton(grpBuy, widgets, "Buy");
				btn.addListener(SWT.Selection, new Listener() {
					private List<MarketItem> getChecked() {
						List<MarketItem> list = new ArrayList<MarketItem>();
						for(int i = 0; i < MARKET_ITEMS_PER_PAGE; i ++) {
							if(checkboxes[i] != null && checkboxes[i].getSelection()) {
								list.add((MarketItem)checkboxes[i].getData());
							}
						}
						return list;
					}
					private boolean isShipSelected() {
						return getComboValue(c) instanceof Starship;
					}
					private int getQuantity() {
						return getIntegerTextValue(txt);
					}
					public void handleEvent (Event event) {
						List<TurnOrder> list = new ArrayList<TurnOrder>();
						for(MarketItem item : getChecked()) {
							TurnOrder order;
							if(isShipSelected()) {
								order = buyOrder(item, (Starship) getComboValue(c), getQuantity());
							}
							else {
								order = buyOrder(item, getQuantity());
							}
							list.add(order);
						}
						mainWindow.addTurnOrders(list);
					}
				});
				
			}
		}
		return grp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + page;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ColonyPane other = (ColonyPane) obj;
		if (page != other.page)
			return false;
		return true;
	}

}
