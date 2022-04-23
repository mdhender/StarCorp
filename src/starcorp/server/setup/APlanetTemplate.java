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
package starcorp.server.setup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.Planet;
import starcorp.common.entities.ResourceDeposit;
import starcorp.common.entities.StarSystem;
import starcorp.common.types.AItemType;
import starcorp.common.types.Coordinates2D;
import starcorp.common.types.CoordinatesPolar;
import starcorp.common.types.PlanetMapSquare;
import starcorp.common.types.TerrainType;
import starcorp.common.util.PackageExplorer;
import starcorp.server.Util;
import starcorp.server.entitystore.IEntityStore;

/**
 * starcorp.server.setup.APlanetTemplate
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public abstract class APlanetTemplate {
	private static Map<String, APlanetTemplate> templates = new HashMap<String, APlanetTemplate>();
	private static final String[] atmospheres = {
		"green",
		"microbiotic",
		"poison",
		"radioactive",
		"thin",
		"toxic",
		"vacuum"
	};
	
	
	static {
		List<Class> classes;
		try {
			classes = PackageExplorer.getClassesForPackage("starcorp.server.setup.planets");
			for(Class clazz : classes) {
				APlanetTemplate template = (APlanetTemplate) clazz.newInstance();
				templates.put(clazz.getSimpleName(), template);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	public static List<APlanetTemplate> listTemplates() {
		ArrayList<APlanetTemplate> templates = new ArrayList<APlanetTemplate>();
		for(String key : APlanetTemplate.templates.keySet()) {
			templates.add(APlanetTemplate.templates.get(key));
		}
		return templates;
	}
	
	public static APlanetTemplate getTemplate(String name) {
		return templates.get(name);
	}
	
	protected Log log = LogFactory.getLog(APlanetTemplate.class); 
	protected IEntityStore entityStore;
	
	public void setEntityStore(IEntityStore entityStore) {
		this.entityStore = entityStore;
	}
	
	public Planet create(int systemID, int quadrant, int orbit, String name) {
		StarSystem system = (StarSystem) entityStore.load(StarSystem.class,systemID);
		if(system == null) {
			return null;
		}
		CoordinatesPolar location = new CoordinatesPolar(quadrant,orbit);
		return create(system,null,location,name);

	}
	
	public Planet create(int orbitingPlanetID, String name) {
		Planet p = (Planet) entityStore.load(Planet.class, orbitingPlanetID);
		if(p == null) {
			return null;
		}
		return create((StarSystem)entityStore.load(StarSystem.class, p.getSystem()),p,p.getLocation(),name);
	}
	
	public Planet create(StarSystem system, Planet orbiting, CoordinatesPolar location, String name) {
		Planet planet = new Planet();
		planet.setAtmosphereType(getAtmosphereType());
		planet.setGravityRating(getGravityRating());
		planet.setLocation(location);
		planet.setName(name);
		planet.setOrbiting(orbiting);
		planet.setSystem(system.getID());
		planet = (Planet) entityStore.create(planet);
		log.info("Created " + planet);
		int size = getSize();
		generateMap(planet, size, size);
		return planet;
	}
	
	protected int getGravityRating() {
		return Util.rnd.nextInt(4) + 1;
	}

	protected int getSize() {
		return Util.rnd.nextInt(25) + 1;
	}

	protected String getAtmosphereType() {
		Object o = Util.pick(atmospheres, getAtmosphereChances());
		if(o == null) {
			return getDefaultAtmosphere();
		}
		return (String) o;
	}

	protected abstract int[] getAtmosphereChances();
	protected abstract String getDefaultAtmosphere();
	
	protected abstract int getTerrainChance(TerrainType terrain);
	protected abstract String getDefaultTerrain(); 
	
	private final List<TerrainType> allTypes = TerrainType.listTypes();
	
	protected TerrainType pick(List<TerrainType> neighbours) {
		for(TerrainType type : allTypes) {
			int rand = Util.rnd.nextInt(100);
			int chance = getTerrainChance(type);
			int modifier = TerrainType.count(neighbours, type.getKey());
			if(modifier > 0)
				chance *= Util.rnd.nextInt(modifier);
			if(rand <= chance) {
				return type;
			}
		}
		// this is just a fall back!
		return TerrainType.getType(getDefaultTerrain());
	}
	
	private void addNeighbour(Planet planet, List<TerrainType> neighbours, Coordinates2D location) {
		TerrainType terrain = planet.getTerrain(location);
		if(terrain != null)
			neighbours.add(terrain);
	}
	
	private ArrayList<TerrainType> neighbours = new ArrayList<TerrainType>();

	private void calculateNeighbours(Planet planet, Coordinates2D location) {
		neighbours.clear();
		addNeighbour(planet, neighbours, location.north());
		addNeighbour(planet, neighbours, location.northeast());
		addNeighbour(planet, neighbours, location.east());
		addNeighbour(planet, neighbours, location.southeast());
		addNeighbour(planet, neighbours, location.south());
		addNeighbour(planet, neighbours, location.southwest());
		addNeighbour(planet, neighbours, location.west());
		addNeighbour(planet, neighbours, location.northwest());
	}
	
	private void generateResources(Planet planet, Coordinates2D location, TerrainType terrain) {
		Map<AItemType,Integer> chances = terrain.getResourcesChances();
		for(AItemType type : chances.keySet()) {
			int chance = chances.get(type);
			if(chance <= Util.rnd.nextInt(100)) {
				int totalQuantity = Util.rnd.nextInt(1000000) + 1;
				int yield = Util.rnd.nextInt(20) + 1;
				ResourceDeposit deposit = new ResourceDeposit();
				deposit.setLocation(location);
				deposit.setSystemEntity(planet.getID());
				deposit.setTotalQuantity(totalQuantity);
				deposit.setType(type);
				deposit.setYield(yield);
				entityStore.create(deposit);
				if(log.isDebugEnabled())log.debug("Created " + deposit);
			}
		}
	}
	
	private void generateMap(Planet planet, int width, int height) {
		if(planet.getOrbiting() > 0 ) {
			width /= 10;
			width = width > 1 ? width : 1;
			height /= 10;
			height = height > 1 ? height : 1;
		}
		for(int x = 1; x <= width; x++) {
			for(int y = 1; y <= height; y++) {
				Coordinates2D location = new Coordinates2D(x,y);
				calculateNeighbours(planet, location);
				TerrainType terrain = pick(neighbours);
				PlanetMapSquare sq = new PlanetMapSquare(location,terrain);
				planet.add(sq);
				generateResources(planet, location, terrain);
			}
		}
		entityStore.update(planet);
	}
}
