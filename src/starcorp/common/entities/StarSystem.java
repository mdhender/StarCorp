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

import starcorp.common.types.Coordinates3D;

/**
 * starcorp.common.entities.StarSystem
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class StarSystem extends ANamedEntity {

	private String type;
	private Coordinates3D location;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Coordinates3D getLocation() {
		return location;
	}
	public void setLocation(Coordinates3D location) {
		this.location = location;
	}
	@Override
	public void readXML(Element e) {
		super.readXML(e);
		this.type = e.attributeValue("type");
		this.location = new Coordinates3D(e);
	}
	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("type", type);
		location.toXML(e);
		return e;
	}
	@Override
	public String toString() {
		return super.toString() + " [" + type + "] @ " + location;
	}
}
