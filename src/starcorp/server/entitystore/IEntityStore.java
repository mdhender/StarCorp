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
package starcorp.server.entitystore;

import java.util.List;
import java.util.Map;

import starcorp.common.entities.AColonists;
import starcorp.common.entities.AGovernmentLaw;
import starcorp.common.entities.FactoryQueueItem;
import starcorp.common.entities.IEntity;
import starcorp.common.entities.MarketItem;
import starcorp.common.entities.ResourceDeposit;
import starcorp.common.entities.StarSystemEntity;
import starcorp.common.entities.ColonistGrant;
import starcorp.common.entities.Colony;
import starcorp.common.entities.ColonyItem;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.DevelopmentGrant;
import starcorp.common.entities.Facility;
import starcorp.common.entities.FacilityLease;
import starcorp.common.entities.Planet;
import starcorp.common.entities.StarSystem;
import starcorp.common.entities.Starship;
import starcorp.common.entities.StarshipDesign;
import starcorp.common.entities.Unemployed;
import starcorp.common.entities.Workers;
import starcorp.common.types.AFacilityType;
import starcorp.common.types.AItemType;
import starcorp.common.types.AtmosphereType;
import starcorp.common.types.Coordinates3D;
import starcorp.common.types.CoordinatesPolar;
import starcorp.common.types.ICoordinates;
import starcorp.common.types.PopulationClass;

