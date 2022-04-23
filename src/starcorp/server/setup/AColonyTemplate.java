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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.AColonists;
import starcorp.common.entities.Colony;
import starcorp.common.entities.ColonyItem;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Facility;
import starcorp.common.entities.FacilityLease;
import starcorp.common.entities.MarketItem;
import starcorp.common.entities.Planet;
import starcorp.common.entities.ResourceDeposit;
import starcorp.common.entities.Workers;
import starcorp.common.types.AFacilityType;
import starcorp.common.types.AItemType;
import starcorp.common.types.BuildingModules;
import starcorp.common.types.ConsumerGoods;
import starcorp.common.types.Coordinates2D;
import starcorp.common.types.Factory;
import starcorp.common.types.IndustrialGoods;
import starcorp.common.types.Items;
import starcorp.common.types.PlanetMapSquare;
import starcorp.common.types.Population;
import starcorp.common.types.PopulationClass;
import starcorp.common.types.Resources;
import starcorp.common.types.ServiceFacility;
import starcorp.common.types.StarshipHulls;
import starcorp.common.util.PackageExplorer;
import starcorp.server.ServerConfiguration;
import starcorp.server.Util;
import starcorp.server.entitystore.IEntityStore;

/**
 * starcorp.server.setup.AColonyTemplate
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public abstract class AColonyTemplate {
	private static Map<String, AColonyTemplate> templates = new HashMap<String, AColonyTemplate>();
	
	static {
		List<Class> classes;
		try {
			classes = PackageExplorer.getClassesForPackage("starcorp.server.setup.colonies");
			for(Class clazz : classes) {
				AColonyTemplate template = (AColonyTemplate) clazz.newInstance();
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
	
	public static AColonyTemplate getTemplate(String name) {
		return templates.get(name);
	}
	
	protected Log log = LogFactory.getLog(AColonyTemplate.class);
	
	protected IEntityStore entityStore;
	
	public void setEntityStore(IEntityStore entityStore) {
		this.entityStore = entityStore;
	}
	
	public Colony create(int govtID, int planetID, int x, int y, String name) {
		Corporation govt = (Corporation) entityStore.load(Corporation.class, govtID);
		Planet planet = (Planet) entityStore.load(Planet.class, planetID);
		if(govt == null || planet == null) {
			return null;
		}
		Coordinates2D location = new Coordinates2D(x,y);
		List<Corporation> corps = new ArrayList<Corporation>();
		corps.add(govt);
		return create(govt,corps, planet,location,name);
	}
	
	public Colony create(Corporation govt, List<Corporation> corporations, Planet planet, Coordinates2D location, String name) {
		PlanetMapSquare sq = planet.get(location);
		if(sq == null) {
			System.out.println("Invalid location");
			return null;
		}
		if(entityStore.getColony(planet.getID(), location) != null) {
			System.out.println("Colony already exists at location!");
			return null;
		}
		Colony colony = new Colony();
		colony.setFoundedDate(ServerConfiguration.getCurrentDate());
		colony.setGovernment(govt.getID());
		colony.setHazardLevel(planet.getAtmosphereTypeClass().getHazardLevel() + sq.getTerrainType().getHazardLevel());
		colony.setLocation(location);
		colony.setName(name);
		colony.setPlanet(planet.getID());
		colony = (Colony) entityStore.create(colony);
		log.info("Created " + colony);
		List<Facility> facilities = createFacilities(colony, corporations);
		facilities.add(createFacility(colony, govt, "hub"));
		List<AColonists> population = populate(colony, facilities);
		createServiceFacilities(colony, corporations, facilities, population);
		createConsumerGoods(colony, population, corporations);
		createLeases(colony);
		createIndustrialGoods(colony, corporations);
		createBuildingModules(colony, corporations);
		createStarshipHulls(colony, corporations);
		return colony;
	}
	
	protected FacilityLease createLease(Colony colony, AFacilityType type) {
		FacilityLease lease = new FacilityLease();
		lease.setAvailable(true);
		lease.setColony(colony.getID());
		lease.setIssuedDate(ServerConfiguration.getCurrentDate());
		lease.setPrice(type.getTypicalPrice() / 10);
		lease.setTypeClass(type);
		lease = (FacilityLease) entityStore.create(lease);
		if(log.isDebugEnabled())log.debug("Created " + lease);
		return lease;
	}
	
	protected void createLeases(Colony colony) {
		Set<AFacilityType> generatorTypes = listSuitableGenerators(colony);
		for(AFacilityType type : generatorTypes) {
			for(int i = 0; i < 10; i++) {
				createLease(colony, type);
			}
		}
		List<AFacilityType> serviceTypes = AFacilityType.listTypes(ServiceFacility.class);
		for(AFacilityType type : serviceTypes) {
			int max = 10 / ((ServiceFacility)type).getQuality();
			for(int i = 0; i < max; i++) {
				createLease(colony, type);
			}
		}
		if(!hasOrbitalDock()) {
			AFacilityType type = AFacilityType.getType("dock");
			createLease(colony, type);
		}
		List<AFacilityType> factoryTypes = AFacilityType.listTypes(Factory.class);
		for(AFacilityType type : factoryTypes) {
			for(int i = 0; i < 5; i++) {
				createLease(colony, type);
			}
		}
	}
	
	protected void createIndustrialGoods(Colony colony, List<Corporation> corporations) {
		List<AItemType> types = AItemType.listTypes(IndustrialGoods.class);
		
		for(AItemType type : types) {
			createItem(colony, corporations.get(Util.rnd.nextInt(corporations.size())), type, getMarketSize(), type.getNPCPrice());
		}
	}
	
	protected void createBuildingModules(Colony colony, List<Corporation> corporations) {
		List<AItemType> types = AItemType.listTypes(BuildingModules.class);
		
		for(AItemType type : types) {
			createItem(colony, corporations.get(Util.rnd.nextInt(corporations.size())), type, getMarketSize() / 1000, type.getNPCPrice());
		}
	}
	
	protected void createStarshipHulls(Colony colony, List<Corporation> corporations) {
		List<AItemType> types = AItemType.listTypes(StarshipHulls.class);
		
		for(AItemType type : types) {
			createItem(colony, corporations.get(Util.rnd.nextInt(corporations.size())), type, getMarketSize() / 10000, type.getNPCPrice());
		}
	}

	protected void createConsumerGoods(Colony colony, List<AColonists> population, List<Corporation> corporations) {
		Map<PopulationClass,Integer> count = count(population);
		for(PopulationClass popClass : count.keySet()) {
			int n = count.get(popClass) * 100;
			int quality = popClass.getConsumerQualityRequired();
			AItemType food = ConsumerGoods.listFood(quality).get(0);
			AItemType drink = ConsumerGoods.listDrink(quality).get(0);
			AItemType intoxicant = ConsumerGoods.listIntoxicant(quality).get(0);
			AItemType clothes = ConsumerGoods.listClothes(quality).get(0);
			
			createItem(colony, corporations.get(Util.rnd.nextInt(corporations.size())), food, n, food.getNPCPrice());
			createItem(colony, corporations.get(Util.rnd.nextInt(corporations.size())),drink, n, drink.getNPCPrice());
			createItem(colony, corporations.get(Util.rnd.nextInt(corporations.size())),intoxicant, n, intoxicant.getNPCPrice());
			createItem(colony, corporations.get(Util.rnd.nextInt(corporations.size())),clothes, n, clothes.getNPCPrice());
		}
	}
	
	private void createPower(Colony colony, Facility facility) {
		int required = facility.getTypeClass().getPowerRequirement();
		required *= 100;
//		log.info("CREATE POWER x " + required);
		addItem(colony.getID(),facility.getOwner(),"nuclear-power",required);
	}
	
	protected MarketItem createItem(Colony colony, Corporation corp, AItemType type, int quantity, int price) {
		MarketItem item = new MarketItem();
		item.setColony(colony.getID());
		item.setCostPerItem(price);
		item.setItem(new Items(type,quantity));
		item.setSeller(corp.getID());
		entityStore.create(item);
		if(log.isDebugEnabled())log.debug("Created " + item);
		return item;
	}
	
	protected ColonyItem addItem(long colony, long owner, String type, int quantity) {
		ColonyItem item = entityStore.getItem(colony, owner, AItemType.getType(type)); 
		if(item == null) {
			item = new ColonyItem();
			item.setColony(colony);
			item.setItem(new Items());
			item.getItem().setType(type);
			item.setOwner(owner);
			entityStore.create(item);
		}
		item.add(quantity);
		entityStore.update(item);
		if(log.isDebugEnabled())log.debug("Created " + item);
		return item;
	}
	
	protected Map<PopulationClass,Integer> count(List<AColonists> population) {
		Map<PopulationClass,Integer> count = new HashMap<PopulationClass, Integer>();
		for(AColonists colonist : population) {
			PopulationClass popClass = colonist.getPopClass();
			int popCount = count.containsKey(popClass) ? count.get(popClass) : 0;
			popCount += colonist.getQuantity();
			count.put(colonist.getPopClass(), popCount);
		}
		return count;
	}
	
	protected void createServiceFacilities(Colony colony, List<Corporation> corporations, List<Facility> facilities, List<AColonists> population) {
		Map<PopulationClass,Integer> count = count(population);
		Map<AFacilityType, Integer> required = new HashMap<AFacilityType, Integer>();
		for(PopulationClass popClass : count.keySet()) {
			int n = count.get(popClass);
			int quality = popClass.getServiceQualityRequired();
			AFacilityType medical = ServiceFacility.listMedical(quality).get(0);
			AFacilityType fitness = ServiceFacility.listFitness(quality).get(0);
			AFacilityType ent = ServiceFacility.listEntertainment(quality).get(0);
			AFacilityType edu = ServiceFacility.listEducation(quality).get(0);
			int total = 0;
			if(required.containsKey(medical)) {
				total = required.get(medical);
			}
			total += n;
			required.put(medical,total);
			total = 0;
			if(required.containsKey(fitness)) {
				total = required.get(fitness);
			}
			total += n;
			required.put(fitness,total);
			total = 0;
			if(required.containsKey(ent)) {
				total = required.get(ent);
			}
			total += n;
			required.put(ent,total);
			total = 0;
			if(required.containsKey(edu)) {
				total = required.get(edu);
			}
			total += n;
			required.put(edu,total);
		}
		for(AFacilityType type : required.keySet()) {
			int totalPop = required.get(type);
			int total = totalPop / ServiceFacility.MAX_COLONISTS_SERVICED;
			if(totalPop % ServiceFacility.MAX_COLONISTS_SERVICED > 0)
				total++;
			for(int i = 0; i <total; i++) {
				Facility f = createFacility(colony, corporations.get(Util.rnd.nextInt(corporations.size())), type.getKey());
				populate(f, population);
				f.setServiceCharge(100 * ((ServiceFacility)type).getQuality());
				entityStore.update(f);
			}
		}
	}
	
	protected Workers createWorker(Facility facility, Population pop) {
		Workers worker = new Workers();
		worker.setColony(facility.getColony());
		worker.setFacility(facility.getID());
		worker.setSalary(pop.getPopClass().getNPCSalary());
		worker.setPopClass(pop.getPopClass());
		worker.setQuantity(pop.getQuantity());
		worker = (Workers) entityStore.create(worker);
		if(log.isDebugEnabled())log.debug("Created " + worker);
		return worker;
	}
	
	protected void populate(Facility facility, List<AColonists> colonists) {
		for(PopulationClass popClass : facility.getTypeClass().getWorkerRequirement().keySet()) {
			AFacilityType facType = facility.getTypeClass();
			Population pop = facType.getWorkerRequirement(popClass);
			colonists.add(createWorker(facility, pop));
		}
		
	}
	
	protected List<AColonists> populate(Colony colony, List<Facility> facilities) {
		List<AColonists> colonists = new ArrayList<AColonists>();
		for(Facility facility : facilities) {
			populate(facility, colonists);
		}
		return colonists;
	}
	
	protected Set<AFacilityType> listSuitableGenerators(Colony colony) {
		List<ResourceDeposit> deposits = entityStore.listDepositsByColony(colony.getID());
		Set<AFacilityType> set = new HashSet<AFacilityType>();
		for(ResourceDeposit rd : deposits) {
			Resources res = (Resources) rd.getTypeClass();
			set.addAll(res.listGenerators());
		}
		return set;
	}
	
	protected List<Facility> createFacilities(Colony colony, List<Corporation> corporations) {
		List<Facility> facilities = new ArrayList<Facility>();
		int generators = countGenerators();
		int lfact = countLightFactories();
		int hfact = countHeavyFactories();
		int shfact = countSuperHeavyFactories();
		int shipyards = countShipyards();
		Set<AFacilityType> generatorTypes = listSuitableGenerators(colony);
		for(int i = 0; i < generators; i++) {
			for(AFacilityType type : generatorTypes) {
				facilities.add(createFacility(colony,corporations.get(Util.rnd.nextInt(corporations.size())),  type.getKey()));
			}
		}
		for(int i = 0; i < lfact; i++) {
			facilities.add(createFacility(colony, corporations.get(Util.rnd.nextInt(corporations.size())), "light-factory"));
		}
		for(int i = 0; i < hfact; i++) {
			facilities.add(createFacility(colony, corporations.get(Util.rnd.nextInt(corporations.size())), "heavy-factory"));
		}
		for(int i = 0; i < shfact; i++) {
			facilities.add(createFacility(colony, corporations.get(Util.rnd.nextInt(corporations.size())), "super-heavy-factory"));
		}
		for(int i = 0; i < shipyards; i++) {
			facilities.add(createFacility(colony, corporations.get(Util.rnd.nextInt(corporations.size())), "shipyard"));
		}
		if(hasOrbitalDock()) {
			facilities.add(createFacility(colony, corporations.get(Util.rnd.nextInt(corporations.size())), "dock"));
		}
		return facilities;
	}
	
	protected Facility createFacility(Colony colony, Corporation corp, String type) {
		Facility facility = new Facility();
		facility.setBuiltDate(ServerConfiguration.getCurrentDate());
		facility.setColony(colony.getID());
		facility.setOpen(true);
		facility.setOwner(corp.getID());
		facility.setType(type);
		facility.setPowered(true);
		facility = (Facility) entityStore.create(facility);
		createPower(colony, facility);
		if(log.isDebugEnabled())log.debug("Created " + facility);
		return facility;
	}
	
	protected abstract int countGenerators();
	protected abstract int countLightFactories();
	protected abstract int countHeavyFactories();
	protected abstract int countSuperHeavyFactories();
	protected abstract int countShipyards();
	protected abstract boolean hasOrbitalDock();
	
	protected abstract int getMarketSize();
}
