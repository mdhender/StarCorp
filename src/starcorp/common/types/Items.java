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
 * starcorp.common.types.Item
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class Items implements Comparable<Items>{

	private AItemType type;
	private int quantity;
	
	public Items() {
		
	}
	
	public Items(AItemType type) {
		this.type = type;
	}
	
	public Items(AItemType type, int qty) {
		this.type = type;
		this.quantity = qty;
	}
	
	public Items(Element e) {
		this.type = AItemType.getType(e.attributeValue("type"));
		this.quantity = Integer.parseInt(e.attributeValue("quantity","0"));
	}
	
	public Element toXML(Element parent) {
		parent.addAttribute("type", type.getKey());
		parent.addAttribute("quantity", String.valueOf(quantity));
		return parent;
	}
	
	
	public int getTotalMass() {
		return quantity * (type == null ? 0 : type.getMassUnits());
	}
	
	/**
	 * @return
	 */
	public String getType() {
		return type == null ? null : type.getKey();
	}
	
	/**
	 * @param key
	 */
	public void setType(String key) {
		type = AItemType.getType(key);
	}
	
	/**
	 * @return
	 */
	public AItemType getTypeClass() {
		return type;
	}
	
	/**
	 * @param type
	 */
	public void setTypeClass(AItemType type) {
		this.type = type;
	}
	
	/**
	 * @return
	 */
	public int getQuantity() {
		return quantity;
	}
	/**
	 * @param quantity
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * @param qty
	 * @return
	 */
	public int add(int qty) {
		quantity += qty;
		return quantity;
	}
	
	/**
	 * @param qty
	 * @return
	 */
	public int remove(int qty) {
		if(qty > quantity) {
			qty = quantity;
		}
		quantity -= qty;
		return qty;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + quantity;
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Items other = (Items) obj;
		if (quantity != other.quantity)
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return quantity + " x " + (type == null ? "Unknown" : type.getName());
	}

	public int compareTo(Items o) {
		return (o == null) ? 0 : o.type.compareTo(this.type);
	}
}
