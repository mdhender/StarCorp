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
 * starcorp.common.types.IndustrialGoods
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class IndustrialGoods extends TradeGoods {

	public static List<AItemType> listPower() {
		List<AItemType> types = new ArrayList<AItemType>();
		Iterator<AItemType> i = AItemType.listTypes(IndustrialGoods.class).iterator();
		while(i.hasNext()) {
			IndustrialGoods goods = (IndustrialGoods) i.next();
			if(goods.isPower()) {
				types.add(goods);
			}
		}
		return types;
	}

	public boolean isAlloy() {
		return getResourceAsBoolean(this, "alloy");
	}

	public boolean isCeramic() {
		return getResourceAsBoolean(this, "ceramic");
	}
	
	public boolean isElectronic() {
		return getResourceAsBoolean(this, "electronic");
	}
	
	public boolean isPlastic() {
		return getResourceAsBoolean(this, "plastic");
	}
	
	public boolean isPower() {
		return getResourceAsBoolean(this, "power");
	}
	
	public boolean isEngine() {
		return getResourceAsBoolean(this, "engine");
	}

	@Override
	public String getSubCategory() {
		if(isAlloy())
			return "Alloy";
		if(isCeramic())
			return "Ceramic";
		if(isElectronic())
			return "Electronic";
		if(isPlastic())
			return "Plastic";
		if(isPower())
			return "Power";
		if(isEngine())
			return "Engine";
		return "";
	}

}
