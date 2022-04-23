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
package starcorp.common.types;

import java.util.ArrayList;
import java.util.List;

/**
 * starcorp.common.types.Factory
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class Factory extends AFacilityType {

	public boolean canBuild(AItemType type) {
		if(!(type instanceof AFactoryItem)) {
			return false;
		}
		if(type instanceof ConsumerGoods && isConsumer()) {
			return true;
		}
		if(type instanceof IndustrialGoods && isIndustrial()) {
			return true;
		}
		if(type instanceof BuildingModules && isConstruction()) {
			return true;
		}
		if(type instanceof StarshipHulls && isShipard()) {
			return true;
		}
		return false;
	}
	
	public List<AItemType> canBuild() {
		List<AItemType> types = new ArrayList<AItemType>();
		if(isConsumer()) {
			types.addAll(AItemType.listTypes(ConsumerGoods.class));
		}
		if(isIndustrial()) {
			types.addAll(AItemType.listTypes(IndustrialGoods.class));
		}
		if(isConstruction()) {
			types.addAll(AItemType.listTypes(BuildingModules.class));
		}
		if(isShipard()) {
			types.addAll(AItemType.listTypes(StarshipHulls.class));
		}
		return types;
	}
	
	public boolean isConsumer() {
		return Boolean.parseBoolean(getResource(this, "consumer"));
	}
	
	public boolean isIndustrial() {
		return Boolean.parseBoolean(getResource(this, "industrial"));
	}
	
	public boolean isConstruction() {
		return Boolean.parseBoolean(getResource(this, "construction"));
	}

	public boolean isShipard() {
		return Boolean.parseBoolean(getResource(this, "shipyard"));
	}

	public int getMaxCapacity() {
		return Integer.parseInt(getResource(this, "capacity"));
	}
	
	public int getCapacity(List<?> currentWorkers) {
		double efficiency = getEfficiency(currentWorkers);
		return (int) ((double) getMaxCapacity() * efficiency / 100);
	}

	@Override
	public String getSubCategory() {
		return "Capacity " + getMaxCapacity() + "mu of" +
		(isConsumer() ? " consumer" : "") + 
		(isIndustrial() ? " industrial" : "") + 
		(isConstruction() ? " construction" : "") + 
		(isShipard() ? " shipyard" : "");
	}
}
