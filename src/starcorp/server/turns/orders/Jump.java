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
import starcorp.common.entities.StarSystem;
import starcorp.common.entities.Starship;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.Coordinates3D;
import starcorp.common.types.OrderType;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.Jump
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class Jump extends AOrderProcessor {
	// TODO test
	public static final int TIME_UNITS = 20;
	
	/* (non-Javadoc)
	 * @see starcorp.server.turns.AOrderProcessor#process(starcorp.client.turns.TurnOrder)
	 */
	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		Corporation corp = order.getCorp();
		int starshipId = order.getAsInt(0);
		int systemId = order.getAsInt(1);
		
		Starship ship = (Starship) entityStore.load(Starship.class, starshipId);
		StarSystem system = (StarSystem) entityStore.load(StarSystem.class, systemId);
		
		if(ship == null || ship.getOwner() != corp.getID()) {
			error = new TurnError(TurnError.INVALID_SHIP,order);
		}
		else if(system == null) {
			error = new TurnError(TurnError.INVALID_LOCATION,order);
		}
		else if(!ship.enoughTimeUnits(TIME_UNITS)) {
			error = new TurnError(TurnError.INSUFFICIENT_TIME,order);
		}
		else {
			if(ship.getPlanet() != 0) {
				error = new TurnError(TurnError.INVALID_LOCATION,order);
			}
			else {
				StarSystem currentSystem = (StarSystem) entityStore.load(StarSystem.class, ship.getSystem());
				Coordinates3D currentLocation = currentSystem.getLocation();
				Coordinates3D targetLocation = system.getLocation();
				int distance = targetLocation.getDistance(currentLocation);
				if(distance > ship.getDesign().getJumpRange()) {
					error = new TurnError(TurnError.OUT_OF_RANGE,order);
				}
				else {
					ship.setSystem(system.getID());
					ship.incrementTimeUnitsUsed(TIME_UNITS);
					entityStore.update(ship);
					corp.add(system.getID());
					entityStore.update(corp);
					OrderReport report = new OrderReport(order,system,ship);
					report.add(ship.getName());
					report.add(ship.getID());
					report.add(system.getName());
					report.add(system.getID());
					report.add(ship.getTimeUnitsRemaining());
					report.addScannedEntities(entityStore.listSystemEntities(system.getID(), ship.getLocation()));
					order.setReport(report);
				}
			}
		}
		
		return error;
	}
	@Override
	public String getKey() {
		return OrderType.JUMP;
	}

}
