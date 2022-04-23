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
package starcorp.server.npc;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.CashTransaction;
import starcorp.common.entities.ColonyItem;
import starcorp.common.entities.Facility;
import starcorp.common.entities.FactoryQueueItem;
import starcorp.common.entities.MarketItem;
import starcorp.common.entities.ResourceDeposit;
import starcorp.common.types.AFactoryItem;
import starcorp.common.types.AItemType;
import starcorp.common.types.BuildingModules;
import starcorp.common.types.ConsumerGoods;
import starcorp.common.types.Factory;
import starcorp.common.types.IndustrialGoods;
import starcorp.common.types.Items;
import starcorp.common.types.Resources;
import starcorp.common.types.StarshipHulls;
import starcorp.server.ServerConfiguration;
import starcorp.server.Util;
import starcorp.server.Util.BuyResult;
import starcorp.server.engine.AServerTask;

/**
 * starcorp.server.npc.NPCProcessor
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 5 Oct 2007
 */
public class NPCProcessor extends AServerTask {

	private static Log log = LogFactory.getLog(NPCProcessor.class);
	
	private boolean matchType(AItemType type, List<ResourceDeposit> deposits) {
		for(ResourceDeposit deposit : deposits) {
			if(deposit.getTypeClass().equals(type))
				return true;
		}
		return false;
	}
	
	private int countMatchingRawMaterials(AFactoryItem type, List<ResourceDeposit> deposits) {
		int count = 0;
		for(Items item : type.getComponent()) {
			if(matchType(item.getTypeClass(),deposits))
				count++;
		}
		return count;
	}
	
	private List<AItemType> listSuitableItems(long colonyId, Class<?> typeClass) {
		List<ResourceDeposit> deposits = entityStore.listDepositsByColony(colonyId);
		List<AItemType> types = AItemType.listTypes(typeClass);
		List<AItemType> list = new ArrayList<AItemType>();
		for(AItemType type : types) {
			AFactoryItem produceable =(AFactoryItem) type;
			if(countMatchingRawMaterials(produceable, deposits) == produceable.getComponent().size())
				list.add(type);
		}
		return list;
	}
	
	private FactoryQueueItem queueItem(AItemType type, Facility factory) {
		Factory factoryType = (Factory) factory.getTypeClass();
		int capacity = factoryType.getMaxCapacity();
		int quantity = capacity / type.getMassUnits();
		FactoryQueueItem queue = new FactoryQueueItem();
		queue.setColony(factory.getColony());
		queue.setFactory(factory.getID());
		queue.setItem(new Items(type,quantity));
		queue.setPosition(entityStore.getNextQueuePosition(factory.getID()));
		queue.setQueuedDate(ServerConfiguration.getCurrentDate());
		queue = (FactoryQueueItem) entityStore.create(queue);
		if(log.isDebugEnabled()) {
			log.debug(this+": Created " + queue);
		}
		return queue;
		
	}
	
	private void queueConsumerGoods(Facility factory) {
		List<AItemType> types = listSuitableItems(factory.getColony(), ConsumerGoods.class);
		for(AItemType type : types) {
			queueItem(type, factory);
		}
		if(types.size() < 1) {
			factory.setOpen(false);
			entityStore.update(factory);
			log.info(this+": Closed " + factory + " as no suitable items can be produced.");
		}
	}
	
	private void queueIndustrialGoods(Facility factory) {
		List<AItemType> types = listSuitableItems(factory.getColony(), IndustrialGoods.class);
		for(AItemType type : types) {
			queueItem(type, factory);
		}
		if(types.size() < 1) {
			factory.setOpen(false);
			entityStore.update(factory);
			log.info(this+": Closed " + factory + " as no suitable items can be produced.");
		}
	}
	
	private void queueStarshipHulls(Facility factory) {
		List<AItemType> types = AItemType.listTypes(StarshipHulls.class);
		for(AItemType type : types) {
			queueItem(type, factory);
		}
	}
	
	private void queueBuildingModules(Facility factory) {
		List<AItemType> types = AItemType.listTypes(BuildingModules.class);
		for(AItemType type : types) {
			queueItem(type, factory);
		}
	}
	
	private void queueFactoryItems(Facility factory) {
		Factory type = (Factory) factory.getTypeClass();
		if(log.isDebugEnabled()) {
			log.debug(this+": Generating production queue for " + factory + " (" + type + ")");
		}
		if(type.isShipard()) {
			queueStarshipHulls(factory);
		}
		else if(type.isConstruction()) {
			queueBuildingModules(factory);
		}
		else if(type.isIndustrial()) {
			queueIndustrialGoods(factory);
		}
		else if(type.isConsumer()) {
			queueConsumerGoods(factory);
		}
	}
	
