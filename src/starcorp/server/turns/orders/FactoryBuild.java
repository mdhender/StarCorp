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
import starcorp.common.entities.Facility;
import starcorp.common.entities.FactoryQueueItem;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.AFactoryItem;
import starcorp.common.types.AItemType;
import starcorp.common.types.Factory;
import starcorp.common.types.Items;
import starcorp.common.types.OrderType;
import starcorp.server.ServerConfiguration;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.FactoryBuild
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class FactoryBuild extends AOrderProcessor {

	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		Corporation corp = order.getCorp();
		int factoryId = order.getAsInt(0);
		String itemTypeKey = order.get(1);
		int quantity = order.getAsInt(2);
		Facility factory = (Facility) entityStore.load(Facility.class, factoryId);
		AItemType type = AItemType.getType(itemTypeKey);
		if(factory == null || !(factory.getTypeClass() instanceof Factory)) {
			error = new TurnError(TurnError.INVALID_FACILITY,order);
		}
		else if(factory.getOwner() != corp.getID()) {
			error = new TurnError(TurnError.INVALID_FACILITY,order);
		}
		else if(type == null || !(type instanceof AFactoryItem)) {
			error = new TurnError(TurnError.INVALID_ITEM,order);
		}
		else {
			Factory facType = (Factory) factory.getTypeClass();
			if(facType.canBuild(type)) {
				int position = entityStore.getNextQueuePosition(factory.getID());
				FactoryQueueItem queueItem = new FactoryQueueItem();
				queueItem.setOwner(corp.getID());
				queueItem.setColony(factory.getColony());
				queueItem.setFactory(factory.getID());
				queueItem.setItem(new Items(type,quantity));
				queueItem.setPosition(position);
				queueItem.setQueuedDate(ServerConfiguration.getCurrentDate());
				entityStore.create(queueItem);
				OrderReport report = new OrderReport(order,queueItem,factory);
				report.add(type.getName());
				report.add(quantity);
				report.add(factory.getID());
				order.setReport(report);
			}
			else {
				error = new TurnError(TurnError.INVALID_ITEM,order);
			}
		}
		return error;
	}
	@Override
	public String getKey() {
		return OrderType.FACTORY_BUILD;
	}

}
