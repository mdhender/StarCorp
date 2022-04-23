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

import starcorp.common.types.Population;
import starcorp.common.types.PopulationClass;

/**
 * starcorp.common.entities.AColonists
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 18 Sep 2007
 */
public abstract class AColonists extends ABaseEntity {
	private long colony;
	private Population population;
	private double happiness;

	public AColonists() {
		super();
	}
	
	public String getPopClassType() {
		return population == null ? null : population.getPopClassType();
	}
	
	public void addHappiness(double happiness) {
		this.happiness += happiness;
	}
	
	public PopulationClass getPopClass() {
		return population == null ? null : population.getPopClass();
	}
	
	public void setPopClassType(String type) {
		if(population == null) {
			population = new Population();
		}
		population.setPopClassType(type);
	}
	
	public void addPopulation(int qty) {
		if(population != null) {
			population.add(qty);
		}
	}
	
	public void setPopClass(PopulationClass popClass) {
		if(population == null) {
			population = new Population();
		}
		population.setPopClass(popClass);
	}
	
	public int removePopulation(int qty) {
		if(population != null) {
			return population.remove(qty);
		}
		return 0;
	}
	public int getQuantity() {
		return population == null ? 0 : population.getQuantity();
	}
	
	public void setQuantity(int qty) {
		if(population == null) {
			population = new Population();
		}
		population.setQuantity(qty);
	}
	
	public long getColony() {
		return colony;
	}

	public void setColony(long colony) {
		this.colony = colony;
	}

	public double getHappiness() {
		return happiness;
	}

	public void setHappiness(double happiness) {
		this.happiness = happiness;
	}

	@Override
	public void readXML(Element e) {
		super.readXML(e);
		this.colony = Long.parseLong(e.attributeValue("colony","0"));
		this.population = new Population(e.element("population"));
		this.happiness = Double.parseDouble(e.attributeValue("happiness","0.0"));
	}

	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("colony", String.valueOf(colony));
		e.addAttribute("happiness", String.valueOf(happiness));
		population.toXML(e);
		return e;
	}

	@Override
	public String toString() {
		return population.toString(); 
	}

	public String getDisplayName() {
		return getPopClass().getName() +" x " + getQuantity() + " [" + getID() +"]";
	}

}