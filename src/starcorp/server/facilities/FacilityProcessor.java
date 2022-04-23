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

import starcorp.common.entities.ColonyItem;
import starcorp.common.entities.Facility;
import starcorp.common.entities.MarketItem;
import starcorp.common.types.AItemType;
import starcorp.common.types.IndustrialGoods;
import starcorp.common.types.Items;
import starcorp.server.ServerConfiguration;
import starcorp.server.Util;
import starcorp.server.engine.AServerTask;

/**
 * starcorp.server.facilities.FacilityProcessor
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 18 Sep 2007
 */
public class FacilityProcessor extends AServerTask {

	private static final Log log = LogFactory.getLog(FacilityProcessor.class);
	
	@Override
	protected void doJob() throws Exception {
		log.info(this + ": Started facility processor");
		List<Facility> facilities = entityStore.listFacilities();
		log.info(this + ": " + facilities.size() + " facilities to power");
		for(Facility facility : facilities) {
			usePower(facility);
			Thread.yield();
		}
		engine.scheduleAndWait(new GeneratorProcessor());
		engine.scheduleAndWait(new FactoryProcessor());
		log.info("Finished facility processor");
	}

	@Override
	protected Log getLog() {
		return log;
	}

	private void usePower(Facility facility) {
		facility.setPowered(false);
		int required = facility.getTypeClass().getPowerRequirement();
		if(log.isDebugEnabled())
			log.debug(facility + " requires " + required + " power");
		long owner = facility.getOwner();
		long colony = facility.getColony();
		List<AItemType> types = IndustrialGoods.listPower();
		List<ColonyItem> items = entityStore.listItems(owner, colony, types);
		if(ColonyItem.count(items) < required) {
			// attempt to buy from market
			List<MarketItem> market = entityStore.listMarket(colony, types, 1);
			Util.BuyResult result = Util.buy(ServerConfiguration.getCurrentDate(), market, required, entityStore.getCredits(owner),entityStore);
			if(result.quantityBought < required) {
				// not enough - just add this to the colonies stockpile
				for(Items item : result.bought) {
					ColonyItem cItem = entityStore.getItem(colony, owner, item.getTypeClass());
					if(cItem == null) {
						cItem = new ColonyItem();
						cItem.setColony(colony);
						cItem.setItem(new Items(item.getTypeClass()));
						cItem.setOwner(owner);
						entityStore.create(cItem);
					}
					cItem.add(item.getQuantity());
					entityStore.update(cItem);
				}
			}
			else {
				facility.setPowered(true);
			}
		}
		else {
			ColonyItem.use(items, required);
			facility.setPowered(true);
		}
		entityStore.update(facility);
		if(log.isDebugEnabled())
			log.debug(facility + (facility.isPowered() ? " is powered" : " isn't powered"));
	}
	
}
