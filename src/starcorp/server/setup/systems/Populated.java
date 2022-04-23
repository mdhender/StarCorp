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
package starcorp.server.setup.systems;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Planet;
import starcorp.common.entities.StarSystem;
import starcorp.common.types.AItemType;
import starcorp.common.types.Coordinates2D;
import starcorp.common.types.Coordinates3D;
import starcorp.common.types.Resources;
import starcorp.server.ServerConfiguration;
import starcorp.server.Util;
import starcorp.server.setup.AColonyTemplate;
import starcorp.server.shell.commands.Survey;

/**
 * starcorp.server.setup.systems.Populated
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 21 Sep 2007
 */
public class Populated extends Standard {
	private static final long STARTING_NPC_CREDITS= 1000000000000L; 
	@Override
	public StarSystem create(Coordinates3D location, String name) {
		StarSystem system = super.create(location, name);
		Corporation govt = new Corporation();
		govt.setFoundedDate(ServerConfiguration.getCurrentDate());
		govt.setName(Util.getRandomGovernmentName(name));
		govt.add(system.getID());
		entityStore.create(govt);
		log.info("Created " + govt);
		entityStore.addCredits(govt.getID(), STARTING_NPC_CREDITS, "NPC Setup");
		int otherCorps = Util.rnd.nextInt(10) + 5;
		List<Corporation> tradingCompanies = new ArrayList<Corporation>();
		tradingCompanies.add(govt);
		for(int i = 0; i < otherCorps; i++) {
			Corporation corp = new Corporation();
			corp.setFoundedDate(ServerConfiguration.getCurrentDate());
			corp.setName(Util.getRandomCorporationName());
			corp.add(system.getID());
			corp = (Corporation) entityStore.create(corp);
			log.info("Created " + corp);
			entityStore.addCredits(corp.getID(), STARTING_NPC_CREDITS, "NPC Setup");
			tradingCompanies.add(corp);
		}
		Set<starcorp.server.Util.SuitableLocation> suitableLocations = Survey.findSuitableLocations(system, entityStore, AItemType.listTypes(Resources.class));
		boolean capitol = false;
		int cities = 0;
		int towns = 0;
		int outposts = 0;
		for(starcorp.server.Util.SuitableLocation loc : suitableLocations) {
			if(!capitol) {
				createColony(govt,tradingCompanies,loc.planet,loc.location,"Capitol");
				capitol = true;
			}
			else if(cities < 2) {
				createColony(govt,tradingCompanies,loc.planet,loc.location,"City");
				cities++;
			}
			else if(towns < 3) {
				createColony(govt,tradingCompanies,loc.planet,loc.location,"Town");
				towns++;
			}
			else if(outposts < 10) {
				createColony(govt,tradingCompanies,loc.planet,loc.location,"Outpost");
				outposts++;
			}
			else {
				break;
			}
		}
		return system;
	}
	
	
	protected Colony createColony(Corporation govt, List<Corporation> tradingCompanies, Planet planet, Coordinates2D location, String templateName) {
		AColonyTemplate template = AColonyTemplate.getTemplate(templateName);
		template.setEntityStore(entityStore);
		Colony colony = template.create(govt, tradingCompanies, planet, location, Util.getRandomColonyName());
		return colony;
	}

}
