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

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * starcorp.common.types.OrderType
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class OrderType extends ABaseType {

	public static final String BUY_LEASE = "buy-lease";
	public static final String BUILD_FACILITY = "build-facility";
	public static final String BUILD_STARSHIP = "build-starship";
	public static final String ISSUE_COLONIST_GRANT = "grant-colonist";
	public static final String ISSUE_DEVELOPMENT_GRANT = "grant-development";
	public static final String ISSUE_LEASE = "lease";
	public static final String FACTORY_BUILD = "build";
	public static final String SET_SALARY = "salary";
	public static final String CORP_BUY_ITEM = "corp-buy";
	public static final String CORP_SELL_ITEM = "sell-item";
	public static final String JETTISON_ITEM = "jettison";
	public static final String SHIP_BUY_ITEM = "ship-buy";
	public static final String SHIP_SELL_ITEM = "ship-sell";
	public static final String SHIP_PICKUP_ITEM = "ship-pickup";
	public static final String SHIP_DELIVER_ITEM = "ship-deliver";
	public static final String FOUND_COLONY = "found";
	public static final String INVESTIGATE = "investigate";
	public static final String LEAVE_ORBIT = "orbit-leave";
	public static final String ORBIT = "orbit";
	public static final String DOCK_COLONY = "dock-colony";
	public static final String DOCK_PLANET = "dock-planet";
	public static final String TAKE_OFF = "take-off";
	public static final String MINE_ASTEROID = "mine-asteroid";
	public static final String MINE_GAS_FIELD = "mine-gasfield";
	public static final String PROBE_PLANET = "probe-planet";
	public static final String PROBE_SYSTEM = "probe-system";
	public static final String SCAN_GALAXY = "scan-galaxy";
	public static final String SCAN_SYSTEM = "scan-system";
	public static final String PROSPECT = "prospect";
	public static final String DESIGN_SHIP = "design-ship";
	public static final String MOVE = "move";
	public static final String JUMP  = "jump";
	
	protected static final ResourceBundle bundle = ResourceBundle.getBundle("starcorp.common.types.orders");
	
	private static Map<String, OrderType> types = new TreeMap<String, OrderType>(); 

	static {
		loadTypes();
	}
	
	public static Set<OrderType> listOrders() {
		Set<OrderType> types = new TreeSet<OrderType>();
		for(String s : OrderType.types.keySet() ) {
			types.add(OrderType.types.get(s));
		}
		return types;
	}
	
	public static void main(String[] args) {
		System.out.println("|| *Key* || *Name* || *Arguments* ||");
		Iterator<String> i = types.keySet().iterator();
		while(i.hasNext()) {
			OrderType type = types.get(i.next());
			System.out.println("|| " + type.getKey() + " || " + type.getName() + " || " + type.getArgumentDescriptions() + " ||");
		}
	}
	
	private static void loadTypes() {
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.endsWith(".name")) {
				String k = key.substring(0, key.indexOf("."));
				OrderType type = new OrderType(k);
				types.put(k, type);
			}
		}
	}
	
	/**
	 * @param key
	 * @return
	 */
	public static OrderType getType(String key) {
		if(key == null)
			return null;
		return types.get(key);
	}
	
	/**
	 * @param type
	 * @param resourceName
	 * @return
	 */
	static String getResource(OrderType type, String resourceName) {
		try {
			return bundle.getString(type.getKey() + "." + resourceName);
		}
		catch(MissingResourceException e) {
			return "!" + type.getKey() + "." + resourceName + "!";
		}
	}
	
	private String key;
	
	public OrderType(String key) {
		this.key = key;
	}
	
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

	public String getDescription(List<String> args) {
		return MessageFormat.format(getResource(this,"desc"), args.toArray());
	}
	
	public String getArgumentDescriptions() {
		return getResource(this,"args");
	}
}
