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
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

/**
 * starcorp.common.types.ItemType
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public abstract class AItemType extends ABaseType {
	protected static final ResourceBundle bundle = ResourceBundle.getBundle("starcorp.common.types.items");
	
	private static Map<String, AItemType> types = new TreeMap<String, AItemType>(); 

	static {
		loadTypes();
	}
	
	private static void print(List<AItemType> types, String category) {
		System.out.print("\n");
		Iterator<AItemType> i = types.iterator();
		System.out.println("= " + category + " =");
		System.out.println("|| *Key* || *Name* || *MU* || *Category* || *Sub-Category* || *NPC Price* ||");
		while(i.hasNext()) {
			AItemType type = i.next();
			type.print();
		}
		System.out.print("\n");
		
	}
	
	public static void main(String[] args) {
		print(listTypes(Resources.class), "Resources");
		print(listTypes(ConsumerGoods.class), "Consumer Goods");
		print(listTypes(IndustrialGoods.class), "Industrial Goods");
		print(listTypes(BuildingModules.class), "Building Modules");
		print(listTypes(StarshipHulls.class), "Starship Hulls");
	}
	
	private static void loadTypes() {
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.endsWith(".class")) {
				String className = bundle.getString(key);
				AItemType type;
				try {
					type = (AItemType) Class.forName(className).newInstance();
					type.key = key.substring(0, key.indexOf(".class"));
					types.put(type.getKey(), type);
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public String getCategory() {
		return "[" + getClass().getSimpleName() + "]";
	}
	public abstract String getSubCategory();
	
	/**
	 * @param key
	 * @return
	 */
	public static AItemType getType(String key) {
		return types.get(key);
	}
	
	public static List<AItemType> listTypes(Class<?> typeClass) {
		List<AItemType> types = new ArrayList<AItemType>();
		
		Iterator<Map.Entry<String, AItemType>> i = AItemType.types.entrySet().iterator();
		
		while(i.hasNext()) {
			Map.Entry<String, AItemType> entry = i.next();
			if(typeClass.isAssignableFrom(entry.getValue().getClass())) {
				types.add(entry.getValue());
			}
		}
		
		return types;
	}
	
	public static List<AItemType> listTypes(String filter) {
		List<AItemType> types = new ArrayList<AItemType>();
		
		Iterator<Map.Entry<String, AItemType>> i = AItemType.types.entrySet().iterator();
		filter = filter.toLowerCase();
		while(i.hasNext()) {
			Map.Entry<String, AItemType> entry = i.next();
			AItemType type = entry.getValue();
			if(type.getClass().getSimpleName().equalsIgnoreCase(filter)) {
				types.add(entry.getValue());
			}
			else {
				String s = type.getName().toLowerCase();
				if(s.indexOf(filter) != -1) {
					types.add(entry.getValue());
				}
				else {
					s = type.getKey().toLowerCase();
					if(s.indexOf(filter) != -1) {
						types.add(entry.getValue());
					}
				}
			}
			
		}
		
		return types;
	}
	
	public static List<AItemType> listTypes() {
		List<AItemType> types = new ArrayList<AItemType>();
		
		Iterator<Map.Entry<String, AItemType>> i = AItemType.types.entrySet().iterator();
		
		while(i.hasNext()) {
			Map.Entry<String, AItemType> entry = i.next();
			types.add(entry.getValue());
		}
		
		return types;
	}

	/**
	 * @param type
	 * @param resourceName
	 * @return
	 */
	static String getResource(AItemType type, String resourceName) {
		try {
			return bundle.getString(type.getKey() + "." + resourceName);
		}
		catch(MissingResourceException e) {
			return "!" + type.getKey() + "." + resourceName + "!";
		}
	}
	
	static int getResourceAsInt(AItemType type, String resourceName) {
		try {
			return Integer.parseInt(getResource(type, resourceName));
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}
	
	static double getResourceAsDouble(AItemType type, String resourceName) {
		try {
			return Double.parseDouble(getResource(type, resourceName));
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}
	
	static boolean getResourceAsBoolean(AItemType type, String resourceName) {
		return Boolean.parseBoolean(getResource(type, resourceName));
	}
	
	private String key;
	
	/**
	 * @return
	 */
	public String getKey() {
		return this.key;
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return getResource(this, "name");
	}
	
	/**
	 * @return
	 */
	public int getMassUnits() {
		return getResourceAsInt(this,"mass");
	}
	
	public int getNPCPrice() {
		return getResourceAsInt(this, "npc-price");
	}
	
	public void print() {
		System.out.print("|| " + key + " || " + getName() + " || " + getMassUnits() + " || " + getCategory() + " || " + getSubCategory() + " || " + getNPCPrice() + " || ");
		if(this instanceof AFactoryItem) {
			AFactoryItem fac = (AFactoryItem) this;
			Iterator<Items> j = fac.getComponent().iterator();
			while(j.hasNext()) {
				Items item = j.next();
				if(item.getTypeClass() != null)
					System.out.print(" " + item.getQuantity() + " x " + item.getType() + " ||");
			}
		}
		System.out.print("\n");
	}

	@Override
	public String toString() {
		return super.toString() + " [" + getKey() + "]";
	}
	
	public Set<AItemType> usedIn() {
		Set<AItemType> set = new HashSet<AItemType>();
		List<AItemType> list = listTypes(AFactoryItem.class);
		for(AItemType o : list) {
			AFactoryItem type = (AFactoryItem) o;
			if(type.isComponent(this)) {
				set.add(type);
			}
		}
		return set;
	}
}
