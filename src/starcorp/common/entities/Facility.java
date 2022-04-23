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
package starcorp.common.entities;

import java.util.Collection;
import java.util.List;
import org.dom4j.Element;

import starcorp.common.types.AFacilityType;
import starcorp.common.types.ColonyHub;
import starcorp.common.types.GalacticDate;
import starcorp.common.types.OrbitalDock;
import starcorp.common.types.ServiceFacility;

/**
 * starcorp.common.entities.Facility
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class Facility extends ABaseEntity {

	private long owner;
	private long colony;
	private AFacilityType type;
	private boolean powered;
	private int serviceCharge;
	private int transactionCount;
	private boolean open;
	
	private GalacticDate builtDate;
	
	public double getEfficiency(Collection<?> currentWorkers) {
		return isPowered() ?  type.getEfficiency(currentWorkers) : 0.0;
	}
	
	public int getTransactionsRemaining(List<?> workers) {
		if(type instanceof ServiceFacility) {
			ServiceFacility facility = (ServiceFacility) type;
			return facility.getTotalColonistsServiceable(workers) - transactionCount;
		}
		else if(type instanceof ColonyHub) {
			ColonyHub hub = (ColonyHub) type;
			return hub.getMaxMarketTransactions(workers) - transactionCount;
		}
		else if(type instanceof OrbitalDock) {
			OrbitalDock dock = (OrbitalDock) type;
			return dock.getMaxMarketTransactions(workers) - transactionCount;
		}
		else {
			return -1;
		}
	}
	
	public long getOwner() {
		return owner;
	}
	public void setOwner(long owner) {
		this.owner = owner;
	}
	public long getColony() {
		return colony;
	}
	public void setColony(long colony) {
		this.colony = colony;
	}
	public String getType() {
		return type == null ? null : type.getKey();
	}
	public void setType(String key) {
		type = AFacilityType.getType(key);
	}
	public AFacilityType getTypeClass() {
		return type;
	}
	public void setTypeClass(AFacilityType type) {
		this.type = type;
	}
	public int getServiceCharge() {
		return serviceCharge;
	}
	public void setServiceCharge(int serviceCharge) {
		this.serviceCharge = serviceCharge;
	}
	public int getTransactionCount() {
		return transactionCount;
	}
	
	public void incTransactionCount() {
		this.transactionCount++;
	}
	
	public void incTransactionCount(int qty) {
		this.transactionCount += qty;
	}
	
	public void setTransactionCount(int transactionCount) {
		this.transactionCount = transactionCount;
	}
	public boolean isOpen() {
		return open;
	}
	public void setOpen(boolean open) {
		this.open = open;
	}
	public GalacticDate getBuiltDate() {
		return builtDate;
	}

	public void setBuiltDate(GalacticDate builtDate) {
		this.builtDate = builtDate;
	}

	public boolean isPowered() {
		return powered;
	}

	public void setPowered(boolean powered) {
		this.powered = powered;
	}

	@Override
	public void readXML(Element e) {
		super.readXML(e);
		this.owner = Long.parseLong(e.attributeValue("owner","0"));
		this.colony = Long.parseLong(e.attributeValue("colony","0"));
		this.type = AFacilityType.getType(e.attributeValue("type"));
		this.powered = Boolean.parseBoolean(e.attributeValue("powered","false"));
		this.serviceCharge =Integer.parseInt(e.attributeValue("charge","0"));
		this.open = Boolean.parseBoolean(e.attributeValue("open","false"));
		this.builtDate = new GalacticDate(e.element("built").element("date"));
	}

	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("owner", String.valueOf(owner));
		e.addAttribute("colony", String.valueOf(colony));
		e.addAttribute("type", type.getKey());
		e.addAttribute("powered", powered ? "true" : "false");
		e.addAttribute("charge", String.valueOf(serviceCharge));
		e.addAttribute("open", open ? "true" : "false");
		builtDate.toXML(e.addElement("built"));
		return e;
	}

	@Override
	public String toString() {
		return type.getKey() + " " + super.toString();
	}

	public String getDisplayName() {
		return getTypeClass().getName() + " [" + getID() +"]";
	}
}
