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
import starcorp.common.entities.StarshipDesign;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.OrderType;
import starcorp.server.ServerConfiguration;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.DesignShip
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class DesignShip extends AOrderProcessor {
	// TODO test
	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		int max = order.size();
		String name = order.get(0);
		Corporation corp = order.getCorp();
		StarshipDesign design = new StarshipDesign();
		design.setDesignDate(ServerConfiguration.getCurrentDate());
		design.setName(name);
		design.setOwner(corp.getID());
		for(int i = 1; i < max; i++) {
			design.addHulls(order.get(i));
		}
		
		if(design.isValid()) {
			entityStore.create(design);
			
			OrderReport report = new OrderReport(order,design,corp);
			report.add(name);
			report.add(design.getID());
			order.setReport(report);
		}
		else {
			error = new TurnError(TurnError.INVALID_SHIP_DESIGN,order);
		}
		return error;
	}
	@Override
	public String getKey() {
		return OrderType.DESIGN_SHIP;
	}

}
