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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.StarSystemEntity;
import starcorp.common.entities.Planet;
import starcorp.common.entities.ResourceDeposit;
import starcorp.common.entities.StarSystem;
import starcorp.common.types.Coordinates3D;
import starcorp.common.types.CoordinatesPolar;
import starcorp.common.util.PackageExplorer;
import starcorp.server.Util;
import starcorp.server.entitystore.IEntityStore;

/**
 * starcorp.server.setup.ASystemTemplate
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public abstract class ASystemTemplate {
	
	public static final String TYPE_DWARF = "Dwarf";
	public static final String TYPE_YELLOW_DWARF = "Yellow Dwarf";
	public static final String TYPE_RED_DWARF = "Red Dwarf";
	public static final String TYPE_RED_GIANT = "Red Giant";
	public static final String TYPE_BLUE_GIANT = "Blue Giant";
	public static final String TYPE_SUPER_GIANT = "Super Giant";
	public static final String TYPE_WHITE_DWARF = "White Dwarf";
	public static final String TYPE_BROWN_DWARF ="Brown Dwarf";
	public static final String TYPE_NEUTRON_STAR = "Neutron Star";
	public static final String TYPE_PULSAR = "Pulsar";
	public static final String TYPE_DOUBLE_STAR = "Double Star";
	public static final String TYPE_BINARY_STAR = "Binary Star";
	public static final String[] TYPES = {
		TYPE_DWARF, TYPE_YELLOW_DWARF, TYPE_RED_DWARF,
		TYPE_RED_GIANT, TYPE_BLUE_GIANT, TYPE_SUPER_GIANT,
		TYPE_WHITE_DWARF, TYPE_BROWN_DWARF, TYPE_NEUTRON_STAR,
		TYPE_PULSAR, TYPE_DOUBLE_STAR, TYPE_BINARY_STAR
	};

	private static Map<String, ASystemTemplate> templates = new HashMap<String, ASystemTemplate>();
	
	static {
		List<Class> classes;
		try {
			classes = PackageExplorer.getClassesForPackage("starcorp.server.setup.systems");
			for(Class clazz : classes) {
				ASystemTemplate template = (ASystemTemplate) clazz.newInstance();
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
	
	private static final Random rnd = new Random(System.currentTimeMillis());
	
	protected Log log = LogFactory.getLog(ASystemTemplate.class);
	
	protected String randomType() {
		return TYPES[rnd.nextInt(TYPES.length)];
	}
	
	public static ASystemTemplate getTemplate(String name) {
		return templates.get(name);
	}
	
	protected IEntityStore entityStore;
	
	public void setEntityStore(IEntityStore entityStore) {
		this.entityStore = entityStore;
	}
	
	public StarSystem create(int x, int y, int z, String name) {
		Coordinates3D location = new Coordinates3D(x,y,z);
		return create(location,name);

	}
	
	public StarSystem create(Coordinates3D location, String name) {
		StarSystem system = new StarSystem();
		system.setLocation(location);
		system.setName(name);
		system.setType(randomType());
		system = (StarSystem) entityStore.create(system);
		log.info("Created " + system);
		generateFeatures(system);
		entityStore.update(system);
		return system;
	}
	
	protected boolean hasAsteroid(int orbit) {
		int rand = Util.rnd.nextInt(100);
		if(orbit > 3 && orbit < 7) {
			return rand <= 25;
		}
		else if(orbit > 12 && orbit < 15) {
			return rand <= 50;
		}
		return rand <= 10;
	}
	
	protected boolean hasGasField(int orbit) {
		int rand = Util.rnd.nextInt(100);
		if(orbit > 5 && orbit < 10) {
			return rand <= 15;
		}
		return false;
	}
	
	protected APlanetTemplate hasPlanet(int orbit) {
		return null;
	}
	
	protected int countMoons(APlanetTemplate template, Planet planet) {
		return 0;
	}
	
	protected APlanetTemplate getMoonTemplate(APlanetTemplate planetTemplate, Planet planet) {
		return planetTemplate;
	}
	
	private int planetCount = 0;
	
	protected ResourceDeposit createDeposit(StarSystemEntity entity, String itemKey) {
		ResourceDeposit deposit = new ResourceDeposit();
		deposit.setSystemEntity(entity.getID());
		deposit.setTotalQuantity(rnd.nextInt(10000000));
		deposit.setType(itemKey);
		deposit.setYield(rnd.nextInt(100) + 1);
		entityStore.create(deposit);
		if(log.isDebugEnabled())log.debug("Created " + deposit);
		return deposit;
	}
	
	protected void generateResources(StarSystemEntity entity) {
		if(entity.isAsteroid()) {
			if(Util.rnd.nextBoolean()) createDeposit(entity, "aluminium");
			if(Util.rnd.nextBoolean())createDeposit(entity, "carbonite");
			if(Util.rnd.nextBoolean())createDeposit(entity, "copper");
			if(Util.rnd.nextBoolean())createDeposit(entity, "fissile");
			if(Util.rnd.nextBoolean())createDeposit(entity, "iron");
			if(Util.rnd.nextBoolean())createDeposit(entity, "precious");
			if(Util.rnd.nextBoolean())createDeposit(entity, "silicon");
		}
		else if(entity.isGasfield()) {
			if(Util.rnd.nextBoolean())createDeposit(entity, "noblegas");
			if(Util.rnd.nextBoolean())createDeposit(entity, "hydrocarbon");
			if(Util.rnd.nextBoolean())createDeposit(entity, "water");
			if(Util.rnd.nextBoolean())createDeposit(entity, "rare-minerals");
		}
	}
	
	protected void generateFeatures(StarSystem system, CoordinatesPolar location) {
		if(hasAsteroid(location.getOrbit())) {
			StarSystemEntity field = new StarSystemEntity();
			field.setAsteroid(true);
			field.setLocation(location);
			field.setName("Asteroid " + location);
			field.setSystem(system.getID());
			field = (StarSystemEntity) entityStore.create(field);
			log.info("Created " + field);
			generateResources(field);
		}
		else if(hasGasField(location.getOrbit())) {
			StarSystemEntity field = new StarSystemEntity();
			field.setGasfield(true);
			field.setLocation(location);
			field.setName("Gas Field " + location);
			field.setSystem(system.getID());
			field = (StarSystemEntity) entityStore.create(field);
			log.info("Created " + field);
			generateResources(field);
		}
		else {
			APlanetTemplate template = hasPlanet(location.getOrbit());
			if(template != null) {
				template.setEntityStore(entityStore);
				planetCount++;
				Planet planet = template.create(system, null, location, system.getName() + " " + planetCount);
				int moons = countMoons(template,planet);
				for(int i = 1; i <= moons; i++) {
					APlanetTemplate moonTemplate = getMoonTemplate(template,planet);
					moonTemplate.setEntityStore(entityStore);
					moonTemplate.create(system, planet, location, planet.getName() + "." + i);
				}
				return;
			}
		}
	}
	
	protected void generateFeatures(StarSystem system) {
		planetCount = 0;
		for(int quadrant = 1; quadrant <= 4; quadrant++) {
			for(int orbit = 1; orbit < 15; orbit++) {
				generateFeatures(system, new CoordinatesPolar(quadrant,orbit));
			}
		}
	}
	
}
