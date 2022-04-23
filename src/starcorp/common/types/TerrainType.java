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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * starcorp.common.types.TerrainType
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class TerrainType extends ABaseType {
	private static final ResourceBundle bundle = ResourceBundle.getBundle("starcorp.common.types.terrain");
	
	private static Map<String, TerrainType> types = new TreeMap<String, TerrainType>(); 

	static {
		loadTypes();
	}
	
	public static List<TerrainType> listTypes() {
		ArrayList<TerrainType> types = new ArrayList<TerrainType>();
		for(String key : TerrainType.types.keySet()) {
			types.add(TerrainType.types.get(key));
		}
		return types;
	}
	
	public static int count(List<TerrainType> list, String key) {
		int count = 0;
		for(TerrainType terrain : list) {
			if(terrain.getKey().equals(key)) {
				count++;
			}
		}
		return count;
	}
	
	public static boolean contains(List<TerrainType> list, String key) {
		for(TerrainType terrain : list) {
			if(terrain.getKey().equals(key)) {
				return true;
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		System.out.println("|| *Key* || *Name* || *Hazard Level* ||");
		Iterator<String> i = types.keySet().iterator();
		while(i.hasNext()) {
			types.get(i.next()).print();
		}
	}
	
	private static void loadTypes() {
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.endsWith(".name")) {
				String k = key.substring(0, key.indexOf("."));
				TerrainType type = new TerrainType(k);
				types.put(k, type);
			}
		}
	}
	
	/**
	 * @param key
	 * @return
	 */
	public static TerrainType getType(String key) {
		return types.get(key);
	}
	
	/**
	 * @param type
	 * @param resourceName
	 * @return
	 */
	static String getResource(TerrainType type, String resourceName) {
		try {
			return bundle.getString(type.getKey() + "." + resourceName);
		}
		catch(MissingResourceException e) {
			return "!" + type.getKey() + "." + resourceName + "!";
		}
	}
	
	private String key;
	
	public TerrainType(String key) {
		this.key = key;
	}
	
	/**
	 * @return
	 */
	public String getKey() {
		return key;
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
	public double getHazardLevel() {
		return Double.parseDouble(getResource(this,"hazard"));
	}
	
	public String getImageFilename() {
		return getResource(this,"image");
	}
	
	public Map<AItemType, Integer> getResourcesChances() {
		HashMap<AItemType, Integer> resources = new HashMap<AItemType, Integer>();
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.indexOf(getKey() + ".resource.type") != -1) {
				String resourceNumber = key.substring(key.lastIndexOf(".") + 1);
				AItemType type = AItemType.getType(bundle.getString(key));
				int chance = 0;
				try {
					chance = Integer.parseInt(bundle.getString(getKey() + ".resource.chance." + resourceNumber));
				}
				catch(NumberFormatException e) {
					// ignore
				}
				catch(MissingResourceException e) {
					// ignore
				}
				resources.put(type, chance);
			}
		}
		return resources;
	}

	public void print() {
		StringBuffer sb = new StringBuffer("|| " + getKey() + " || " + getName() + " || " + getHazardLevel() + " ||");
		Map<AItemType, Integer> resources = getResourcesChances();
		for(AItemType type : resources.keySet()) {
			sb.append(" ");
			sb.append(type.getKey());
			sb.append(" ");
			sb.append(resources.get(type));
			sb.append("% ||");
		}
		System.out.println(sb);
	}
	
}
