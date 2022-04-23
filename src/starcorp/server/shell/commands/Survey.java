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
package starcorp.server.shell.commands;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.Planet;
import starcorp.common.entities.ResourceDeposit;
import starcorp.common.entities.StarSystem;
import starcorp.common.types.AItemType;
import starcorp.common.types.AtmosphereType;
import starcorp.common.types.Coordinates2D;
import starcorp.common.types.Resources;
import starcorp.server.engine.AServerTask;
import starcorp.server.entitystore.IEntityStore;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.Survey
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 21 Sep 2007
 */
public class Survey extends ACommand {
	private static Log log = LogFactory.getLog(Survey.class); 
	public static Set<starcorp.server.Util.SuitableLocation> findSuitableLocations(StarSystem system, IEntityStore entityStore, List<AItemType> types) {
		List<AtmosphereType> atmospheres = AtmosphereType.listTypes(1.0);
		TreeSet<starcorp.server.Util.SuitableLocation> sorted = new TreeSet<starcorp.server.Util.SuitableLocation>();
		List<?> planets = entityStore.listPlanets(system.getID(), 5, atmospheres);
		for(Object o : planets) {
			Planet planet = (Planet) o;
			findSuitableLocations(planet, entityStore, types, sorted);
		}
		return sorted;
	}
	
	public static Set<starcorp.server.Util.SuitableLocation> findSuitableLocations(Planet planet, IEntityStore entityStore, List<AItemType> types) {
		return findSuitableLocations(planet, entityStore, types, new TreeSet<starcorp.server.Util.SuitableLocation>());
	}

	private static Set<starcorp.server.Util.SuitableLocation> findSuitableLocations(Planet planet, IEntityStore entityStore, List<AItemType> types, TreeSet<starcorp.server.Util.SuitableLocation> sorted) {
		Map<Coordinates2D, List<ResourceDeposit>> map = new HashMap<Coordinates2D, List<ResourceDeposit>>();
		List<?> list = entityStore.listDeposits(planet.getID(), types, 10000);
		for(Object o : list) {
			ResourceDeposit deposit = (ResourceDeposit) o;
			if(map.containsKey(deposit.getLocation())) {
				map.get(deposit.getLocation()).add(deposit);
			}
			else {
				ArrayList<ResourceDeposit> deposits = new ArrayList<ResourceDeposit>();
				deposits.add(deposit);
				map.put(deposit.getLocation(), deposits);
			}
		}
		for(Coordinates2D loc : map.keySet()) {
			List<ResourceDeposit> deposits = map.get(loc);
			int total = 0;
			for(ResourceDeposit deposit : deposits) {
				total += deposit.getYield();
			}
			starcorp.server.Util.SuitableLocation sl = new starcorp.server.Util.SuitableLocation();
			sl.location=loc;
			sl.planet=planet;
			sl.rating=total;
			sorted.add(sl);
		}
		return sorted;
	}
	
	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "survey system (System ID)\n\nSearches star system for planets suitable for colonisation.\n\nsurvey planet (Planet ID) (Type)\n\nSearches a planet map for suitable locations for colonisation. Only the top 10 locations are returned.";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "survey";
	}

	public AServerTask task(final Arguments args, final PrintWriter out) {
		return new AServerTask() {
			public String toString() {
				return super.toString() + (args.count() > 0 ?  " [" + args + "]" : "");
			}
			protected String getName() {
				return "survey";
			}
			protected Log getLog() {
				return log;
			}
			protected void doJob() throws Exception {
				String surveyType = args.get(0);
				List<AItemType> types;
				String typeParam = args.get(2);
				if("metal".equalsIgnoreCase(typeParam)) {
					types = Resources.listMetals();
				}
				else if("organic".equalsIgnoreCase(typeParam)) {
					types = Resources.listOrganics();
				}
				else if("fuel".equalsIgnoreCase(typeParam)) {
					types = Resources.listFuel();
				}
				else if("gas".equalsIgnoreCase(typeParam)) {
					types = Resources.listGas();
				}
				else if("fissile".equalsIgnoreCase(typeParam)) {
					types = Resources.listFissile();
				}
				else if("liquid".equalsIgnoreCase(typeParam)) {
					types = Resources.listLiquid();
				}
				else if("mineral".equalsIgnoreCase(typeParam)) {
					types = Resources.listMinerals();
				}
				else {
					types = AItemType.listTypes(Resources.class);
				}
				if("planet".equalsIgnoreCase(surveyType)) {
					int planetId = args.getAsInt(1);
					Planet planet = (Planet) entityStore.load(Planet.class, planetId);
					if(planet == null) {
						out.println();
						out.println("Invalid planet");
					}
					else {
						Set<starcorp.server.Util.SuitableLocation> sorted = findSuitableLocations(planet, entityStore, types);
						int size = sorted.size();
						if(size > 0) {
							out.println();
							out.println("Found " + size + " suitable locations:");
							int i = 0;
							for(starcorp.server.Util.SuitableLocation location : sorted) {
								if(i >= 10)
									break;
								out.println();
								out.println((i + 1) + ": " + location.location + " rating " + location.rating);
								i++;
							}
						}
						else {
							out.println();
							out.println("No suitable locations found.");
						}
					}
				}
				else if("system".equalsIgnoreCase(surveyType)) {
					int systemId = args.getAsInt(1);
					StarSystem system = (StarSystem) entityStore.load(StarSystem.class, systemId);
					if(system == null) {
						out.println();
						out.println("Invalid system");
					}
					else {
						Set<starcorp.server.Util.SuitableLocation> sorted = findSuitableLocations(system, entityStore, types);
						int size = sorted.size();
						if(size > 0) {
							out.println();
							out.println("Found " + size + " suitable locations:");
							int i = 0;
							for(starcorp.server.Util.SuitableLocation location : sorted) {
								if(i >= 10)
									break;
								out.println();
								out.println((i + 1) + ": " + location.location + " @ " + location.planet.getName() + " (" +  location.planet.getID() + ") rating " + location.rating);
								i++;
							}
						}
						else {
							out.println();
							out.println("No suitable locations found.");
						}
					}
				}
				else {
					out.println();
					out.println("Invalid argument");
				}
				out.print(Shell.PROMPT);
				out.flush();
			}
		};

	}

}
