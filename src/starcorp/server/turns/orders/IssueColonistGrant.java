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

import starcorp.common.entities.ColonistGrant;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Corporation;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.OrderType;
import starcorp.common.types.PopulationClass;
import starcorp.server.ServerConfiguration;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.IssueColonistGrant
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class IssueColonistGrant extends AOrderProcessor {
	// TODO test
	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		Corporation corp = order.getCorp(); 
		int colonyId = order.getAsInt(0);
		String popClassType = order.get(1);
		int credit = order.getAsInt(2);
		
		Colony colony = (Colony) entityStore.load(Colony.class, colonyId);
		PopulationClass popClass = PopulationClass.getType(popClassType);
		
		if(colony == null) {
			error = new TurnError(TurnError.INVALID_COLONY,order);
		}
		else if(popClass == null) {
			error = new TurnError(TurnError.INVALID_POP_CLASS,order);
		}
		else if(colony.getGovernment() != corp.getID()){
			error = new TurnError(TurnError.INVALID_COLONY,order);
		}
		else {
			ColonistGrant grant = new ColonistGrant();
			grant.setAvailable(true);
			grant.setColony(colony.getID());
			grant.setCredits(credit);
			grant.setIssuedDate(ServerConfiguration.getCurrentDate());
			grant.setPopClass(popClass);
			grant.setAvailable(true);
			entityStore.create(grant);
			
			OrderReport report = new OrderReport(order,colony, corp);
			report.add(popClass.getName());
			report.add(credit);
			report.add(colony.getName());
			report.add(colony.getID());
			order.setReport(report);
			
		}
		return error;
	}
	@Override
	public String getKey() {
		return OrderType.ISSUE_COLONIST_GRANT;
	}

}
