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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.CashTransaction;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.FacilityLease;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.OrderType;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.BuyLease
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class BuyLease extends AOrderProcessor {
	private static final Log log = LogFactory.getLog(BuyLease.class);
	
	/* (non-Javadoc)
	 * @see starcorp.server.turns.AOrderProcessor#process(starcorp.client.turns.TurnOrder)
	 */
	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		OrderReport report = null;
		Corporation corp = order.getCorp();
		int colonyId = order.getAsInt(0);
		int lawId = order.getAsInt(1);

		FacilityLease lease = (FacilityLease) entityStore.load(FacilityLease.class, lawId);
		
		if(lease == null || lease.getColony() != colonyId) {
			error = new TurnError(TurnError.NO_SUCH_LAW,order);
		}
		else {	
			if(corp.getID() < 1) {
				corp = entityStore.getCorporation(corp.getPlayerEmail());
			}
			long credits = entityStore.getCredits(corp.getID());
			if(log.isDebugEnabled())
				log.debug("Credits available for " + corp + " is " + credits);
			if(lease.getPrice() > credits) {
				error = new TurnError(TurnError.INSUFFICIENT_CREDITS,order);
			}
			else {
				lease.setLicensee(corp.getID());
				entityStore.update(lease);
				Colony colony = (Colony) entityStore.load(Colony.class,colonyId);
				Object[] args = {lease.getType(), colony.getName(),String.valueOf(colony.getID())};
				String desc = CashTransaction.getDescription(CashTransaction.LEASE_XFER, args);
				
				entityStore.transferCredits(corp.getID(), colony.getGovernment(), lease.getPrice(), desc);
				report = new OrderReport(order,colony,corp);
				report.add(lease.getType());
				report.add(colony.getName());
				report.add(colony.getID());
				report.add(lease.getPrice());
				order.setReport(report);
			}
		}
		return error;
	}
	@Override
	public String getKey() {
		return OrderType.BUY_LEASE;
	}

}
