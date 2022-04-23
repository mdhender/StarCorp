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
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import starcorp.common.entities.AColonists;

/**
 * starcorp.common.types.FacilityType
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public abstract class AFacilityType extends ABaseType {
	protected static final ResourceBundle bundle = ResourceBundle.getBundle("starcorp.common.types.facilities");
	
	private static Map<String, AFacilityType> types = new TreeMap<String, AFacilityType>(); 

	static {
		loadTypes();
	}
	
	public static void main(String[] args) {
		print(listTypes(ColonyHub.class),"Colony Hub");
		print(listTypes(OrbitalDock.class),"Orbital Dock");
		print(listTypes(Factory.class),"Factories");
		print(listTypes(ResourceGenerator.class),"Resource Generators");
		print(listTypes(ServiceFacility.class),"Service Facilities");
	}
	
	private static void loadTypes() {
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.endsWith(".class")) {
				String className = bundle.getString(key);
				AFacilityType type;
				try {
					type = (AFacilityType) Class.forName(className).newInstance();
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
	
	/**
	 * @param key
	 * @return
	 */
	public static AFacilityType getType(String key) {
		if(key == null)
			return null;
		return types.get(key);
	}
	
	public static List<AFacilityType> listTypes(Class<?> typeClass) {
		List<AFacilityType> types = new ArrayList<AFacilityType>();
		Iterator<Map.Entry<String, AFacilityType>> i = AFacilityType.types.entrySet().iterator();
		while(i.hasNext()) {
			Map.Entry<String, AFacilityType> entry = i.next();
			if(typeClass.isAssignableFrom(entry.getValue().getClass())) {
				types.add(entry.getValue());
			}
		}
		return types;
	}
	
	public static List<AFacilityType> listTypesExcludeClass(Class<?> typeClass) {
		List<AFacilityType> types = new ArrayList<AFacilityType>();
		Iterator<Map.Entry<String, AFacilityType>> i = AFacilityType.types.entrySet().iterator();
		while(i.hasNext()) {
			Map.Entry<String, AFacilityType> entry = i.next();
			if(!entry.getValue().getClass().equals(typeClass)) {
				types.add(entry.getValue());
			}
		}
		return types;
	}

	public static List<AFacilityType> listTypes() {
		List<AFacilityType> types = new ArrayList<AFacilityType>();
		Iterator<Map.Entry<String, AFacilityType>> i = AFacilityType.types.entrySet().iterator();
		while(i.hasNext()) {
			Map.Entry<String, AFacilityType> entry = i.next();
			types.add(entry.getValue());
		}
		return types;
	}

	/**
	 * @param type
	 * @param resourceName
	 * @return
	 */
	static String getResource(AFacilityType type, String resourceName) {
		try {
			return bundle.getString(type.getKey() + "." + resourceName);
		}
		catch(MissingResourceException e) {
			return "!" + type.getKey() + "." + resourceName + "!";
		}
	}
	
	private static void print(List<AFacilityType> types, String category) {
		System.out.print("\n");
		Iterator<AFacilityType> i = types.iterator();
		System.out.println("= " + category + " =");
		System.out.println("|| *Key* || *Name* || *Power* || *Workers* || *Modules* || *Typical Price* ||");
		while(i.hasNext()) {
			AFacilityType type = i.next();
			type.print();
		}
		System.out.print("\n");
		
	}
	
	private String key;
	
	public void print() {
		StringBuffer s = new StringBuffer();
		s.append("|| ");
		s.append(getKey());
		s.append(" || ");
		s.append(getName());
		s.append(" || ");
		s.append(getPowerRequirement());
		s.append(" || ");
		Iterator<PopulationClass> i = getWorkerRequirement().keySet().iterator();
		while(i.hasNext()) {
			Population pop = getWorkerRequirement(i.next());
			if(pop.getPopClass() != null) {
				s.append(pop.getQuantity());
				s.append(" x ");
				s.append(pop.getPopClass().getName());
				s.append(" ");
			}
		}
		s.append(" || ");
		Iterator<Items> j = getBuildingRequirement().iterator();
		while(j.hasNext()) {
			Items item = j.next();
			if(item.getTypeClass() != null) {
				s.append(item.getQuantity());
				s.append(" x ");
				s.append(item.getType());
				s.append(" ");
			}
		}
		s.append(" ||");
		s.append(getTypicalPrice());
		s.append(" ||");
		s.append(getSubCategory());
		s.append(" ||");
		System.out.println(s.toString());
	}
	
	public String getCategory() {
		return getClass().getSimpleName();
	}
	
	public abstract String getSubCategory();
	
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
	public int getPowerRequirement() {
		return Integer.parseInt(getResource(this, "power"));
	}
	
	public Population getWorkerRequirement(PopulationClass popClass) {
		return getWorkerRequirement().get(popClass);
	}
	
	public Map<PopulationClass, Population> getWorkerRequirement() {
		HashMap<PopulationClass, Population> workers = new HashMap<PopulationClass, Population>();
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.indexOf(getKey() + ".worker.type") != -1) {
				String workerNumber = key.substring(key.lastIndexOf(".") + 1);
				Population colonist = new Population();
				PopulationClass popClass = PopulationClass.getType(bundle.getString(key));
				colonist.setPopClass(popClass);
				try {
					colonist.setQuantity(Integer.parseInt(bundle.getString(getKey() + ".worker.qty." + workerNumber)));
				}
				catch(NumberFormatException e) {
					// ignore
				}
				catch(MissingResourceException e) {
					// ignore
				}
				workers.put(popClass, colonist);
			}
		}
		return workers;
	}
	
	public Set<Items> getBuildingRequirement() {
		HashSet<Items> modules = new HashSet<Items>();
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.indexOf(getKey() + ".module.type") != -1) {
				String moduleNumber = key.substring(key.lastIndexOf(".") + 1);
				Items item = new Items();
				item.setType(bundle.getString(key));
				try {
					item.setQuantity(Integer.parseInt(bundle.getString(getKey() + ".module.qty." + moduleNumber)));
				}
				catch(NumberFormatException e) {
					// ignore
				}
				modules.add(item);
			}
		}
		return modules;
	}
	
	public double getEfficiency(Collection<?> currentWorkers) {
		Map<PopulationClass, Population> requiredWorkers = getWorkerRequirement();
		
		int requiredTotal = 0;
		int currentTotal = 0;
		
		for(PopulationClass popClass : requiredWorkers.keySet()) {
			Population c = requiredWorkers.get(popClass);
			requiredTotal += c.getQuantity();
		}
		
		Iterator<?> n = currentWorkers.iterator();
		
		while(n.hasNext()) {
			AColonists w = (AColonists) n.next();
			int qty1 = w.getQuantity();
			int qty2 = requiredWorkers.get(w.getPopClass()).getQuantity();
			if(qty1 <= qty2) {
				currentTotal += qty1;
			}
			else {
				currentTotal += qty2;
			}
		}
		
		return ((double) requiredTotal / (double) currentTotal) * 100.0; 
	}
	
	public int getTypicalPrice() {
		int price = 0;
		for(Items item : getBuildingRequirement()) {
			price += (item.getQuantity() * item.getTypeClass().getNPCPrice());
		}
		return price;
	}

	@Override
	public String toString() {
		return super.toString() + " [" + getKey() + "]";
	}
}
