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
package starcorp.server.population;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.AColonists;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Planet;
import starcorp.common.entities.StarSystem;
import starcorp.common.entities.Unemployed;
import starcorp.common.types.CoordinatesPolar;
import starcorp.common.types.PopulationClass;
import starcorp.server.engine.AServerTask;

/**
 * starcorp.server.population.UnemployedMigration
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 22 Sep 2007
 */
public class UnemployedMigration extends AServerTask {
	private static final Log log = LogFactory.getLog(UnemployedMigration.class);
	

	public static final double MIGRATION_DISTANCE_OTHER_SYSTEM = 0.2;
	public static final double MIGRATION_DISTANCE_SAME_LOCATION = 1.0;
	public static final double MIGRATION_DISTANCE_SAME_PLANET = 2.0;
	public static final double MIGRATION_DISTANCE_SAME_SYSTEM = 0.5;

	private Map<Colony, Map<PopulationClass,Unemployed>> all_unemployed = new HashMap<Colony, Map<PopulationClass,Unemployed>>();
	
	private void doMigration(Unemployed unemployed) {
		if (unemployed.getQuantity() < 1)
			return;
		Colony colony = (Colony) entityStore.load(Colony.class, unemployed.getColony());
		Planet planet = (Planet) entityStore.load(Planet.class, colony.getPlanet());
		StarSystem system = planet == null ? null : (StarSystem) entityStore.load(StarSystem.class, planet.getSystem());
		CoordinatesPolar location = planet.getLocation();
		
		List<Colony> samePlanet = entityStore.searchColonies(planet.getID());
		List<Colony> sameLocation =  entityStore.searchColonies(system.getID(), location, planet.getID());
		List<Colony> sameSystem =  entityStore.searchColonies(system.getID(), location);
		List<Colony> others =  entityStore.searchColonies(system.getID());
		doMigration(unemployed, samePlanet, MIGRATION_DISTANCE_SAME_PLANET);
		doMigration(unemployed, sameLocation, MIGRATION_DISTANCE_SAME_LOCATION);
		doMigration(unemployed, sameSystem, MIGRATION_DISTANCE_SAME_SYSTEM);
		doMigration(unemployed, others, MIGRATION_DISTANCE_OTHER_SYSTEM);
		
	}

	private void doMigration(Unemployed unemployed, Colony colony,
			double distanceModifier) {
		if (unemployed.getQuantity() < 1)
			return;
		double happiness = entityStore.getAverageHappiness(colony.getID(), unemployed.getPopClass());
//		if(log.isDebugEnabled()) {
//			log.debug(this + ": " + colony + " has avg happiness " + happiness +". Unemployed happiness " + unemployed.getHappiness());
//		}
		double migrationRate = happiness - unemployed.getHappiness();
		migrationRate *= distanceModifier;
		if(migrationRate > 100.0) {
			migrationRate = 100.0;
		}

		if(log.isDebugEnabled()) {
			log.debug(this + ": Migration Rate = " + migrationRate + " to " + colony +" for "+ unemployed);
		}
		int migrate = (int) (unemployed.getQuantity() * (migrationRate / 100.0));

		if (migrate > 0) {
			long creditsPerPerson = entityStore.getCredits(unemployed.getID()) / unemployed.getQuantity();
			long credits = migrate * creditsPerPerson;
			Unemployed other = getUnemployed(colony, unemployed.getPopClass());
			other.addPopulation(migrate);
			unemployed.removePopulation(migrate);
			entityStore.removeCredits(unemployed.getID(), credits, "");
			if (log.isDebugEnabled())
				log.debug(migrate + " of " + unemployed.getPopClass()
						+ " migrated to " + other);
		}
	}
	
	private Unemployed getUnemployed(Colony colony, PopulationClass popClass) {
		Map<PopulationClass, Unemployed> map = all_unemployed.get(colony);
		if(map == null) {
			map = new HashMap<PopulationClass, Unemployed>();
			all_unemployed.put(colony,map);
		}
		Unemployed u = map.get(popClass);
		if(u == null) {
			u = new Unemployed();
			u.setColony(colony.getID());
			u.setPopClass(popClass);
			u = (Unemployed) entityStore.create(u);
			map.put(popClass,u);
		}
		return u;
	}

	private void doMigration(Unemployed unemployed, List<Colony> colonies,
			double distanceModifier) {
		if (unemployed.getQuantity() < 1)
			return;
		for (Colony colony : colonies) {
			doMigration(unemployed, colony, distanceModifier);
		}
	}

	/* (non-Javadoc)
	 * @see starcorp.server.engine.AServerTask#doJob()
	 */
	@Override
	protected void doJob() throws Exception {
		List<AColonists> unemployed = entityStore.listUnemployed();
		for(AColonists col : unemployed) {
			Colony colony = (Colony) entityStore.load(Colony.class,col.getColony());
			PopulationClass popClass = col.getPopClass();
			Unemployed u = (Unemployed) col;
			Map<PopulationClass, Unemployed> entry =
				all_unemployed.get(colony);
			if(entry == null) {
				entry = new HashMap<PopulationClass,Unemployed>();
			}
			entry.put(popClass,u);
			all_unemployed.put(colony,entry);
		}
		int size = unemployed.size();
		int i = 1;
		log.info(this + ": " + size + " unemployed to process");
		for(Colony colony : all_unemployed.keySet()) {
			Map<PopulationClass, Unemployed> map = all_unemployed.get(colony);
			for(PopulationClass popClass : map.keySet()) {
				Unemployed col = map.get(popClass);
				if(log.isDebugEnabled())
					log.debug(this + ": " + i + " of " + size + ": " + col);
				doMigration(col);
				Thread.yield();
			}
		}
		for(Colony colony : all_unemployed.keySet()) {
			Map<PopulationClass, Unemployed> map = all_unemployed.get(colony);
			for(PopulationClass popClass : map.keySet()) {
				Unemployed col = map.get(popClass);
				entityStore.update(col);
			}
		}
	}

	/* (non-Javadoc)
	 * @see starcorp.server.engine.AServerTask#getLog()
	 */
	@Override
	protected Log getLog() {
		return log;
	}

}
