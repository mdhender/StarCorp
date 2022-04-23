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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TreeMap;

/**
 * starcorp.common.types.AtmosphereType
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class AtmosphereType extends ABaseType {
	private static final ResourceBundle bundle = ResourceBundle.getBundle("starcorp.common.types.atmospheres");
	
	private static Map<String, AtmosphereType> types = new TreeMap<String, AtmosphereType>(); 

	static {
		loadTypes();
	}
	
	public static void main(String[] args) {
		System.out.println("|| *Key* || *Name* || *Hazard Level* ||");
		Iterator<String> i = types.keySet().iterator();
		while(i.hasNext()) {
			types.get(i.next()).print();
		}
	}
	
	public static List<AtmosphereType> listTypes(double mazHazardLevel) {
		ArrayList<AtmosphereType> list = new ArrayList<AtmosphereType>();
		for(String key : types.keySet()) {
			AtmosphereType type = types.get(key);
			if(type.getHazardLevel() <= mazHazardLevel) {
				list.add(type);
			}
		}
		return list;
	}

	public static List<AtmosphereType> listTypes() {
		ArrayList<AtmosphereType> list = new ArrayList<AtmosphereType>();
		for(String key : types.keySet()) {
			list.add(types.get(key));
		}
		return list;
	}
	
	private static void loadTypes() {
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.endsWith(".name")) {
				String k = key.substring(0, key.indexOf("."));
				AtmosphereType type = new AtmosphereType(k);
				types.put(k, type);
			}
		}
	}
	
	private String key;
	
	public AtmosphereType(String key) {
		this.key = key;
	}
	
	/**
	 * @param key
	 * @return
	 */
	public static AtmosphereType getType(String key) {
		if(key == null)
			return null;
		return types.get(key);
	}
	
	/**
	 * @param type
	 * @param resourceName
	 * @return
	 */
	static String getResource(AtmosphereType type, String resourceName) {
		try {
			return bundle.getString(type.getKey() + "." + resourceName);
		}
		catch(MissingResourceException e) {
			return "!" + type.getKey() + "." + resourceName + "!";
		}
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
	
	public void print() {
		System.out.println("|| " + getKey() + " || " + getName() + " || " + getHazardLevel() + " ||");
	}
	
	
}
