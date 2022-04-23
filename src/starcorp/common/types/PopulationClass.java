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
 * starcorp.common.types.PopulationClass
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class PopulationClass extends ABaseType {
	private static final ResourceBundle bundle = ResourceBundle.getBundle("starcorp.common.types.population");
	
	private static Map<String, PopulationClass> types = new TreeMap<String, PopulationClass>(); 

	static {
		loadTypes();
	}
	
	public static void main(String[] args) {
		System.out.println("|| *Key* || *Name* || *Service Quality* || *Consumer Quality* || *NPC Salary* ||");
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
				PopulationClass type = new PopulationClass(k);
				types.put(k, type);
			}
		}
	}
	
	public static List<PopulationClass> listTypes() {
		ArrayList<PopulationClass> types = new ArrayList<PopulationClass>();
		for(String key : PopulationClass.types.keySet()) {
			types.add(PopulationClass.types.get(key));
		}
		return types;
	}
	
	/**
	 * @param key
	 * @return
	 */
	public static PopulationClass getType(String key) {
		return types.get(key);
	}
	
	/**
	 * @param type
	 * @param resourceName
	 * @return
	 */
	static String getResource(PopulationClass type, String resourceName) {
		try {
			return bundle.getString(type.getKey() + "." + resourceName);
		}
		catch(MissingResourceException e) {
			return "!" + type.getKey() + "." + resourceName + "!";
		}
	}
	
	public static int countTypes() {
		return types.size();
	}
	
	public static Iterator<String> typeKeys() {
		return types.keySet().iterator();
	}
	
	private String key;
	
	public PopulationClass(String key) {
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
	
	public int getServiceQualityRequired() {
		try {
			return Integer.parseInt(getResource(this, "quality.service"));
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}

	public int getConsumerQualityRequired() {
		try {
			return Integer.parseInt(getResource(this, "quality.consumer"));
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}
	
	public int getNPCSalary() {
		try {
			return Integer.parseInt(getResource(this, "salary"));
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}
	
	public void print() {
		String s = "|| " + getKey() + " || [" + getName() + "] || " + getServiceQualityRequired() + " || " + getConsumerQualityRequired() + " || " + getNPCSalary() + " ||";
		System.out.println(s);
	}

	@Override
	public String toString() {
		return super.toString() + " [" + getKey() + "]";
	}
}
