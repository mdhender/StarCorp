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

import starcorp.common.entities.Corporation;
import starcorp.common.entities.Starship;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.AItemType;
import starcorp.common.types.Items;
import starcorp.common.types.OrderType;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.JettisonItem
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class JettisonItem extends AOrderProcessor {

	public TurnError process(TurnOrder order) {
		TurnError error = null;
		OrderReport report = null;
		Corporation corp = order.getCorp();
		int starshipId = order.getAsInt(0);
		String itemTypeKey = order.get(1);
		int quantity = order.getAsInt(2);
		
		Starship ship = (Starship) entityStore.load(Starship.class, starshipId);
		AItemType type = AItemType.getType(itemTypeKey);
		
		if(ship == null || ship.getOwner() != corp.getID()) {
			error = new TurnError(TurnError.INVALID_SHIP,order);
		}
		else {
			Items cargo = ship.getCargo(type);
			if(cargo == null || cargo.getQuantity() < 1) {
				error = new TurnError(TurnError.INVALID_ITEM,order);
			}
			else {
				if(quantity > cargo.getQuantity()) {
					quantity = cargo.getQuantity();
				}
				
				ship.removeCargo(type, quantity);
				entityStore.update(ship);
				report = new OrderReport(order,null,ship);
				report.add(quantity);
				report.add(type.getName());
				report.add(ship.getName());
				report.add(ship.getID());
				order.setReport(report);
			}
		}
		return error;
	}

	@Override
	public String getKey() {
		return OrderType.JETTISON_ITEM;
	}

}
