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
package starcorp.server.turns.orders;

import java.util.Iterator;

import starcorp.common.entities.Colony;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Facility;
import starcorp.common.entities.Planet;
import starcorp.common.entities.Starship;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.AFacilityType;
import starcorp.common.types.ColonyHub;
import starcorp.common.types.Coordinates2D;
import starcorp.common.types.Items;
import starcorp.common.types.OrderType;
import starcorp.common.types.PlanetMapSquare;
import starcorp.server.ServerConfiguration;
import starcorp.server.turns.AOrderProcessor;

/**
 * starcorp.server.turns.FoundColony
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class FoundColony extends AOrderProcessor {
	// TODO test
	@Override
	public TurnError process(TurnOrder order) {
		TurnError error = null;
		Corporation corp = order.getCorp();
		int starshipId = order.getAsInt(0);
		String hubTypeKey = order.get(1);
		String name = order.get(2);
		
		Starship ship = (Starship) entityStore.load(Starship.class, starshipId);
		ColonyHub hubType = (ColonyHub) AFacilityType.getType(hubTypeKey);
		if(ship == null || ship.getOwner() != corp.getID()) {
			error = new TurnError(TurnError.INVALID_SHIP,order);
		}
		else if(hubType == null) {
			error = new TurnError(TurnError.INVALID_FACILITY_TYPE,order);
		}
		else {
			Planet planet = (Planet) entityStore.load(Planet.class, ship.getPlanet());
			Coordinates2D location = ship.getPlanetLocation();
			if(planet == null || location == null) {
				error = new TurnError(TurnError.INVALID_LOCATION,order);
			}
			else {
				Colony colony = entityStore.getColony(planet.getID(), location);
				if(colony != null) {
					error = new TurnError(TurnError.INVALID_LOCATION,order);
				}
				else {
					PlanetMapSquare sq = planet.get(location);
					double hazardLevel = sq.getTerrainType().getHazardLevel() + planet.getAtmosphereTypeClass().getHazardLevel();
					
					Facility hub = new Facility();
					hub.setBuiltDate(ServerConfiguration.getCurrentDate());
					hub.setOpen(true);
					hub.setOwner(corp.getID());
					hub.setTypeClass(hubType);
					
					boolean hasNeededModules = true;
					Iterator<Items> i = hubType.getBuildingRequirement().iterator();
					while(i.hasNext()) {
						Items item = i.next();
						Items cargo = ship.getCargo(item.getTypeClass());
						if(cargo == null || cargo.getQuantity() < item.getQuantity()) {
							error = new TurnError(TurnError.INSUFFICIENT_BUILDING_MODULES,order);
							hasNeededModules = false;
							break;
						}
					}
					
					if(hasNeededModules) {
						i = hubType.getBuildingRequirement().iterator();
						
						while(i.hasNext()) {
							Items item = i.next();
							Items cargo = ship.getCargo(item.getTypeClass());
							if(cargo != null || !(cargo.getQuantity() < item.getQuantity())) {
								cargo.remove(item.getQuantity());
							}
						}
						
						
					
						colony = new Colony();
						colony.setFoundedDate(ServerConfiguration.getCurrentDate());
						colony.setGovernment(corp.getID());
						colony.setHazardLevel(hazardLevel);
						colony.setLocation(location);
						colony.setName(name);
						colony.setPlanet(planet.getID());
				
						entityStore.create(colony);
						hub.setColony(colony.getID());
						entityStore.create(hub);
						entityStore.update(ship);
						OrderReport report = new OrderReport(order,colony,ship);
						report.add(colony.getName());
						report.add(colony.getID());
						report.add(planet.getName());
						report.add(planet.getID());
						order.setReport(report);
					}
				}
			}
		}
		
		return error;
	}
	@Override
	public String getKey() {
		return OrderType.FOUND_COLONY;
	}

}
