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

import java.util.Iterator;
import java.util.List;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.ResourceDeposit;
import starcorp.common.entities.StarSystemEntity;
import starcorp.common.entities.Starship;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.OrderType;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.MineAsteroid
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class MineAsteroid extends AOrderProcessor {
	public static final int TIME_UNITS = 50;
	// TODO test	
	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		Corporation corp = order.getCorp();
		int starshipId = order.getAsInt(0);
		int asteroidId = order.getAsInt(1);
		
		Starship ship = (Starship) entityStore.load(Starship.class, starshipId);
		StarSystemEntity asteroid = (StarSystemEntity) entityStore.load(StarSystemEntity.class, asteroidId);
		
		if(ship == null || ship.getOwner() != corp.getID()) {
			error = new TurnError(TurnError.INVALID_SHIP,order);
		}
		else if(!ship.enoughTimeUnits(TIME_UNITS)) {
			error = new TurnError(TurnError.INSUFFICIENT_TIME,order);
		}
		else if(asteroid == null || !asteroid.isAsteroid() || 
				asteroid.getSystem() != ship.getSystem() || 
				!asteroid.getLocation().equals(ship.getLocation()) || 
				ship.getPlanet() != 0) {
			error = new TurnError(TurnError.INVALID_LOCATION,order);
		}
		else if(!ship.getDesign().canMineAsteroid()) {
			error = new TurnError(TurnError.INVALID_SHIP,order);
		}
		else {
			List<?> deposits = entityStore.listDeposits(asteroid.getID());
			Iterator<?> i = deposits.iterator();
			while(i.hasNext()) {
				ResourceDeposit y = (ResourceDeposit) i.next();
				int qty = ship.addCargo(y.getTypeClass(), y.getYield());
				y.setTotalQuantity(y.getTotalQuantity() - qty);
				entityStore.update(y);
			}
			ship.incrementTimeUnitsUsed(TIME_UNITS);
			entityStore.update(ship);
			OrderReport report = new OrderReport(order,asteroid,ship);
			report.addScannedEntities(entityStore.listSystemEntities(ship.getSystem(),ship.getLocation(),ship.getID()));
			report.add(asteroid.getName());
			report.add(asteroid.getID());
			report.add(ship.getTimeUnitsRemaining());
			order.setReport(report);
		}
		
		return error;
	}
	@Override
	public String getKey() {
		return OrderType.MINE_ASTEROID;
	}
	
}
