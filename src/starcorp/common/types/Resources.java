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
import java.util.Iterator;
import java.util.List;

/**
 * starcorp.common.types.Resource
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class Resources extends AItemType {
	
	public static List<AItemType> listMetals() {
		List<AItemType> list = AItemType.listTypes(Resources.class);
		Iterator<AItemType> i = list.iterator();
		while(i.hasNext()) {
			Resources r = (Resources) i.next();
			if(!r.isMetal())
				i.remove();
		}
		return list;
	}
	
	public static List<AItemType> listOrganics() {
		List<AItemType> list = AItemType.listTypes(Resources.class);
		Iterator<AItemType> i = list.iterator();
		while(i.hasNext()) {
			Resources r = (Resources) i.next();
			if(!r.isOrganic())
				i.remove();
		}
		return list;
	}
	
	public static List<AItemType> listFuel() {
		List<AItemType> list = AItemType.listTypes(Resources.class);
		Iterator<AItemType> i = list.iterator();
		while(i.hasNext()) {
			Resources r = (Resources) i.next();
			if(!r.isFuel())
				i.remove();
		}
		return list;
	}
	
	public static List<AItemType> listMinerals() {
		List<AItemType> list = AItemType.listTypes(Resources.class);
		Iterator<AItemType> i = list.iterator();
		while(i.hasNext()) {
			Resources r = (Resources) i.next();
			if(!r.isMineral())
				i.remove();
		}
		return list;
	}
	
	public static List<AItemType> listFissile() {
		List<AItemType> list = AItemType.listTypes(Resources.class);
		Iterator<AItemType> i = list.iterator();
		while(i.hasNext()) {
			Resources r = (Resources) i.next();
			if(!r.isFissile())
				i.remove();
		}
		return list;
	}
	
	public static List<AItemType> listGas() {
		List<AItemType> list = AItemType.listTypes(Resources.class);
		Iterator<AItemType> i = list.iterator();
		while(i.hasNext()) {
			Resources r = (Resources) i.next();
			if(!r.isGas())
				i.remove();
		}
		return list;
	}
	
	public static List<AItemType> listLiquid() {
		List<AItemType> list = AItemType.listTypes(Resources.class);
		Iterator<AItemType> i = list.iterator();
		while(i.hasNext()) {
			Resources r = (Resources) i.next();
			if(!r.isLiquid())
				i.remove();
		}
		return list;
	}
	
	public List<AFacilityType> listGenerators() {
		List<AFacilityType> list = new ArrayList<AFacilityType>();
		for(AFacilityType type : AFacilityType.listTypes(ResourceGenerator.class)) {
			ResourceGenerator rg = (ResourceGenerator) type;
			if(rg.canGenerate(this)) {
				list.add(rg);
			}
		}
		return list;
	}
	
	public boolean isOrganic() {
		return getResourceAsBoolean(this, "organic");
	}

	public boolean isMineral() {
		return getResourceAsBoolean(this, "mineral");
	}

	public boolean isMetal() {
		return getResourceAsBoolean(this, "metal");
	}

	public boolean isFissile() {
		return getResourceAsBoolean(this, "fissile");
	}

	public boolean isFuel() {
		return getResourceAsBoolean(this, "fuel");
	}

	public boolean isGas() {
		return getResourceAsBoolean(this, "gas");
	}

	public boolean isLiquid() {
		return getResourceAsBoolean(this, "liquid");
	}

	@Override
	public String getSubCategory() {
		if(isOrganic())
			return "Organic";
		if(isMineral())
			return "Mineral";
		if(isMetal())
			return "Metal";
		if(isFissile())
			return "Fissile";
		if(isFuel())
			return "Fuel";
		if(isGas())
			return "Gas";
		if(isLiquid())
			return "Liquid";
		return "";
	}
}
