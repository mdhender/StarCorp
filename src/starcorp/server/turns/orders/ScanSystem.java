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

import starcorp.common.entities.Corporation;
import starcorp.common.entities.StarSystem;
import starcorp.common.entities.Starship;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.OrderType;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.ScanSystem
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class ScanSystem extends AOrderProcessor {
	public static final int TIME_UNITS = 10;
	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		Corporation corp = order.getCorp();
		int starshipId = order.getAsInt(0);
		
		Starship ship = (Starship) entityStore.load(Starship.class, starshipId);
		
		if(ship == null || ship.getOwner() != corp.getID()) {
			error = new TurnError(TurnError.INVALID_SHIP,order);
		}
		else if(!ship.enoughTimeUnits(TIME_UNITS)) {
			error = new TurnError(TurnError.INSUFFICIENT_TIME,order);
		}
		else {
			ship.incrementTimeUnitsUsed(TIME_UNITS);
			entityStore.update(ship);
			StarSystem system = (StarSystem) entityStore.load(StarSystem.class, ship.getSystem());
			OrderReport report = new OrderReport(order,system,ship);
			List<?> entities = entityStore.listSystemEntities(system.getID(),ship.getID());
			report.addScannedEntities(entities);
			report.add(entities.size());
			report.add(ship.getTimeUnitsRemaining());
			order.setReport(report);
		}
		
		return error;
	}

	@Override
	public String getKey() {
		return OrderType.SCAN_SYSTEM;
	}

}
