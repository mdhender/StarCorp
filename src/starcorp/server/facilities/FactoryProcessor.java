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

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.AColonists;
import starcorp.common.entities.ColonyItem;
import starcorp.common.entities.Facility;
import starcorp.common.entities.FactoryQueueItem;
import starcorp.common.types.AFacilityType;
import starcorp.common.types.AFactoryItem;
import starcorp.common.types.Factory;
import starcorp.common.types.Items;
import starcorp.server.engine.AServerTask;

/**
 * starcorp.server.facilities.FactoryProcessor
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 24 Sep 2007
 */
public class FactoryProcessor extends AServerTask {
	// TODO testings
	private static final Log log = LogFactory.getLog(FactoryProcessor.class);
	
	private void processFactory(Facility factory, List<?> workers) {
		if(log.isDebugEnabled())
			log.debug(this + ": " + factory + " beginning production.");
		Factory type = (Factory) factory.getTypeClass();
		int maxCapacity = factory.isPowered() ? type.getCapacity(workers) : 0;
		int capacityUsed = 0;
		List<FactoryQueueItem> queue = entityStore.listQueue(factory.getID());
		if(log.isDebugEnabled())
			log.debug(this +": " + queue.size() + " items queued.");
		Iterator<FactoryQueueItem> i = queue.iterator();
		while(capacityUsed < maxCapacity && i.hasNext()) {
			Items item = build(factory, i.next(), (maxCapacity - capacityUsed));
			if(item != null) {
				capacityUsed += item.getTotalMass(); 
			}
		}
		if(log.isDebugEnabled())
			log.debug(this + ": " + factory + " production done.");
	}

	private Items build(Facility factory, FactoryQueueItem queueItem, int capacityAvailable) {
		Items item = queueItem.getItem();
		if(log.isDebugEnabled()) {
			log.debug("Next queue item for " + factory + " is " + queueItem);
		}
		AFactoryItem type = (AFactoryItem) item.getTypeClass();
		int qty = capacityAvailable / type.getMassUnits();
		if(qty > item.getQuantity()) {
			qty = item.getQuantity();
		}
		int materials = hasMaterials(factory.getColony(), factory.getOwner(), type);
		if(qty > materials) {
			qty = materials;
		}
		if(qty < 1) {
			return null;
		}
		ColonyItem cItem = entityStore.getItem(factory.getColony(), factory.getOwner(), type);
		if(cItem == null) {
			cItem = new ColonyItem();
			cItem.setColony(factory.getColony());
			cItem.setItem(new Items(type));
			cItem.setOwner(factory.getOwner());
			entityStore.create(cItem);
		}
		cItem.add(qty);
		entityStore.update(cItem);
		queueItem.remove(qty);
		if(queueItem.getQuantity() < 1) {
			entityStore.delete(queueItem);
		}
		else {
			entityStore.update(queueItem);
		}
		if(log.isDebugEnabled())
			log.debug(this + ": " + factory +" produced " + qty + " x " + type);
		useMaterials(factory.getColony(),factory.getOwner(), type, qty); 
		return new Items(item.getTypeClass(),qty);
	}
	
	private int hasMaterials(long colony, long owner, AFactoryItem type) {
		int max = 0;
		for(Items item : type.getComponent()) {
			ColonyItem cItem = entityStore.getItem(colony, owner, item.getTypeClass());
			if(cItem == null) {
				return 0;
			}
			int x = cItem.getQuantity() / item.getQuantity();
			if(x < max) {
				max = x;
			}
		}
		return max;
	}
	
	private void useMaterials(long colony, long owner, AFactoryItem type, int quantity) {
		for(Items item : type.getComponent()) {
			ColonyItem cItem = entityStore.getItem(colony, owner, item.getTypeClass());
			if(cItem == null) {
				int qty = item.getQuantity() * quantity;
				cItem.remove(qty);
				entityStore.update(cItem);
			}
		}
	}
	
	
	/* (non-Javadoc)
	 * @see starcorp.server.engine.AServerTask#doJob()
	 */
	@Override
	protected void doJob() throws Exception {
		List<Facility> list = entityStore.listFacilitiesPowered(AFacilityType.listTypes(Factory.class));
		log.info(this +": " + list.size() + " factories to process");
		for(Facility facility : list) {
			List<AColonists> workers = entityStore.listWorkersByFacility(facility.getID());
			processFactory(facility, workers);
		}
		log.info(this+": " + list.size() + " factories processed.");
	}

	/* (non-Javadoc)
	 * @see starcorp.server.engine.AServerTask#getLog()
	 */
	@Override
	protected Log getLog() {
		return log;
	}

}
