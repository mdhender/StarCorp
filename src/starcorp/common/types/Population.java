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

import org.dom4j.Element;

/**
 * starcorp.common.types.Population
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class Population {

	private int quantity;
	private PopulationClass popClass;
	
	public Population() {
		
	}
	
	public Population(PopulationClass popClass) {
		this.popClass = popClass;
	}
	
	public Population(Element e) {
		this.quantity = Integer.parseInt(e.attributeValue("quantity","0"));
		this.popClass = PopulationClass.getType(e.attributeValue("key"));
	}
	
	public Element toXML(Element parent) {
		Element root = parent.addElement("population");
		root.addAttribute("quantity", String.valueOf(quantity));
		root.addAttribute("key", popClass.getKey());
		return root;
	}
	
	public void add(int qty) {
		quantity += qty;
	}
	
	public int remove(int qty) {
		if(qty > quantity) {
			qty = quantity;
		}
		quantity -= qty;
		return qty;
	}
	
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public PopulationClass getPopClass() {
		return popClass;
	}
	public void setPopClass(PopulationClass popClass) {
		this.popClass = popClass;
	}
	
	public String getPopClassType() {
		return popClass == null ? null : popClass.getKey();
	}
	
	public void setPopClassType(String key) {
		popClass = PopulationClass.getType(key);
	}

	@Override
	public String toString() {
		return quantity + " x " + popClass;
	}
}
