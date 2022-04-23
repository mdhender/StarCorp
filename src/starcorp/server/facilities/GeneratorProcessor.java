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
package starcorp.server.facilities;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.AColonists;
import starcorp.common.entities.Colony;
import starcorp.common.entities.ColonyItem;
import starcorp.common.entities.Facility;
import starcorp.common.entities.ResourceDeposit;
import starcorp.common.types.AFacilityType;
import starcorp.common.types.ICoordinates;
import starcorp.common.types.Items;
import starcorp.common.types.ResourceGenerator;
import starcorp.server.engine.AServerTask;

/**
 * starcorp.server.facilities.GeneratorProcessor
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 24 Sep 2007
 */
public class GeneratorProcessor extends AServerTask {
	private static final Log log = LogFactory.getLog(GeneratorProcessor.class);
	
	/* (non-Javadoc)
	 * @see starcorp.server.engine.AServerTask#doJob()
	 */
	@Override
	protected void doJob() throws Exception {
		List<Facility> list = entityStore.listFacilitiesPowered(AFacilityType.listTypes(ResourceGenerator.class));
		log.info(this +": " + list.size() + " generators to process");
		for(Facility facility : list) {
			List<AColonists> workers = entityStore.listWorkersByFacility(facility.getID());
			processGenerator(facility, workers);
			Thread.yield();
		}
	}

	/* (non-Javadoc)
	 * @see starcorp.server.engine.AServerTask#getLog()
	 */
	@Override
	protected Log getLog() {
		return log;
	}
	
	private void processGenerator(Facility generator, List<?> workers) {
		if(log.isDebugEnabled())
			log.debug(this + ": " + generator + " started generating.");
		long owner = generator.getOwner();
		Colony colony = (Colony) entityStore.load(Colony.class, generator.getColony());
		ICoordinates location = colony.getLocation();
		List<ResourceDeposit> deposits = entityStore.listDeposits(colony.getPlanet(), location);
		double efficiency = generator.getEfficiency(workers);
		ResourceGenerator type = (ResourceGenerator) generator.getTypeClass();
		
		int total = 0;
		for(ResourceDeposit deposit : deposits){
			if(deposit.getTotalQuantity() < deposit.getYield()) {
				continue;
			}
			if(type.canGenerate(deposit.getTypeClass())) {
				if(log.isDebugEnabled()) {
					log.debug("Yield: " + deposit.getYield() + " Total: " + deposit.getTotalQuantity() + " Efficiency: " + efficiency);
				}
				int qty = (int) (deposit.getYield() * (efficiency/100.0));
				if(qty > deposit.getTotalQuantity()) {
					qty = deposit.getTotalQuantity();
				}
				total += qty;
				ColonyItem item = entityStore.getItem(colony.getID(), owner, deposit.getTypeClass());
				if(item == null) {
					item = new ColonyItem();
					item.setColony(colony.getID());
					item.setItem(new Items(deposit.getTypeClass()));
					item.setOwner(owner);
					entityStore.create(item);
				}
				item.add(qty);
				deposit.remove(qty);
				entityStore.update(item);
				entityStore.update(deposit);
				if(log.isDebugEnabled())
					log.debug(this + ": " + generator + " generated " + qty + " x " + deposit.getType());
			}
			
		}
		
		if(total < 1) {
			generator.setOpen(false);
			entityStore.update(generator);
			log.info("Closed " + generator + " as no suitable resources can be gathered.");
		}
	}

}
