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

import starcorp.common.entities.CashTransaction;
import starcorp.common.entities.Colony;
import starcorp.common.entities.ColonyItem;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Facility;
import starcorp.common.entities.MarketItem;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.AItemType;
import starcorp.common.types.ColonyHub;
import starcorp.common.types.Items;
import starcorp.common.types.OrderType;
import starcorp.server.ServerConfiguration;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.CorporationSellItem
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class CorporationSellItem extends AOrderProcessor {
	// TODO test
	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		OrderReport report = null;
		Corporation corp = order.getCorp();
		int colonyId = order.getAsInt(0);
		String itemTypeKey = order.get(1);
		int quantity = order.getAsInt(2);
		int price = order.getAsInt(3);
		
		Colony colony = (Colony) entityStore.load(Colony.class, colonyId);
		AItemType type = AItemType.getType(itemTypeKey);
		
		if(corp.getID() < 1) {
			corp = entityStore.getCorporation(corp.getPlayerEmail());
		}
		
		if(colony == null) {
			error = new TurnError(TurnError.INVALID_COLONY,order);
		}
		else if(type == null) {
			error = new TurnError(TurnError.INVALID_ITEM,order);
		}
		else {
			Facility colonyHub = entityStore.getFacility(colony.getID(), colony.getGovernment(), ColonyHub.class);
			ColonyItem colonyItem = entityStore.getItem(colony.getID(), corp.getID(), type);
			List<?> workers = colonyHub == null ? null : entityStore.listWorkersByFacility(colonyHub.getID());
			if(colonyItem == null) {
				error = new TurnError(TurnError.INVALID_ITEM,order);
			}
			else if(colonyHub == null) {
				error = new TurnError(TurnError.INVALID_COLONY,order);
			}
			else if(colonyHub.getTransactionsRemaining(workers) < 1) {
				error = new TurnError(TurnError.MARKET_OUT_OF_TRANSACTIONS,order);
			}
			else {
				quantity = colonyItem.getItem().remove(quantity);
				entityStore.update(colonyItem);
				
				MarketItem marketItem = new MarketItem();
				marketItem.setColony(colony.getID());
				marketItem.setCostPerItem(price);
				marketItem.setIssuedDate(ServerConfiguration.getCurrentDate());
				marketItem.setSeller(corp.getID());
				marketItem.setItem(new Items(type,quantity));
				
				entityStore.create(marketItem);
				
				Object[] args2 = {colonyHub.getTypeClass().getName(), colony.getName(), String.valueOf(colony.getID())};
				String desc = CashTransaction.getDescription(CashTransaction.MARKET_FEES, args2);
				entityStore.transferCredits(corp.getID(), colonyHub.getOwner(), colonyHub.getServiceCharge(), desc);
				colonyHub.incTransactionCount();
				entityStore.update(colonyHub);
				report = new OrderReport(order,colony,corp);
				report.add(quantity);
				report.add(type.getName());
				report.add(colony.getName());
				report.add(colony.getID());
				report.add(price);
				order.setReport(report);
			}
		}
		return error;
	}
	@Override
	public String getKey() {
		return OrderType.CORP_SELL_ITEM;
	}

}
