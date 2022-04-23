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
 * starcorp.common.entities.DevelopmentGrant
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class DevelopmentGrant extends AGovernmentLaw {
	private AFacilityType type;
	private int grant;
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
	public int getGrant() {
		return grant;
	}
	public void setGrant(int grant) {
		this.grant = grant;
	}

	@Override
	public void readXML(Element e) {
		super.readXML(e);
		this.type = AFacilityType.getType(e.attributeValue("type"));
		this.grant = Integer.parseInt(e.attributeValue("grant","0"));
	}
	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("type", type.getKey());
		e.addAttribute("grant",String.valueOf(grant));
		return e;
	}
	@Override
	public String getDisplayName() {
		return "Grant of \u20a1 " + grant + " for building 1 x " + type.getName();
	}

}
