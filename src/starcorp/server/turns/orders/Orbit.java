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
import starcorp.common.entities.Planet;
import starcorp.common.entities.Starship;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.OrderType;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.Orbit
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class Orbit extends AOrderProcessor {
	public static final int TIME_UNITS = 5;
	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		Corporation corp = order.getCorp();
		int starshipId = order.getAsInt(0);
		int planetId = order.getAsInt(1);
		
		Starship ship = (Starship) entityStore.load(Starship.class, starshipId);
		Planet planet = (Planet) entityStore.load(Planet.class, planetId);
		
		if(ship == null || ship.getOwner() != corp.getID()) {
			error = new TurnError(TurnError.INVALID_SHIP,order);
		}
		else if(planet == null) {
			error = new TurnError(TurnError.INVALID_LOCATION,order);
		}
		else if(!ship.enoughTimeUnits(TIME_UNITS)) {
			error = new TurnError(TurnError.INSUFFICIENT_TIME,order);
		}
		else {
			if(ship.getPlanet() != 0) {
				error = new TurnError(TurnError.INVALID_LOCATION,order);
			}
			else if(planet.getGravityRating() > ship.getDesign().getMaxOrbitGravity()){
				error = new TurnError(TurnError.GRAVITY_TOO_HIGH,order);
			}
			else {
				ship.setPlanet(planet.getID());
				ship.incrementTimeUnitsUsed(TIME_UNITS);
				entityStore.update(ship);
				OrderReport report = new OrderReport(order,planet,ship);
				report.add(ship.getName());
				report.add(ship.getID());
				report.add(planet.getName());
				report.add(planet.getID());
				report.add(ship.getTimeUnitsRemaining());
				report.addScannedEntities(entityStore.listShipsInOrbit(planet.getID(),ship.getID()));
				report.addScannedEntities(entityStore.listColoniesByPlanet(ship.getPlanet()));
				order.setReport(report);
			}
		}
		
		return error;
		
	}

	@Override
	public String getKey() {
		return OrderType.ORBIT;
	}

}
