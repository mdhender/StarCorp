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
package starcorp.server.turns.orders;

import java.util.List;

import starcorp.common.entities.CashTransaction;
import starcorp.common.entities.Colony;
import starcorp.common.entities.ColonyItem;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Facility;
import starcorp.common.entities.Planet;
import starcorp.common.entities.Starship;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.AItemType;
import starcorp.common.types.OrbitalDock;
import starcorp.common.types.OrderType;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.orders.PickupItem
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 17 Sep 2007
 */
public class PickupItem extends AOrderProcessor {
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		OrderReport report = null;
		Corporation corp = order.getCorp();
		int starshipId = order.getAsInt(0);
		int colonyId = order.getAsInt(1);
		String itemTypeKey = order.get(2);
		int quantity = order.getAsInt(3);
		
		Starship ship = (Starship) entityStore.load(Starship.class, starshipId);
		Colony colony = (Colony) entityStore.load(Colony.class, colonyId);
		Planet colonyPlanet = null;
		if(colony != null)
			colonyPlanet = ((Planet) entityStore.load(Planet.class, colony.getPlanet()));
		AItemType type = AItemType.getType(itemTypeKey);
		ColonyItem item = null;
		if(colony != null && corp != null) 
			item = entityStore.getItem(colony.getID(), corp.getID(), type);

		Facility orbitalDock = null;
		if(colony != null)
			orbitalDock = entityStore.getFacility(colony.getID(), OrbitalDock.class);
		
		List<?> dockWorkers = orbitalDock == null ? null : entityStore.listWorkersByFacility(orbitalDock.getID());
		
		if(ship == null || ship.getOwner() != corp.getID()) {
			error = new TurnError(TurnError.INVALID_SHIP,order);
		}
		else if(colony == null) {
			error = new TurnError(TurnError.INVALID_COLONY,order);
		}
		else if(ship.getPlanet() == 0 || ship.getPlanet() != colonyPlanet.getID()) {
			error = new TurnError(TurnError.INVALID_LOCATION,order);
		}
		else if(ship.getColony() != 0 && ship.getColony() != colony.getID()) {
			error = new TurnError(TurnError.INVALID_COLONY,order);
		}
		else if(ship.getColony() == 0 && orbitalDock == null) {
			error = new TurnError(TurnError.INVALID_LOCATION,order);
		}
		else if(orbitalDock != null && ship.getColony() == 0 && orbitalDock.getTransactionsRemaining(dockWorkers) < 1) {
			error = new TurnError(TurnError.MARKET_OUT_OF_TRANSACTIONS,order);
		}
		else if(item == null || item.getItem().getQuantity() < 1) {
			error = new TurnError(TurnError.INVALID_ITEM,order);
		}
		else {
			int quantitySpaceFor = ship.getSpaceFor(type);
			if(quantity > quantitySpaceFor) {
				quantity = quantitySpaceFor;
			}
			int quantityAvailable = item.getItem().getQuantity();
			if(quantityAvailable < quantity) {
				quantity = quantityAvailable;
			}
			ship.addCargo(type, quantity);
			entityStore.update(ship);
			item.getItem().remove(quantity);
			entityStore.update(item);

			if(orbitalDock != null && ship.getColony() == 0) {
				Object[] args2 = {orbitalDock.getTypeClass().getName(), colony.getName(), String.valueOf(colony.getID())};
				String desc = CashTransaction.getDescription(CashTransaction.MARKET_FEES, args2);
				entityStore.transferCredits(corp.getID(), orbitalDock.getOwner(),orbitalDock.getServiceCharge(), desc);
				orbitalDock.incTransactionCount();
				entityStore.update(orbitalDock);
			}
			report = new OrderReport(order,colony,ship);
			report.add(quantity);
			report.add(type.getName());
			report.add(colony.getName());
			report.add(colony.getID());
			report.add(ship.getName());
			report.add(ship.getID());
			order.setReport(report);
		}
		return error;
	}
	
	@Override
	public String getKey() {
		return OrderType.SHIP_PICKUP_ITEM;
	}

}
