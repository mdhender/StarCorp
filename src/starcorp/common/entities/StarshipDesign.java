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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.dom4j.Element;

import starcorp.common.types.AItemType;
import starcorp.common.types.GalacticDate;
import starcorp.common.types.Items;
import starcorp.common.types.Resources;
import starcorp.common.types.StarshipHulls;

/**
 * starcorp.common.entities.StarshipDesign
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class StarshipDesign extends ANamedEntity {

	private long owner;
	private GalacticDate designDate;
	private Set<Items> hulls = new HashSet<Items>();
	
	public boolean isValid() {
		int total = countHulls();
		int command = countCommandHulls();
		int crew = countCrewHulls();
		
		if(command < 1)
			return false;
		
		int nonCrewOrCommand = total - (command + crew);
		
		return (nonCrewOrCommand / 3) <= crew;
	}
	
	public int countHulls() {
		int total = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			total += item.getQuantity();
		}
		return total;
	}
	
	public int countHulls(StarshipHulls type) {
		int total = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			if(type.equals(item.getTypeClass())) {
				total += item.getQuantity();
			}
		}
		return total;
	}
	
	public int countCommandHulls() {
		int total = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			if(hull.isCommand()) {
				total += item.getQuantity();
			}
		}
		return total;
	}

	public int countCrewHulls() {
		int total = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			if(hull.isCrew()) {
				total += item.getQuantity();
			}
		}
		return total;
	}

	public int getConsumerCapacity() {
		int total = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			total += item.getQuantity() * hull.getConsumerCapacity();
		}
		return total;
	}

	public int getIndustrialCapacity() {
		int total = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			total += item.getQuantity() * hull.getIndustrialCapacity();
		}
		return total;
	}

	public int getModulesCapacity() {
		int total = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			total += item.getQuantity() * hull.getModulesCapacity();
		}
		return total;
	}

	public int getOrganicsCapacity() {
		int total = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			total += item.getQuantity() * hull.getOrganicsCapacity();
		}
		return total;
	}

	public int getLiquidGasCapacity() {
		int total = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			total += item.getQuantity() * hull.getLiquidGasCapacity();
		}
		return total;
	}
	
	public int getTotalMass() {
		int total = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			total += item.getQuantity() * hull.getMassUnits();
		}
		return total;
	}

	public int getJumpRange() {
		int maxWarp = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			if(hull.getWarpFactor() > maxWarp)
				maxWarp = hull.getWarpFactor();
		}
		return maxWarp;
	}

	public double getImpulseSpeed() {
		int totalThrust = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			totalThrust += item.getQuantity() * hull.getThrustPower();
		}
		int totalMass = getTotalMass();
		return ((double) totalMass / (double) totalThrust);
	}

	public long getMaxOrbitGravity() {
		return Math.round(5 / getImpulseSpeed());
	}
	
	public long getMaxDockGravity() {
		return Math.round(3 / getImpulseSpeed());
	}
	
	public boolean canMineAsteroid() {
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			if(hull.isAsteroidMining())
				return true;
		}
		return false;
	}

	public boolean canMineGasField() {
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			if(hull.isGasFieldMining())
				return true;
		}
		return false;
	}

	public int getScanGalaxyRange() {
		int range = 0;
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			range += hull.getLongScanner();
			
		}
		return range;
	}

	public boolean canScanStarSystem() {
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			if(hull.isShortScanner())
				return true;
		}
		return false;
	}

	public boolean canProbeField() {
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			if(hull.isSystemProbe())
				return true;
		}
		return false;
	}

	public boolean canProbePlanet() {
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			if(hull.isPlanetProbe())
				return true;
		}
		return false;
	}
	
	public boolean canProspect(AItemType type) {
		if(type instanceof Resources) {
			Resources r = (Resources) type;
			if(r.isOrganic()) {
				return hasBioLab();
			}
			if(r.isFissile() || r.isMetal() || r.isMineral()) {
				return hasPhysicsLab();
			}
			if(r.isFuel() || r.isGas() || r.isLiquid()) {
				return hasGeoLab();
			}
		}
		return false;
	}

	public boolean hasBioLab() {
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			if(hull.isBioLab())
				return true;
		}
		return false;
	}

	public boolean hasPhysicsLab() {
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			if(hull.isPhysicsLab())
				return true;
		}
		return false;
	}

	public boolean hasGeoLab() {
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			StarshipHulls hull = (StarshipHulls) item.getTypeClass();
			if(hull.isGeoLab())
				return true;
		}
		return false;
	}

	public long getOwner() {
		return owner;
	}
	public void setOwner(long owner) {
		this.owner = owner;
	}
	
	public void addHulls(String hullType) {
		Items items = getHulls(hullType);
		if(items == null) {
			items = new Items();
			items.setType(hullType);
			hulls.add(items);
		}
		items.add(1);
	}
	
	public Items getHulls(String hullType) {
		StarshipHulls type = (StarshipHulls) AItemType.getType(hullType);
		if(type == null) {
			return null;
		}
		return getHulls(type);
	}
	
	public Items getHulls(StarshipHulls hullType) {
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			if(item.getTypeClass().equals(hullType)) {
				return item;
			}
		}
		return null;
	}
	
	public Items setHulls(Items item) {
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			Items item2 = i.next();
			if(item2.getTypeClass().equals(item.getTypeClass())) {
				item2.add(item.getQuantity());
				item = item2;
				i.remove();
			}
		}
		hulls.add(item);
		return item;
	}
	
	public void clearHulls() {
		hulls.clear();
	}
	
	public void addHulls(StarshipHulls hullType, int quantity) {
		Items item = getHulls(hullType);
		if(item == null) {
			item = new Items();
			item.setQuantity(quantity);
			item.setTypeClass(hullType);
		}
		else {
			item.add(quantity);
		}
		setHulls(item);
	}
	
	public Set<Items> getHulls() {
		return hulls;
	}
	public void setHulls(Set<Items> hulls) {
		this.hulls = hulls;
	}
	public GalacticDate getDesignDate() {
		return designDate;
	}
	public void setDesignDate(GalacticDate designDate) {
		this.designDate = designDate;
	}

	@Override
	public void readXML(Element e) {
		super.readXML(e);
		this.owner = Long.parseLong(e.attributeValue("owner","0"));
		this.designDate = new GalacticDate(e.element("designed").element("date"));
		
		for(Iterator i = e.element("hulls").elementIterator("item"); i.hasNext();) {
			hulls.add(new Items((Element)i.next()));
		}
	}

	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("owner", String.valueOf(owner));
		designDate.toXML(e.addElement("designed"));
		Element eHulls = e.addElement("hulls");
		Iterator<Items> i = hulls.iterator();
		while(i.hasNext()) {
			i.next().toXML(eHulls.addElement("item"));
		}
		return e;
	}

	
}
