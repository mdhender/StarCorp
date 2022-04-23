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
package starcorp.common.entities;

import java.util.Iterator;
import java.util.List;

/**
 * starcorp.common.entities.ColonyItem
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class ColonyItem extends ACorporateItem {
	public static int count(List<ColonyItem> items) {
		int count = 0;
		for(ColonyItem o : items) {
			count += o.getQuantity();
		}
		return count;
	}
	
	public static int use(List<ColonyItem> items, int quantity) {
		int used = 0;
		Iterator<ColonyItem> i = items.iterator();
		while(i.hasNext() && used < quantity) {
			ColonyItem item = i.next();
			int qty = quantity - used;
			if(qty > item.getQuantity()) {
				qty = item.getQuantity();
			}
			item.remove(qty);
		}
		return used;
	}
	
}
