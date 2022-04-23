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
package starcorp.client.gui;

import java.util.List;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.windows.MainWindow;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Facility;
import starcorp.common.entities.IEntity;
import starcorp.common.entities.MarketItem;
import starcorp.common.entities.Planet;
import starcorp.common.entities.StarSystem;
import starcorp.common.entities.StarSystemEntity;
import starcorp.common.entities.Starship;
import starcorp.common.entities.StellarAnomoly;
import starcorp.common.entities.Workers;
import starcorp.common.turns.TurnOrder;
import starcorp.common.turns.TurnReport;
import starcorp.common.types.AFacilityType;
import starcorp.common.types.AItemType;
import starcorp.common.types.OrderType;
import starcorp.common.types.PopulationClass;

/**
 * starcorp.client.gui.AEntityPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public abstract class AEntityPane extends ADataPane {

	private final IEntity entity;
	private Group entityGroup;
	
	public AEntityPane(MainWindow mainWindow, IEntity entity) {
		super(mainWindow);
		this.entity = entity;
	}

	@Override
	protected void createWidgets(List<Widget> widgets) {
		entityGroup = createGroup(super.getParent(), widgets, entity.getDisplayName());
		GridLayout layout = new GridLayout(2,false);
		layout.marginWidth=20;
		layout.marginHeight=10;
		entityGroup.setLayout(layout);
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
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
		final AEntityPane other = (AEntityPane) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		return true;
	}
	
	protected TurnReport getTurnReport() {
		return mainWindow.getTurnReport();
	}
	
	protected TurnOrder issueGrant(Colony colony, PopulationClass type, int credits) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.ISSUE_COLONIST_GRANT));
		order.add(colony.getID());
		order.add(type.getKey());
		order.add(credits);
		return order;
	}

	protected TurnOrder issueGrant(Colony colony, AFacilityType type, int credits) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.ISSUE_DEVELOPMENT_GRANT));
		order.add(colony.getID());
		order.add(type.getKey());
		order.add(credits);
		return order;
	}
	
	protected TurnOrder issueLease(Colony colony, AFacilityType type, int price, boolean forSelf) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.ISSUE_LEASE));
		order.add(colony.getID());
		order.add(type.getKey());
		order.add(price);
		if(forSelf) {
			Corporation corp = getTurnReport().getTurn().getCorporation();
			order.add(corp.getID());
		}
		return order;
	}

	protected TurnOrder jettisonOrder(Starship ship, AItemType type, int qty) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.JETTISON_ITEM));
		order.add(ship.getID());
		order.add(type.getKey());
		order.add(qty);
		return order;
	}

	protected TurnOrder sellOrder(Colony colony, AItemType type, int qty, int price) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.CORP_SELL_ITEM));
		order.add(colony.getID());
		order.add(type.getKey());
		order.add(qty);
		order.add(price);
		return order;
	}
	
	protected TurnOrder sellOrder(Starship ship, long colony, AItemType type, int qty, int price) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.SHIP_SELL_ITEM));
		order.add(ship.getID());
		order.add(colony);
		order.add(type.getKey());
		order.add(qty);
		order.add(price);
		return order;
	}

	protected TurnOrder mineGasFieldOrder(Starship ship, StarSystemEntity gasfield) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.MINE_GAS_FIELD));
		order.add(ship.getID());
		order.add(gasfield.getID());
		return order;
	}

	protected TurnOrder mineAsteroidOrder(Starship ship, StarSystemEntity asteroid) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.MINE_ASTEROID));
		order.add(ship.getID());
		order.add(asteroid.getID());
		return order;
	}

	protected TurnOrder investigateOrder(Starship ship, StellarAnomoly anomoly) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.INVESTIGATE));
		order.add(ship.getID());
		order.add(anomoly.getID());
		return order;
	}
	
	protected TurnOrder scanSystemOrder(Starship ship) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.SCAN_SYSTEM));
		order.add(ship.getID());
		return order;
	}

	protected TurnOrder scanGalaxyOrder(Starship ship) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.SCAN_GALAXY));
		order.add(ship.getID());
		return order;
	}

	protected TurnOrder prospectOrder(Starship ship) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.PROSPECT));
		order.add(ship.getID());
		return order;
	}
	
	protected TurnOrder probeSystemOrder(Starship ship, StarSystemEntity entity) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.PROBE_SYSTEM));
		order.add(ship.getID());
		order.add(entity.getID());
		return order;
	}
	
	protected TurnOrder probePlanetOrder(Starship ship, Planet planet) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.PROBE_PLANET));
		order.add(ship.getID());
		order.add(planet.getID());
		return order;
	}
	
	protected TurnOrder jumpOrder(Starship ship, StarSystem system) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.JUMP));
		order.add(ship.getID());
		if(system != null)
			order.add(system.getID());
		return order;
	}

	protected TurnOrder orbitOrder(Starship ship, Planet planet) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.ORBIT));
		order.add(ship.getID());
		order.add(planet.getID());
		return order;
	}
	
	protected TurnOrder leaveOrbitOrder(Starship ship) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.LEAVE_ORBIT));
		order.add(ship.getID());
		return order;
	}
	
	protected TurnOrder moveOrder(Starship ship, int quadrant, int orbit) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.MOVE));
		order.add(ship.getID());
		order.add(quadrant);
		order.add(orbit);
		return order;
	}
	
	protected TurnOrder takeOffOrder(Starship ship) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.TAKE_OFF));
		order.add(ship.getID());
		return order;
	}

	protected TurnOrder dockOrder(Starship ship, Colony colony) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.DOCK_COLONY));
		order.add(ship.getID());
		order.add(colony.getID());
		return order;
	}
	
	protected TurnOrder dockOrder(Starship ship, int x, int y) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.DOCK_PLANET));
		order.add(ship.getID());
		order.add(x);
		order.add(y);
		return order;
	}

	protected TurnOrder buildOrder(Facility facility, AItemType type, int qty) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.FACTORY_BUILD));
		order.add(facility.getID());
		order.add(type.getKey());
		order.add(qty);
		return order;
	}

	protected TurnOrder setSalaryOrder(Workers workers, int salary) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.SET_SALARY));
		order.add(workers.getFacility());
		order.add(workers.getPopClassType());
		order.add(salary);
		return order;
	}

	protected TurnOrder buyOrder(MarketItem item, Starship ship, int qty) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.SHIP_BUY_ITEM));
		order.add(ship.getID());
		order.add(item.getColony());
		order.add(item.getItem().getType());
		order.add(qty);
		return order;
	}
	
	protected TurnOrder buyOrder(MarketItem item, int qty) {
		TurnOrder order = new TurnOrder();
		order.setType(OrderType.getType(OrderType.CORP_BUY_ITEM));
		order.add(item.getColony());
		order.add(item.getItem().getType());
		order.add(qty);
		return order;
	}

	@Override
	protected Group getParent() {
		return entityGroup;
	}

	@Override
	public void redraw() {
		entityGroup.pack();
		entityGroup.redraw();
		super.redraw();
	}

	
}
