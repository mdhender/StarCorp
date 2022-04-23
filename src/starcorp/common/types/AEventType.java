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

/**
 * starcorp.common.types.EventType
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public abstract class AEventType extends ABaseType {
	private static final ResourceBundle bundle = ResourceBundle.getBundle("starcorp.common.types.events");
	
	private static Map<String, AEventType> types = new HashMap<String, AEventType>(); 

	static {
		loadTypes();
	}
	
	private static void loadTypes() {
		Enumeration<String> keys = bundle.getKeys();
		while(keys.hasMoreElements()) {
			String key = keys.nextElement();
			if(key.endsWith(".class")) {
				String className = bundle.getString(key);
				AEventType type;
				try {
					type = (AEventType) Class.forName(className).newInstance();
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
	public static AEventType getType(String key) {
		if(key == null)
			return null;
		return types.get(key);
	}
	
	public static List<AEventType> listTypes(Class<?> typeClass) {
		List<AEventType> types = new ArrayList<AEventType>();
		
		Iterator<Map.Entry<String, AEventType>> i = AEventType.types.entrySet().iterator();
		
		while(i.hasNext()) {
			Map.Entry<String, AEventType> entry = i.next();
			if(typeClass.equals(entry.getValue().getClass())) {
				types.add(entry.getValue());
			}
		}
		
		return types;
	}
	
	/**
	 * @param type
	 * @param resourceName
	 * @return
	 */
	static String getResource(AEventType type, String resourceName) {
		try {
			return bundle.getString(type.getKey() + "." + resourceName);
		}
		catch(MissingResourceException e) {
			return "!" + type.getKey() + "." + resourceName + "!";
		}
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
	
}
