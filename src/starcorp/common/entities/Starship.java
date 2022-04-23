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
import starcorp.common.types.BuildingModules;
import starcorp.common.types.ConsumerGoods;
import starcorp.common.types.Coordinates2D;
import starcorp.common.types.GalacticDate;
import starcorp.common.types.IndustrialGoods;
import starcorp.common.types.Items;
import starcorp.common.types.Resources;
import starcorp.common.types.StarshipHulls;

/**
 * starcorp.common.entities.Starship
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class Starship extends StarSystemEntity {
	public static final int TIME_UNITS_PER_TURN = 100;
	private long owner;
	private long planet;
	private Coordinates2D planetLocation;
	private long colony;
	private StarshipDesign design;
	private Set<Items> cargo = new HashSet<Items>();
	private GalacticDate builtDate;
	private int timeUnitsUsed;
	
	public boolean inOpenSpace() {
		return !isDocked() && !inOrbit();
	}
	
	public boolean isDocked() {
		return getPlanetLocation() != null || getColony() > 0;
	}
	
	public boolean inOrbit() {
		return getPlanetLocation() == null && getColony() == 0 && getPlanet() != 0;
	}
	
	public boolean isOrbiting(Planet planet) {
		return isOrbiting(planet.getID());
	}
	
	public boolean isOrbiting(long planet) {
		return getPlanetLocation() == null && getColony() == 0 && this.planet == planet;
	}

	public int getCargoConsumerGoodsMass() {
		int mass = 0;
		Iterator<Items> i = cargo.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			if(item.getTypeClass() instanceof ConsumerGoods) {
				mass += (item.getTypeClass().getMassUnits() * item.getQuantity());
			}
		}
		return mass;
	}
	
	public int getCargoIndustrialGoodsMass() {
		int mass = 0;
		Iterator<Items> i = cargo.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			if(item.getTypeClass() instanceof IndustrialGoods) {
				mass += (item.getTypeClass().getMassUnits() * item.getQuantity());
			}
			else if(item.getTypeClass() instanceof Resources) {
				Resources r = (Resources) item.getTypeClass();
				if(r.isFissile() || r.isMetal() || r.isMineral()) {
					mass += (r.getMassUnits() * item.getQuantity()); 
				}
			}
		}
		return mass;
	}
	
	public int getCargoModulesMass() {
		int mass = 0;
		Iterator<Items> i = cargo.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			if(item.getTypeClass() instanceof BuildingModules || item.getTypeClass() instanceof StarshipHulls) {
				mass += (item.getTypeClass().getMassUnits() * item.getQuantity());
			}
		}
		return mass;
		
	}
	
	public int getCargoOrganicsMass() {
		int mass = 0;
		Iterator<Items> i = cargo.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			if(item.getTypeClass() instanceof Resources) {
				Resources r = (Resources) item.getTypeClass();
				if(r.isOrganic()) {
					mass += (r.getMassUnits() * item.getQuantity()); 
				}
			}
		}
		return mass;
		
	}
	
	public int getCargoLiquidGasMass() {
		int mass = 0;
		Iterator<Items> i = cargo.iterator();
		while(i.hasNext()) {
			Items item = i.next();
			if(item.getTypeClass() instanceof Resources) {
				Resources r = (Resources) item.getTypeClass();
				if(r.isLiquid() || r.isGas()) {
					mass += (r.getMassUnits() * item.getQuantity()); 
				}
			}
		}
		return mass;
		
	}
	
	public int getSpaceFor(AItemType type) {
		int mu = getCargoSpace(type);
		return mu / type.getMassUnits();
	}
	
	public int getCargoSpace(AItemType type) {
		int cargoSpace = 0;
		if(type instanceof ConsumerGoods) {
			cargoSpace = design.getConsumerCapacity() - getCargoConsumerGoodsMass();
		}
		else if(type instanceof IndustrialGoods) {
			cargoSpace = design.getIndustrialCapacity() - getCargoIndustrialGoodsMass();
		}
		else if(type instanceof BuildingModules || type instanceof StarshipHulls) {
			cargoSpace = design.getModulesCapacity() - getCargoModulesMass();
		}
		else if(type instanceof Resources) {
			Resources r = (Resources) type;
			if(r.isFissile() || r.isMetal() || r.isMineral()) {
				cargoSpace = design.getIndustrialCapacity() - getCargoIndustrialGoodsMass();
			}
			else if(r.isGas() || r.isLiquid()) {
				cargoSpace = design.getLiquidGasCapacity() - getCargoLiquidGasMass();
			}
			else if(r.isOrganic()) {
				cargoSpace = design.getOrganicsCapacity() - getCargoOrganicsMass();
			}
		}
		return cargoSpace;
	}
	
	public Items getCargo(AItemType type) {
		for(Items item : cargo) {
			if(item.getTypeClass().equals(type)) {
				return item;
			}
		}
		return null;
	}
	
	private Items setCargo(Items item) {
		Iterator<Items> i = cargo.iterator();
		while(i.hasNext()) {
			Items item2 = i.next();
			if(item2.getTypeClass().equals(item.getTypeClass())) {
				i.remove();
			}
			else if(item2.getQuantity() < 1)
				i.remove();
		}
		cargo.add(item);
		return item;
	}
	
	public int addCargo(AItemType type, int quantity) {
		Items item = getCargo(type);
		int qty = quantity;
		int spaceFor = getSpaceFor(type);
		if(spaceFor < qty) {
			qty = spaceFor;
		}
		if(item == null) {
			item = new Items();
			item.setQuantity(qty);
			item.setTypeClass(type);
		}
		else {
			item.add(qty);
		}
		if(item.getQuantity() > 0)
			setCargo(item);
		return qty;
	}
	
	public int removeCargo(AItemType type, int quantity) {
		Items item = getCargo(type);
		if(item == null) {
			return 0;
		}
		int qty = quantity > item.getQuantity() ? item.getQuantity() : quantity;
		item.remove(qty);
		setCargo(item);
		return qty;
	}
	
	public long getOwner() {
		return owner;
	}
	public void setOwner(long owner) {
		this.owner = owner;
	}
	public long getPlanet() {
		return planet;
	}
	public void setPlanet(long planet) {
		this.planet = planet;
	}
	public Coordinates2D getPlanetLocation() {
		return planetLocation;
	}
	public void setPlanetLocation(Coordinates2D planetLocation) {
		this.planetLocation = planetLocation;
	}
	public StarshipDesign getDesign() {
		return design;
	}
	public void setDesign(StarshipDesign design) {
		this.design = design;
	}
	public Set<Items> getCargo() {
		return cargo;
	}
	public void setCargo(Set<Items> cargo) {
		this.cargo = cargo;
	}
	public GalacticDate getBuiltDate() {
		return builtDate;
	}
	public void setBuiltDate(GalacticDate builtDate) {
		this.builtDate = builtDate;
	}
	
	public long getColony() {
		return colony;
	}

	public void setColony(long colony) {
		this.colony = colony;
	}

	public int getTimeUnitsUsed() {
		return timeUnitsUsed;
	}

	public void setTimeUnitsUsed(int timeUnitsUsed) {
		this.timeUnitsUsed = timeUnitsUsed;
	}
	
	public int getTimeUnitsRemaining() {
		return TIME_UNITS_PER_TURN - timeUnitsUsed;
	}
	
	public void setTimeUnitsRemaining(int remaining) {
		this.timeUnitsUsed = (TIME_UNITS_PER_TURN - remaining);
	}
	
	public void incrementTimeUnitsUsed(int used) {
		this.timeUnitsUsed += used;
	}
	
	public boolean enoughTimeUnits(int required) {
		return getTimeUnitsRemaining() >= required;
	}

	@Override
	public void readXML(Element e) {
		super.readXML(e);
		this.timeUnitsUsed = Integer.parseInt(e.attributeValue("TU","0"));
		this.owner = Long.parseLong(e.attributeValue("owner","0"));
		this.planet = Long.parseLong(e.attributeValue("planet","0"));
		Element eLoc = e.element("planet-location");
		if(eLoc != null) {
			this.planetLocation = new Coordinates2D(eLoc);
		}
		this.colony = Long.parseLong(e.attributeValue("colony","0"));
		this.design = new StarshipDesign();
		this.design.readXML(e.element("design").element("entity"));
		this.builtDate = new GalacticDate(e.element("built").element("date"));
		
		Element eCargo = e.element("cargo");
		if(eCargo != null) {
			for(Iterator i = eCargo.elementIterator("item"); i.hasNext();) {
				cargo.add(new Items((Element)i.next()));
			}
		}
	}

	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("owner", String.valueOf(owner));
		e.addAttribute("planet", String.valueOf(planet));
		if(planetLocation != null)
			planetLocation.toXML(e.addElement("planet-location"));
		e.addAttribute("colony", String.valueOf(colony));
		design.toBasicXML(e.addElement("design"));
		builtDate.toXML(e.addElement("built"));
		return e;
	}

	@Override
	public Element toFullXML(Element parent) {
		Element e =super.toFullXML(parent);
		e.addAttribute("TU", String.valueOf(timeUnitsUsed));
		Element eCargo = e.addElement("cargo");
		Iterator<Items> i = cargo.iterator();
		while(i.hasNext()) {
			i.next().toXML(eCargo.addElement("item"));
		}
		return e;
	}

	@Override
	public String toString() {
		return super.toString() + " {" + design.getName() + "}";
	} 

}
