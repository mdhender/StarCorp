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

import starcorp.common.types.AEventType;

/**
 * starcorp.common.entities.StellarAnomoly
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class StellarAnomoly extends StarSystemEntity {

	private String description;
	private AEventType investigationEvent;
	
	@Override
	public void readXML(Element e) {
		super.readXML(e);
		this.description = e.attributeValue("description");
		this.investigationEvent = AEventType.getType(e.attributeValue("event"));
	}
	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("description", description);
		e.addAttribute("event", investigationEvent.getKey());
		return e;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getEvent() {
		return investigationEvent == null ? null : investigationEvent.getKey();
	}
	public void setEvent(String event) {
		investigationEvent = AEventType.getType(event);
	}
	public AEventType getEventClass() {
		return investigationEvent;
	}
	public void setEventClass(AEventType investigationEvent) {
		this.investigationEvent = investigationEvent;
	}
	
}