/**
 * starcorp.server.entitystore.IEntityStore
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public interface IEntityStore {
	public abstract void shutdown();

	public abstract void resetHappiness();
	public abstract void resetFacilityTransactions();
	public abstract void resetShipTimeUnits();
	
	public abstract int getNextQueuePosition(long factory);
	
	public abstract double getAveragePrice(long colony, AItemType type);
	public abstract double getAverageHappiness(long colony, PopulationClass popClass);
	
	public abstract long getCredits(long entity);
	public abstract long removeCredits(long entity, long credits, String reason);
	public abstract long addCredits(long entity, long credits, String reason);
	public abstract long transferCredits(long from, long to, long credits, String reason);
	
	public abstract IEntity load(Class<?> entityClass, long ID);
	public abstract IEntity create(IEntity entity);
	public abstract IEntity update(IEntity entity);
	public abstract List<IEntity> list(Class<?> entityClass);
	public abstract List<IEntity> listAll();
	public abstract void importEntity(IEntity entity); 
	
	public abstract void delete(IEntity entity);
	
	public abstract List<Object> query(String hql);
	
	public abstract List<?> listEntities(Class<?> entityClass);
	public abstract List<AGovernmentLaw> listLaws();
	public abstract List<AGovernmentLaw> listLawsByStarSystem(long systemId);
	
	public abstract List<StarSystem> listSystems(Coordinates3D origin, int range);
	
	public abstract List<StarSystemEntity> listSystemEntities(long star);
	public abstract List<StarSystemEntity> listSystemEntities(long star, long exclude);
	public abstract List<StarSystemEntity> listSystemEntities(long star, CoordinatesPolar location);
	public abstract List<StarSystemEntity> listSystemEntities(long star, CoordinatesPolar location, long exclude);

	public abstract List<Planet> listPlanets(long star);
	public abstract List<Planet> listPlanets(long star, int maxGravity, List<AtmosphereType> atmospheres);
	
	public abstract AGovernmentLaw getColonistGrant(long colony, PopulationClass popClass, boolean openOnly);
	
	public abstract List<Colony> listColonies();
	public abstract List<Colony> listColoniesByGovernment(long govt);
	public abstract List<Colony> listColoniesByPlanet(long planet);
	public abstract List<Colony> searchColonies(long system, CoordinatesPolar location, long excludePlanet);
	public abstract List<Colony> searchColonies(long system, CoordinatesPolar excludeLocation);
	public abstract List<Colony> searchColonies(long excludeSystem);
	public abstract Colony getColony(long planet, ICoordinates location);
	
	public abstract List<ColonyItem> listAllNPCItems(Class<?> typeClass, int minQty);
	public abstract List<ColonyItem> listItems(long owner);
	public abstract List<ColonyItem> listItems(long owner, long colony, List<AItemType> types);
	public abstract ColonyItem getItem(long colony, long owner, AItemType type);
	
	public abstract List<FactoryQueueItem> listQueueByCorporation(long corp);
	public abstract List<FactoryQueueItem> listQueue(long facility);
	
	public abstract Corporation getCorporation(String email);
	public abstract boolean authorize(String email, String password);

	public abstract List<DevelopmentGrant> listDevelopmentGrants(long owner, boolean openOnly);
	public abstract DevelopmentGrant getDevelopmentGrant(long colony, AFacilityType type, boolean openOnly);
	
	public abstract List<Facility> listFacilities();
	public abstract List<Facility> listNPCFactories();
	public abstract List<Facility> listNPCEmptyQueueFactories();
	public abstract List<Facility> listFacilitiesPowered(List<AFacilityType> types);
	public abstract List<Facility> listFacilities(long colony);
	public abstract List<Facility> listFacilitiesByOwner(long owner);
	public abstract List<Facility> listFacilities(long colony, Class<?> type);
	public abstract Map<Facility,List<AColonists>> mapFacilitiesWithWorkers(long colony, List<AFacilityType> types);
	public abstract Map<AFacilityType, Map<Facility, List<AColonists>>> mapFacilitiesWithWorkersByType(
			long colony, List<AFacilityType> types);
	public abstract Facility getFacility(long colony, long owner, Class<?> type);
	public abstract Facility getFacility(long colony, Class<?> type);
	
	public abstract List<FacilityLease> listLeases(long corp, boolean openOnly);
	public abstract FacilityLease getLease(long colony, long owner, AFacilityType type, boolean openOnly);
	
	public abstract List<MarketItem> listMarket(int minQty);
	public abstract List<MarketItem> listMarketBySeller(long seller, int minQty);
	public abstract List<MarketItem> listMarket(long colony, int minQty);
	public abstract List<MarketItem> listMarket(long colony, AItemType type, int minQty);
	public abstract List<MarketItem> listMarket(long colony, List<AItemType> types, int minQty);
	
	public abstract List<Starship> listShips(long owner);
	public abstract List<Starship> listShipsInOrbit(long orbitingPlanet, long excludeShip);
	public abstract List<Starship> listShipsDocked(long planet, ICoordinates location, long excludeShip);
	public abstract List<Starship> listShipsDocked(long colony, long excludeShip);
	
	public abstract List<StarshipDesign> listDesigns(long owner);
	
	public abstract List<AColonists> listColonists();
	public abstract List<AColonists> listColonists(long colony);
	public abstract List<AColonists> listColonists(long colony, PopulationClass popClass);
	public abstract List<AColonists> listWorkersByEmployer(long  corp);
	public abstract List<AColonists> listWorkersByFacility(long facility);
	public abstract List<AColonists> listWorkersByColony(long colony);
	public abstract List<AColonists> listWorkersByColony(long colony, PopulationClass popClass);
	public abstract Workers getWorkers(long facility, PopulationClass popClass);
	public abstract List<AColonists> listUnemployed();
	public abstract List<AColonists> listUnemployed(long colony);
	public abstract List<AColonists> listUnemployedByGovernment(long corpId);
	public abstract Unemployed getUnemployed(long colony, PopulationClass popClass);
	
	public abstract List<ResourceDeposit> listDepositsByColony(long colonyId);
	public abstract List<ResourceDeposit> listDeposits(long systemEntity);
	public abstract List<ResourceDeposit> listDeposits(long planet, ICoordinates location);
	public abstract List<ResourceDeposit> listDeposits(long planet, List<AItemType> types, int minTotal);
	
	public Map<AItemType, List<MarketItem>> mapMarketByItemType(long colony, List<AItemType> types);
	public Map<PopulationClass, ColonistGrant> mapColonistGrantsByPopClass(long colony, boolean openOnly);
}
