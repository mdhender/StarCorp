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
import starcorp.common.types.Coordinates2D;
import starcorp.common.types.OrderType;
import starcorp.common.types.PlanetMapSquare;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.orders.DockAtPlanet
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 17 Sep 2007
 */
public class DockAtPlanet extends AOrderProcessor {
	public static final int TIME_UNITS = 10;
	
	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		Corporation corp = order.getCorp();
		int starshipId = order.getAsInt(0);
		int x = order.getAsInt(1);
		int y = order.getAsInt(2);
		
		Starship ship = (Starship) entityStore.load(Starship.class, starshipId);
		
		if(ship == null || ship.getOwner() != corp.getID()) {
			error = new TurnError(TurnError.INVALID_SHIP,order);
		}
		else if(!ship.enoughTimeUnits(TIME_UNITS)) {
			error = new TurnError(TurnError.INSUFFICIENT_TIME,order);
		}
		else {
			Planet planet = (Planet) entityStore.load(Planet.class, ship.getPlanet());
			if(planet == null) {
				error = new TurnError(TurnError.INVALID_LOCATION,order);
			}
			else if(planet.getGravityRating() > ship.getDesign().getMaxDockGravity()){
				error = new TurnError(TurnError.GRAVITY_TOO_HIGH,order);
			}
			else {
				Coordinates2D location = new Coordinates2D();
				location.setX(x);
				location.setY(y);
				PlanetMapSquare sq = planet.get(location);
				if(sq == null) {
					error = new TurnError(TurnError.INVALID_LOCATION,order);
				}
				else {
					ship.setPlanetLocation(location);
					ship.incrementTimeUnitsUsed(TIME_UNITS);
					entityStore.update(ship);
					OrderReport report = new OrderReport(order,planet,ship);
					report.add(ship.getName());
					report.add(ship.getID());
					report.add(planet.getName());
					report.add(planet.getID());
					report.add(x);
					report.add(y);
					report.add(sq.getTerrainType().getName());
					report.add(ship.getTimeUnitsRemaining());
					report.addScannedEntity(entityStore.getColony(planet.getID(), location));
					report.addScannedEntities(entityStore.listShipsDocked(planet.getID(), location,ship.getID()));
					order.setReport(report);
				}
			}
		}
		
		return error;
		
	}
	@Override
	public String getKey() {
		return OrderType.DOCK_PLANET;
	}

}
