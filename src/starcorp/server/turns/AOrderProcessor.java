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
package starcorp.server.turns;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.util.PackageExplorer;
import starcorp.server.entitystore.IEntityStore;

/**
 * starcorp.server.turns.AOrderProcessor
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public abstract class AOrderProcessor {

	private static Map<String, AOrderProcessor> processors = new HashMap<String, AOrderProcessor>();
	
	static {
		loadProcessors();
	}
	
	private static void loadProcessors() {
		List<Class> classes;
		try {
			classes = PackageExplorer.getClassesForPackage("starcorp.server.turns.orders");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
			return;
		}
		for(int i = 0; i < classes.size(); i++) {
			AOrderProcessor processor;
			try {
				processor = (AOrderProcessor) classes.get(i).newInstance();
				processors.put(processor.getKey(), processor);
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static AOrderProcessor getProcessor(String type) {
		return processors.get(type);
	}

	protected IEntityStore entityStore;
	
	public abstract TurnError process(TurnOrder order);

	public abstract String getKey();

	public IEntityStore getEntityStore() {
		return entityStore;
	}

	public void setEntityStore(IEntityStore entityStore) {
		this.entityStore = entityStore;
	}
}
