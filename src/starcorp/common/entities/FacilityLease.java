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

import org.dom4j.Element;

import starcorp.common.types.AFacilityType;

/**
 * starcorp.common.entities.FacilityLease
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class FacilityLease extends AGovernmentLaw {
	private AFacilityType type;
	private long licensee;
	private int price;

	public String getType() {
		return type == null ? null : type.getKey();
	}
	public void setType(String key) {
		type = AFacilityType.getType(key);
	}
	public AFacilityType getTypeClass() {
		return type;
	}
	public void setTypeClass(AFacilityType type) {
		this.type = type;
	}
	public long getLicensee() {
		return licensee;
	}
	public void setLicensee(long licensee) {
		this.licensee = licensee;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	
	@Override
	public void readXML(Element e) {
		super.readXML(e);
		this.licensee = Long.parseLong(e.attributeValue("licensee","0"));
		this.type = AFacilityType.getType(e.attributeValue("type"));
		this.price = Integer.parseInt(e.attributeValue("price","0"));
	}
	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("licensee", String.valueOf(licensee));
		e.addAttribute("type", type.getKey());
		e.addAttribute("price",String.valueOf(price));
		return e;
	}
	@Override
	public String toString() {
		return type.getKey() + " " + super.toString();
	}
	
	@Override
	public String getDisplayName() {
		return "Lease to build 1 x " + type.getName() + (licensee > 0 ? " (SOLD)" : "");
	}
	
}
