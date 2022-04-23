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
 * starcorp.common.types.ConsumerGoods
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class ConsumerGoods extends TradeGoods {
	
	public static List<AItemType> listFood(int quality) {
		List<AItemType> types = new ArrayList<AItemType>();
		Iterator<AItemType> i = AItemType.listTypes(ConsumerGoods.class).iterator();
		while(i.hasNext()) {
			ConsumerGoods goods = (ConsumerGoods) i.next();
			if(goods.isFood() && goods.getQuality() == quality) {
				types.add(goods);
			}
		}
		return types;
	}
	
	public static List<AItemType> listDrink(int quality) {
		List<AItemType> types = new ArrayList<AItemType>();
		Iterator<AItemType> i = AItemType.listTypes(ConsumerGoods.class).iterator();
		while(i.hasNext()) {
			ConsumerGoods goods = (ConsumerGoods) i.next();
			if(goods.isDrink() && goods.getQuality() == quality) {
				types.add(goods);
			}
		}
		return types;
	}
	
	public static List<AItemType> listIntoxicant(int quality) {
		List<AItemType> types = new ArrayList<AItemType>();
		Iterator<AItemType> i = AItemType.listTypes(ConsumerGoods.class).iterator();
		while(i.hasNext()) {
			ConsumerGoods goods = (ConsumerGoods) i.next();
			if(goods.isIntoxicant() && goods.getQuality() == quality) {
				types.add(goods);
			}
		}
		return types;
	}
	
	public static List<AItemType> listClothes(int quality) {
		List<AItemType> types = new ArrayList<AItemType>();
		Iterator<AItemType> i = AItemType.listTypes(ConsumerGoods.class).iterator();
		while(i.hasNext()) {
			ConsumerGoods goods = (ConsumerGoods) i.next();
			if(goods.isClothes() && goods.getQuality() == quality) {
				types.add(goods);
			}
		}
		return types;
	}
	
	public int getQuality() {
		return getResourceAsInt(this, "quality");
	}
	
	public boolean isFood() {
		return getResourceAsBoolean(this, "food");
	}

	public boolean isDrink() {
		return getResourceAsBoolean(this, "drink");
	}
	
	public boolean isIntoxicant() {
		return getResourceAsBoolean(this, "intoxicant");
	}
	
	public boolean isClothes() {
		return getResourceAsBoolean(this, "clothes");
	}

	@Override
	public String getSubCategory() {
		String s = String.valueOf(getQuality());
		if(isFood())
			return "Food (" + s + ")";
		if(isDrink())
			return "Drink (" + s + ")";
		if(isIntoxicant())
			return "Intoxicant (" + s + ")";
		if(isClothes())
			return "Clothes (" + s + ")";
		return "Unknown + (" + s + ")";
			
	}

}
