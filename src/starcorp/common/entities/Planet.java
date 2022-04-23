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

import starcorp.common.types.AtmosphereType;
import starcorp.common.types.Coordinates2D;
import starcorp.common.types.PlanetMapSquare;
import starcorp.common.types.TerrainType;

/**
 * starcorp.common.entities.Planet
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class Planet extends StarSystemEntity {
	private long orbiting;
	private AtmosphereType atmosphereType;
	private int gravityRating;
	private Set<PlanetMapSquare> map = new HashSet<PlanetMapSquare>();
	
	public void add(PlanetMapSquare sq) {
		map.add(sq);
	}
	
	public TerrainType getTerrain(Coordinates2D location) {
		PlanetMapSquare sq = get(location);
		if(sq == null) {
			return null;
		}
		return sq.getTerrainType();
	}
	public PlanetMapSquare get(Coordinates2D location) {
		if(location == null)
			return null;
		if(location.getX() < 0 || location.getY() < 0)
			return null;
		Iterator<PlanetMapSquare> i = map.iterator();
		while(i.hasNext()) {
			PlanetMapSquare sq = i.next();
			if(sq.getLocation().equals(location)) {
				return sq;
			}
		}
		return null;
	}
	
	public Planet set(PlanetMapSquare planetMapSquare) {
		Iterator<PlanetMapSquare> i = map.iterator();
		while(i.hasNext()) {
			PlanetMapSquare sq = i.next();
			if(sq.getLocation().equals(planetMapSquare.getLocation())) {
				i.remove();
			}
		}
		map.add(planetMapSquare);
		return this;
	}
	
	public long getOrbiting() {
		return orbiting;
	}
	public void setOrbiting(long orbiting) {
		this.orbiting = orbiting;
	}
	public void setOrbiting(Planet orbiting) {
		this.orbiting = orbiting == null ? 0 : orbiting.getID();
	}
	
	public String getAtmosphereType() {
		return atmosphereType == null ? null : atmosphereType.getKey();
	}
	
	public void setAtmosphereType(String key) {
		atmosphereType = AtmosphereType.getType(key);
	}
	
	public AtmosphereType getAtmosphereTypeClass() {
		return atmosphereType;
	}
	public void setAtmosphereTypeClass(AtmosphereType atmosphereType) {
		this.atmosphereType = atmosphereType;
	}
	public int getGravityRating() {
		return gravityRating;
	}
	public void setGravityRating(int gravityRating) {
		this.gravityRating = gravityRating;
	}
	public Set<PlanetMapSquare> getMap() {
		return map;
	}
	public void setMap(Set<PlanetMapSquare> map) {
		this.map = map;
	}

	@Override
	public void readXML(Element e) {
		super.readXML(e);
		try {
			this.orbiting = Long.parseLong(e.attributeValue("orbiting"));
		}
		catch(NumberFormatException ex) { }
		this.atmosphereType = AtmosphereType.getType(e.attributeValue("atmosphere"));
		this.gravityRating = Integer.parseInt(e.attributeValue("gravity","0"));
		Element eMap = e.element("map");
		if(eMap != null) {
			for(Iterator i = eMap.elementIterator("map-square"); i.hasNext(); ) {
				map.add(new PlanetMapSquare((Element)i.next()));
			}
		}
	}

	@Override
	public Element toBasicXML(Element parent) {
		Element e = super.toBasicXML(parent);
		e.addAttribute("orbiting", String.valueOf(orbiting));
		e.addAttribute("atmosphere", atmosphereType.getKey());
		e.addAttribute("gravity", String.valueOf(gravityRating));
		return e;
	}

	@Override
	public Element toFullXML(Element parent) {
		Element e = super.toFullXML(parent);
		Element eMap = e.addElement("map");
		Iterator<PlanetMapSquare> i = map.iterator();
		while(i.hasNext()) {
			i.next().toXML(eMap);
		}
		return e;
	}

	@Override
	public String toString() {
		return super.toString() + 
		" orbiting " + getOrbiting() + 
		" : " + (atmosphereType == null ? "" : atmosphereType.getKey()) + 
		" : " + gravityRating + "g";
	}
	
}
