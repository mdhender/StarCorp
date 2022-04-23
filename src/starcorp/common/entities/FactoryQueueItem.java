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

import starcorp.common.types.GalacticDate;

/**
 * starcorp.common.entities.FactoryQueueItem
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 24 Sep 2007
 */
public class FactoryQueueItem extends ACorporateItem {

	private long factory;
	private GalacticDate queuedDate;
	private int position;
	
	public long getFactory() {
		return factory;
	}
	public void setFactory(long factory) {
		this.factory = factory;
	}
	public GalacticDate getQueuedDate() {
		return queuedDate;
	}
	public void setQueuedDate(GalacticDate queuedDate) {
		this.queuedDate = queuedDate;
	}
	@Override
	public void readXML(Element e) {
		super.readXML(e);
		position = Integer.parseInt(e.attributeValue("position","0"));
		factory = Long.parseLong(e.attributeValue("factory","0"));
		queuedDate = new GalacticDate(e.element("queued").element("date"));
	}
	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("position", String.valueOf(position));
		e.addAttribute("factory", String.valueOf(factory));
		queuedDate.toXML(e.addElement("queued"));
		return e;
	}
	@Override
	public String toString() {
		return "Factory ["+ factory + "] Queue [" + position +"] "+ super.toString();
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (factory ^ (factory >>> 32));
		result = prime * result + position;
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
		final FactoryQueueItem other = (FactoryQueueItem) obj;
		if (factory != other.factory)
			return false;
		if (position != other.position)
			return false;
		return true;
	}
	
}
