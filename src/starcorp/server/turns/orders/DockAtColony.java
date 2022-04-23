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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.Colony;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Planet;
import starcorp.common.entities.Starship;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.OrderType;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.orders.DockAtColony
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 17 Sep 2007
 */
public class DockAtColony extends AOrderProcessor {
	public static final int TIME_UNITS = 10;
	
	private static final Log log = LogFactory.getLog(DockAtColony.class);
	
	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		Corporation corp = order.getCorp();
		int starshipId = order.getAsInt(0);
		int colonyId = order.getAsInt(1);
		
		Starship ship = (Starship) entityStore.load(Starship.class, starshipId);
		Colony colony = (Colony) entityStore.load(Colony.class, colonyId);
		
		if(log.isDebugEnabled())
			log.debug(ship + " : " + colony);
		
		if(ship == null || ship.getOwner() != corp.getID()) {
			error = new TurnError(TurnError.INVALID_SHIP,order);
		}
		else if(colony == null) {
			error = new TurnError(TurnError.INVALID_LOCATION,order);
		}
		else if(!ship.enoughTimeUnits(TIME_UNITS)) {
			error = new TurnError(TurnError.INSUFFICIENT_TIME,order);
		}
		else {
			long planet = ship.getPlanet();
			Planet colonyPlanet = ((Planet) entityStore.load(Planet.class, colony.getPlanet()));
			if(log.isDebugEnabled())
				log.debug(planet + " : " + colonyPlanet);
			if(planet == 0) {
				error = new TurnError(TurnError.INVALID_LOCATION,order);
			}
			else if(colonyPlanet.getID() != planet) {
				error = new TurnError(TurnError.INVALID_LOCATION,order);
			}
			else if(colonyPlanet.getGravityRating() > ship.getDesign().getMaxDockGravity()){
				error = new TurnError(TurnError.GRAVITY_TOO_HIGH,order);
			}
			else {
				ship.setPlanetLocation(colony.getLocation());
				ship.setColony(colony.getID());
				ship.incrementTimeUnitsUsed(TIME_UNITS);
				entityStore.update(ship);
				OrderReport report = new OrderReport(order,colony,ship);
				report.add(ship.getName());
				report.add(ship.getID());
				report.add(colony.getName());
				report.add(colony.getID());
				report.add(ship.getTimeUnitsRemaining());
				report.addScannedEntity(colony);
				report.addScannedEntities(entityStore.listFacilities(colony.getID()));
				report.addScannedEntities(entityStore.listShipsDocked(colony.getID(),ship.getID()));
				order.setReport(report);
			}
		}
		
		return error;
	}
	@Override
	public String getKey() {
		return OrderType.DOCK_COLONY;
	}

}
