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

import starcorp.common.entities.Facility;
import starcorp.common.entities.Unemployed;
import starcorp.common.entities.Workers;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.OrderType;
import starcorp.common.types.PopulationClass;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.orders.SetSalary
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 18 Sep 2007
 */
public class SetSalary extends AOrderProcessor {
	// TODO test
	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		int facilityId = order.getAsInt(0);
		String popClassKey = order.get(1);
		int salary = order.getAsInt(2);
		
		PopulationClass popClass = PopulationClass.getType(popClassKey);
		Facility facility = (Facility) entityStore.load(Facility.class, facilityId);
		
		if(facility == null) {
			error = new TurnError(TurnError.INVALID_FACILITY,order);
		}
		else if(popClass == null) {
			error = new TurnError(TurnError.INVALID_POP_CLASS,order);
		}
		else {
			long colony = facility.getColony();
			Workers workers = entityStore.getWorkers(facility.getID(), popClass);
			if(workers == null) {
				error = new TurnError(TurnError.INVALID_POP_CLASS,order);
			}
			else {
				if(salary < workers.getSalary()) {
					Unemployed unemployed = entityStore.getUnemployed(colony, popClass);
					if(unemployed == null) {
						unemployed = new Unemployed();
						unemployed.setColony(colony);
						unemployed.setHappiness(0.0);
						unemployed.setPopClass(popClass);
						unemployed = (Unemployed) entityStore.create(unemployed);
					}
					long credits = entityStore.getCredits(workers.getID());
					int qty = workers.getQuantity();
					unemployed.addPopulation(qty);
					workers.setQuantity(0);
					entityStore.update(unemployed);
					entityStore.transferCredits(workers.getID(), unemployed.getID(), credits, "");
				}
				
				workers.setSalary(salary);
				entityStore.update(workers);
				OrderReport report = new OrderReport(order,workers,facility);
				report.add(facility.getID());
				report.add(salary);
				report.add(popClass.getName());
				order.setReport(report);
			}
		}
		
		return error;
	}

	@Override
	public String getKey() {
		return OrderType.SET_SALARY;
	}

}
