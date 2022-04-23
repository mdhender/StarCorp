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

import starcorp.common.types.PopulationClass;

/**
 * starcorp.common.entities.ColonistGrant
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class ColonistGrant extends AGovernmentLaw {
	PopulationClass popClass;
	int credits;
	public String getPopClassType() {
		return popClass == null ? null : popClass.getKey();
	}
	
	public void setPopClassType(String key) {
		popClass = PopulationClass.getType(key);
	}
	
	public PopulationClass getPopClass() {
		return popClass;
	}
	public void setPopClass(PopulationClass popClass) {
		this.popClass = popClass;
	}
	public int getCredits() {
		return credits;
	}
	public void setCredits(int credit) {
		this.credits = credit;
	}

	@Override
	public void readXML(Element e) {
		super.readXML(e);
		this.popClass = PopulationClass.getType(e.attributeValue("popClass"));
		this.credits = Integer.parseInt(e.attributeValue("credits","0"));
	}

	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("popClass", popClass.getKey());
		e.addAttribute("credits",String.valueOf(credits));
		return e;
	}

	@Override
	public String toString() {
		return popClass.getKey() + " \u20a1 " + credits + " " + super.toString(); 
	}

	@Override
	public String getDisplayName() {
		return "Grant " + popClass.getName() + " \u20a1 " + credits + " each.";
	}
}
