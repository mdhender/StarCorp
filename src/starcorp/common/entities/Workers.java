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


/**
 * starcorp.common.entities.Worker
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 18 Sep 2007
 */
public class Workers extends AColonists {

	private long facility;
	private int salary;
	
	@Override
	public void readXML(Element e) {
		super.readXML(e);
		this.facility = Long.parseLong(e.attributeValue("facility","0"));
		this.salary = Integer.parseInt(e.attributeValue("salary","0"));
	}
	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("facility", String.valueOf(facility));
		e.addAttribute("salary", String.valueOf(salary));
		return e;
	}

	public long getFacility() {
		return facility;
	}
	public void setFacility(long facility) {
		this.facility = facility;
	}
	public int getSalary() {
		return salary;
	}
	public void setSalary(int salary) {
		this.salary = salary;
	}
	
	
}