	private void queueFactoryItems() {
		List<Facility> list = entityStore.listNPCEmptyQueueFactories();
		log.info(this+": Generating production queues for " + list.size() + " factories.");
		for(Facility factory : list) {
			queueFactoryItems(factory);
		}
		log.info(this+": Finished generating production queues for " + list.size() + " factories.");
	}
	
	private void sellItems(Class<?> typeClass, int minQty) {
		List<ColonyItem> items = entityStore.listAllNPCItems(typeClass, minQty);
		log.info(this+": " + items.size() + " of " + typeClass.getSimpleName() + " types to sell (qty >= " + minQty + ")");
		for(ColonyItem item : items) {
			int qty = item.getQuantity() - minQty;
			int costPerItem = (int) entityStore.getAveragePrice(item.getColony(), item.getItem().getTypeClass());
			if(costPerItem < 1) {
				costPerItem = item.getItem().getTypeClass().getNPCPrice();
			}
			MarketItem marketItem = new MarketItem();
			marketItem.setColony(item.getColony());
			marketItem.setCostPerItem(costPerItem);
			marketItem.setIssuedDate(ServerConfiguration.getCurrentDate());
			marketItem.setItem(new Items(item.getItem().getTypeClass(),qty));
			marketItem.setSeller(item.getOwner());
			entityStore.create(marketItem);
			if(log.isDebugEnabled()) {
				log.debug(this + ": Selling " + marketItem);
			}
			item.remove(qty);
			entityStore.update(item);
		}
	}
	
	private void sellItems() {
		sellItems(Resources.class, 100);
		sellItems(ConsumerGoods.class, 0);
		sellItems(IndustrialGoods.class, 100);
		sellItems(BuildingModules.class, 0);
		sellItems(StarshipHulls.class, 0);
	}
	
	private void buyRawMaterials(FactoryQueueItem queueItem) {
		Facility f = (Facility) entityStore.load(Facility.class, queueItem.getFactory());
		long totalCredits = entityStore.getCredits(f.getOwner());
		AFactoryItem type = (AFactoryItem) queueItem.getItem().getTypeClass();
		long colony = f.getColony();
		long spent = 0;
		for(Items item: type.getComponent()) {
			long cashAvailable = totalCredits - spent;
			ColonyItem cItem = entityStore.getItem(colony, f.getOwner(), item.getTypeClass());
			int required = item.getQuantity() * queueItem.getQuantity();
			int quantity = 0;
			if(cItem == null) {
				quantity = required;
			}
			else {
				quantity = (required - item.getQuantity());
			}
			List<MarketItem> market = entityStore.listMarket(colony, item.getTypeClass(), 1);
			BuyResult result = Util.buy(ServerConfiguration.getCurrentDate(), market, quantity, cashAvailable, entityStore);
			spent += result.totalPrice;
			String[] args = {String.valueOf(result.quantityBought),item.getTypeClass().getName(),"",String.valueOf(colony)};
			entityStore.removeCredits(f.getOwner(), result.totalPrice, CashTransaction.getDescription(CashTransaction.ITEM_BOUGHT, args) );
			log.info(this+": " + f + " bought " + result.quantityBought + " x " + item.getTypeClass() + " for " + result.totalPrice);
		}
	}
	
	private void buyRawMaterials() {
		List<Facility> factories = entityStore.listNPCFactories();
		log.info(this+": Buying raw materials for " + factories.size() + " factories");
		for(Facility f : factories) {
			List<FactoryQueueItem> queue = entityStore.listQueue(f.getID());
			log.info(this + ": " + queue.size() + " queue size of " + f);
			for(FactoryQueueItem queueItem : queue) {
				buyRawMaterials(queueItem);
			}
		}
		log.info(this+": Bought raw materials for " + factories.size() + " factories");
	}

	/* (non-Javadoc)
	 * @see starcorp.server.engine.AServerTask#doJob()
	 */
	@Override
	protected void doJob() throws Exception {
		queueFactoryItems();
		sellItems();
		buyRawMaterials();
	}

	/* (non-Javadoc)
	 * @see starcorp.server.engine.AServerTask#getLog()
	 */
	@Override
	protected Log getLog() {
		return log;
	}
	
	@Override
	protected String getName() {
		return "NPC Processor";
	}

}
