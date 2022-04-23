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
package starcorp.common.turns;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import starcorp.common.entities.AColonists;
import starcorp.common.entities.AGovernmentLaw;
import starcorp.common.entities.Colony;
import starcorp.common.entities.ColonyItem;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Facility;
import starcorp.common.entities.FacilityLease;
import starcorp.common.entities.FactoryQueueItem;
import starcorp.common.entities.IEntity;
import starcorp.common.entities.MarketItem;
import starcorp.common.entities.Planet;
import starcorp.common.entities.StarSystem;
import starcorp.common.entities.StarSystemEntity;
import starcorp.common.entities.Starship;
import starcorp.common.entities.StarshipDesign;
import starcorp.common.entities.StellarAnomoly;
import starcorp.common.entities.Workers;
import starcorp.common.types.AFacilityType;
import starcorp.common.types.CoordinatesPolar;
import starcorp.common.util.Util;

/**
 * starcorp.client.turns.TurnReport
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class TurnReport {

	private Set<Colony> colonies = new HashSet<Colony>();
	private Set<Corporation> corporations = new HashSet<Corporation>();
	private long credits;
	private List<AColonists> employees;
	private Set<Facility> facilities = new HashSet<Facility>();
	private List<FactoryQueueItem> factoryQueue;
	private List<ColonyItem> items;
	private List<AGovernmentLaw> laws;
	private List<MarketItem> market;
	private Set<Planet> planets = new HashSet<Planet>();
	private List<IEntity> playerEntities = new ArrayList<IEntity>(); 
	private Set<StarSystem> systems = new HashSet<StarSystem>();
	private Turn turn;
	private List<AColonists> unemployed;
	
	public TurnReport(Element e) {
		readXML(e);
	}
	
	public TurnReport(InputStream is) throws DocumentException {
		SAXReader sax = new SAXReader();
		Document doc = sax.read(is);
		readXML(doc.getRootElement().element("turn-report"));
	}
	
	public TurnReport(Turn turn) {
		this.turn = turn;
	}
	
	public void addPlayerEntities(List<?> entities) {
		for(Object o : entities) {
			IEntity entity = (IEntity) o;
			addPlayerEntity(entity);
			
		}
	}

	public void addPlayerEntity(IEntity entity) {
		playerEntities.add(entity);
		if(entity instanceof Facility)
			facilities.add((Facility)entity);
	}

	public void addScanned(Colony colony) {
		colonies.add(colony);
	}
	
	public void addScanned(Corporation corp) {
		corporations.add(corp);
	}

	public void addScanned(Facility facility) {
		facilities.add(facility);
	}

	public void addScanned(Planet planet) {
		planets.add(planet);
	}
	
	public void addScanned(StarSystem system) {
		systems.add(system);
	}
	
	public int countPlayerFacilities() {
		int count = 0;
		for(Object o : playerEntities) {
			if(o instanceof Facility) {
				count++;
			}
		}
		return count;
	}
	
	public int countPlayerStarships() {
		return getPlayerStarships().size();
	}
	
	public Set<Colony> getColonies() {
		return colonies;
	}
	
	public Set<Colony> getColoniesByPlanet(long planet) {
		Set<Colony> colonies = new TreeSet<Colony>();
		for(Colony colony : getColonies()) {
			if(colony.getPlanet() == planet)
				colonies.add(colony);
		}
		return colonies;
	}
	
	public Set<Colony> getColoniesByGovernment(long government) {
		Set<Colony> colonies = new TreeSet<Colony>();
		for(Colony colony : getColonies()) {
			if(colony.getGovernment() == government)
				colonies.add(colony);
		}
		return colonies;
	}

	public Colony getColony(long ID) {
		if(colonies == null || ID < 1) {
			return null;
		}
		for(Colony colony : getColonies()) {
			if(colony.getID() == ID)
				return colony;
		}
		return null;
	}
	
	public Corporation getCorporation(long ID) {
		if(turn.getCorporation().getID() == ID)
			return turn.getCorporation();
		if(corporations == null || ID < 1) {
			return null;
		}
		for(Corporation c : corporations) {
			if(c.getID() == ID)
				return c;
		}
		return null;
	}
	
	public Set<Corporation> getCorporations() {
		return corporations;
	}
	
	public long getCredits() {
		return credits;
	}
	
	public List<AColonists> getEmployees() {
		return employees;
	}
	
	public Set<AColonists> getEmployees(Facility facility) {
		Set<AColonists> set = new TreeSet<AColonists>();
		for(AColonists colonist : employees) {
			Workers w = (Workers) colonist;
			if(facility.getID() == w.getFacility()) {
				set.add(w);
			}
		}
		return set;
	}

	public Set<Facility> getFacilities() {
		return facilities;
	}

	public Facility getFacility(long ID) {
		if(facilities == null || ID < 1) {
			return null;
		}
		for(Facility f : facilities) {
			if(f.getID() == ID)
				return f;
		}
		return null;
	}

	public List<FactoryQueueItem> getFactoryQueue() {
		return factoryQueue;
	}
	
	public List<ColonyItem> getItems() {
		return items;
	}
	
	public List<FacilityLease> getLeases(long colonyId, boolean availableOnly) {
		List<FacilityLease> list = new ArrayList<FacilityLease>();
		for(AGovernmentLaw law : laws) {
			if(law instanceof FacilityLease) {
				FacilityLease lease = (FacilityLease) law;
				if(lease.getColony() == colonyId) {
					if(availableOnly) {
						if(lease.isAvailable() && lease.getLicensee() == 0) {
							list.add(lease);
						}
					}
					else {
						list.add(lease);
					}
					
				}
			}
		}
		return list;
	}
	
	public List<AGovernmentLaw> getLaws() {
		return laws;
	}
	
	public List<MarketItem> getMarket() {
		return market;
	}
	
	public Map<Long, List<MarketItem>> getMarketByColony() {
		Map<Long, List<MarketItem>> map = new TreeMap<Long, List<MarketItem>>();
		for(MarketItem item : getMarket()) {
			List<MarketItem> list = map.get(item.getColony());
			if(list == null) {
				list = new ArrayList<MarketItem>();
				map.put(item.getColony(),list);
			}
			list.add(item);
		}
		return map;
	}
	
	public Map<Long, List<MarketItem>> getMarketBySeller() {
		Map<Long, List<MarketItem>> map = new TreeMap<Long, List<MarketItem>>();
		for(MarketItem item : getMarket()) {
			List<MarketItem> list = map.get(item.getSeller());
			if(list == null) {
				list = new ArrayList<MarketItem>();
				map.put(item.getSeller(),list);
			}
			list.add(item);
		}
		return map;
	}
	
	public Planet getPlanet(long ID) {
		if(planets == null || ID < 1) 
			return null;
		for(Planet p : planets) {
			if(p.getID() == ID)
				return p;
		}
		return null;
	}
	
	public Set<Planet> getPlanets() {
		return planets;
	}
	
	public Set<Planet> getPlanetsByLocation(long systemID, CoordinatesPolar location) {
		Set<Planet> entities = new TreeSet<Planet>();
		for(Planet p : planets) {
			if(p.getSystem() == systemID && p.getLocation().equals(location))
				entities.add(p);
		}
		return entities;
	}
	
	public Set<Planet> getPlanetsBySystem(long systemID) {
		Set<Planet> entities = new TreeSet<Planet>();
		for(Planet p : planets) {
			if(p.getSystem() == systemID)
				entities.add(p);
		}
		return entities;
	}
	
	public Set<StarshipDesign> getPlayerDesigns() {
		Set<StarshipDesign> list = new TreeSet<StarshipDesign>();
		for(Object o : playerEntities) {
			if(o instanceof StarshipDesign)
				list.add((StarshipDesign)o);
		}
		return list;
	}
	
	public List<IEntity> getPlayerEntities() {
		return playerEntities;
	}
	
	public Set<Colony> getPlayerGovernments() {
		return getColoniesByGovernment(turn.getCorporation().getID());
	}
	
	public Set<Facility> getPlayerFacilities() {
		Set<Facility> list = new TreeSet<Facility>();
		for(Object o : playerEntities) {
			if(o instanceof Facility) {
				list.add((Facility)o);
			}
		}
		return list;
	}

	public Map<Long,Set<Facility>> getPlayerFacilitiesByColony() {
		Map<Long, Set<Facility>> map = new TreeMap<Long, Set<Facility>>(); 
		for(Object o : playerEntities) {
			if(o instanceof Facility) {
				Facility f = (Facility) o;
				Set<Facility> list = map.get(f.getColony());
				if(list == null) {
					list = new TreeSet<Facility>();
					map.put(f.getColony(), list);
				}
				list.add(f);
			}
		}
		return map;
	}

	public Set<Facility> getPlayerFacilitiesByType(Class clazz) {
		Set<Facility> list = new TreeSet<Facility>();
		for(Object o : playerEntities) {
			if(o instanceof Facility) {
				Facility f = (Facility) o;
				if(f.getTypeClass().getClass().equals(clazz)) {
					list.add(f);
				}
			}
		}
		return list;
	}
	
	public Map<AFacilityType,Map<Long,Set<Facility>>> getPlayerFacilitiesByTypeAndColony() {
		Map<AFacilityType,Map<Long,Set<Facility>>> facMap = new TreeMap<AFacilityType, Map<Long,Set<Facility>>>();
		 
		for(Object o : playerEntities) {
			if(o instanceof Facility) {
				Facility f = (Facility) o;
				Map<Long, Set<Facility>> map = facMap.get(f.getTypeClass());
				if(map == null) {
					map = new TreeMap<Long, Set<Facility>>();
					facMap.put(f.getTypeClass(), map);
				}
				Set<Facility> list = map.get(f.getColony());
				if(list == null) {
					list = new TreeSet<Facility>();
					map.put(f.getColony(), list);
				}
				list.add(f);
			}
		}
		return facMap;
	}
	public Set<Starship> getPlayerOrbitingrStarships(Planet planet) {
		Set<Starship> list = new TreeSet<Starship>();
		for(Starship ship : getPlayerStarships() ) {
			if(ship.isOrbiting(planet))
				list.add(ship);
		}
		return list;
	}
	public Set<Starship> getPlayerStarships() {
		Set<Starship> list = new TreeSet<Starship>();
		for(Object o : playerEntities) {
			if(o instanceof Starship)
				list.add((Starship)o);
		}
		return list;
	}
	
	public Set<Starship> getPlayerStarships(Colony colony) {
		Set<Starship> list = new TreeSet<Starship>();
		for(Starship ship : getPlayerStarships() ) {
			if(colony.equals(ship.getColony()))
				list.add(ship);
		}
		return list;
	}
	
	public Set<Starship> getPlayerStarships(StarshipDesign design) {
		return getPlayerStarshipsByDesign().get(design);
	}

	public Map<StarshipDesign,Set<Starship>> getPlayerStarshipsByDesign() {
		Map<StarshipDesign,Set<Starship>> map = new TreeMap<StarshipDesign, Set<Starship>>();
		
		for(Object o : playerEntities) {
			if(o instanceof Starship) {
				Starship ship = (Starship) o;
				Set<Starship> list = map.get(ship.getDesign());
				if(list == null) {
					list = new TreeSet<Starship>();
					map.put(ship.getDesign(),list);
				}
				list.add(ship);
			}
		}
		return map;
	}

	public Set<Starship> getPlayerStarshipsInOrOrbitingColony(Colony colony) {
		Set<Starship> list = new TreeSet<Starship>();
		for(Starship ship : getPlayerStarships() ) {
			if(colony.getID() == ship.getColony() || ship.isOrbiting(colony.getPlanet()))
				list.add(ship);
		}
		return list;
	}

	public List<FactoryQueueItem> getQueue(Facility facility) {
		List<FactoryQueueItem> set = new ArrayList<FactoryQueueItem>();
//		System.out.println("queue items: " + factoryQueue.size());
		for(FactoryQueueItem item : factoryQueue) {
//			System.out.println("queue items factory: " + item.getFactory());
			if(facility.getID() == item.getFactory()) {
//				System.out.println("queue item matches!");
				set.add(item);
			}
		}
//		System.out.println("queue items for " + facility + ": " + set.size());
		return set;
	}

	public Set<StellarAnomoly> getScannedAnomolies() {
		Set<StellarAnomoly> list = new TreeSet<StellarAnomoly>();
		for(Object o : turn.getScannedEntities(StellarAnomoly.class)) {
			list.add((StellarAnomoly)o);
		}
		return list;
	}

	public Set<StellarAnomoly> getScannedAnomolies(CoordinatesPolar location) {
		Set<StellarAnomoly> list = new TreeSet<StellarAnomoly>();
		for(Object o : turn.getScannedEntities(StellarAnomoly.class)) {
			StellarAnomoly anomoly = (StellarAnomoly)o;
			if(anomoly.getLocation().equals(location))
				list.add(anomoly);
		}
		return list;
	}

	public Set<StarSystemEntity> getScannedAsteroids() {
		Set<StarSystemEntity> entities = new TreeSet<StarSystemEntity>();
		for(Object o :turn.getScannedEntities(StarSystemEntity.class)) {
			StarSystemEntity entity = (StarSystemEntity)o;
			if(entity.isAsteroid()) {
				entities.add(entity);
			}
		}
		return entities;
	}

	public Set<StarSystemEntity> getScannedAsteroids(CoordinatesPolar location) {
		Set<StarSystemEntity> entities = new TreeSet<StarSystemEntity>();
		for(Object o :turn.getScannedEntities(StarSystemEntity.class)) {
			StarSystemEntity entity = (StarSystemEntity)o;
			if(entity.isAsteroid() && entity.getLocation().equals(location)) {
				entities.add(entity);
			}
		}
		return entities;
	}

	public Set<StarSystemEntity> getScannedEntitiesExcludeShips(long systemID) {
		Set<StarSystemEntity> entities = new TreeSet<StarSystemEntity>();
		for(Object o :turn.getScannedEntities(StarSystemEntity.class)) {
			StarSystemEntity p = (StarSystemEntity)o;
			if(!(p.getClass().equals(Starship.class))) {
				if(p.getSystem() == systemID)
					entities.add(p);
			}
		}
		return entities;
	}

	public Set<StarSystemEntity> getScannedGasFields() {
		Set<StarSystemEntity> entities = new TreeSet<StarSystemEntity>();
		for(Object o :turn.getScannedEntities(StarSystemEntity.class)) {
			StarSystemEntity entity = (StarSystemEntity)o;
			if(entity.isGasfield()) {
				entities.add(entity);
			}
		}
		return entities;
	}

	public Set<StarSystemEntity> getScannedGasFields(CoordinatesPolar location) {
		Set<StarSystemEntity> entities = new TreeSet<StarSystemEntity>();
		for(Object o :turn.getScannedEntities(StarSystemEntity.class)) {
			StarSystemEntity entity = (StarSystemEntity)o;
			if(entity.isGasfield() && entity.getLocation().equals(location)) {
				entities.add(entity);
			}
		}
		return entities;
	}

	public StarSystem getSystem(long ID) {
		if(systems == null || ID < 1)
			return null;
		for(StarSystem system : systems) {
			if(system.getID() == ID)
				return system;
		}
		return null;
	}

	public Set<StarSystem> getSystems() {
		return systems;
	}

	public Turn getTurn() {
		return turn;
	}

	public void readXML(Element e) {
		this.turn = new Turn(e.element("turn"));
		for(Iterator<?> i = e.element("player-entities").elementIterator("entity"); i.hasNext();) {
			IEntity entity = Util.fromXML((Element)i.next());
			if(entity != null)
				playerEntities.add(entity);
		}
		this.credits = Long.parseLong(e.attributeValue("credits","0"));
		factoryQueue = new ArrayList<FactoryQueueItem>();
		Element eQueue = e.element("factory-queue");
		if(eQueue != null) {
			for(Iterator<?> i = eQueue.elementIterator("entity"); i.hasNext();) {
				FactoryQueueItem item  = (FactoryQueueItem) Util.fromXML((Element)i.next());
				if(item != null) {
					factoryQueue.add(item);
				}
			}
		}
		facilities = new HashSet<Facility>();
		Element eFacility = e.element("facilities");
		if(eFacility != null) {
			for(Iterator<?> i = eFacility.elementIterator("entity"); i.hasNext();) {
				Facility f = (Facility) Util.fromXML((Element)i.next());
				if(f != null)
					facilities.add(f);
			}
		}
		corporations = new HashSet<Corporation>();
		Element eCorps = e.element("corporations");
		if(eCorps != null) {
			for(Iterator<?> i = eCorps.elementIterator("entity"); i.hasNext();) {
				Corporation c = (Corporation) Util.fromXML((Element)i.next());
				if(c != null)
					corporations.add(c);
			}
		}
		systems = new HashSet<StarSystem>();
		Element eSystems = e.element("systems");
		if(eSystems != null) {
			for(Iterator<?> i = eSystems.elementIterator("entity"); i.hasNext();) {
				StarSystem system = (StarSystem) Util.fromXML((Element)i.next());
				if(system != null)
					systems.add(system);
			}
		}
		planets = new HashSet<Planet>();
		Element ePlanets = e.element("planets");
		if(ePlanets != null) {
			for(Iterator<?> i = ePlanets.elementIterator("entity"); i.hasNext();) {
				Planet planet = (Planet) Util.fromXML((Element)i.next());
				if(planet != null)
					planets.add(planet);
			}
		}
		colonies = new HashSet<Colony>();
		Element eColonies = e.element("colonies");
		if(eColonies != null) {
			for(Iterator<?> i = eColonies.elementIterator("entity"); i.hasNext();) {
				Colony colony = (Colony) Util.fromXML((Element)i.next());
				if(colony != null)
					colonies.add(colony);
			}
		}
		employees = new ArrayList<AColonists>();
		Element eEmployees = e.element("employees");
		if(eEmployees != null) {
			for(Iterator<?> i = eEmployees.elementIterator("entity"); i.hasNext();) {
				AColonists worker  = (AColonists) Util.fromXML((Element)i.next());
				if(worker != null)
					employees.add(worker);
			}
		}
		unemployed = new ArrayList<AColonists>();
		Element eUnemployed = e.element("unemployed");
		if(eUnemployed != null) {
			for(Iterator<?> i = eUnemployed.elementIterator("entity"); i.hasNext();) {
				AColonists worker  = (AColonists) Util.fromXML((Element)i.next());
				if(worker != null)
					unemployed.add(worker);
			}
		}
		market = new ArrayList<MarketItem>();
		Element eMarket = e.element("market");
		if(eMarket != null) {
			for(Iterator<?> i = eMarket.elementIterator("entity"); i.hasNext();) {
				MarketItem item = (MarketItem) Util.fromXML((Element)i.next());
				if(item != null)
					market.add(item);
			}
		}
		laws = new ArrayList<AGovernmentLaw>();
		Element eLaws = e.element("laws");
		if(eLaws != null) {
			System.out.println(eLaws);
			for(Iterator<?> i = eLaws.elementIterator("entity"); i.hasNext();) {
				AGovernmentLaw item = (AGovernmentLaw) Util.fromXML((Element)i.next());
				if(item != null)
					laws.add(item);
			}
		}
		items = new ArrayList<ColonyItem>();
		Element eItems = e.element("items");
		if(eItems != null) {
			for(Iterator<?> i = eItems.elementIterator("entity"); i.hasNext();) {
				ColonyItem item = (ColonyItem) Util.fromXML((Element)i.next());
				if(item != null)
					items.add(item);
			}
		}
	}

	public void setColonies(Set<Colony> colonies) {
		this.colonies = colonies;
	}

	public void setCorporations(Set<Corporation> corporations) {
		this.corporations = corporations;
	}
	
	public void setCredits(long credits) {
		this.credits = credits;
	}

	public void setEmployees(List<AColonists> employees) {
		this.employees = employees;
	}

	public void setFacilities(Set<Facility> facilities) {
		this.facilities = facilities;
	}

	public void setFactoryQueue(List<FactoryQueueItem> factoryQueue) {
		this.factoryQueue = factoryQueue;
	}

	public void setItems(List<ColonyItem> items) {
		this.items = items;
	}

	public void setLaws(List<AGovernmentLaw> laws) {
		this.laws = laws;
	}

	public void setMarket(List<MarketItem> market) {
		this.market = market;
	}

	public void setPlanets(Set<Planet> planets) {
		this.planets = planets;
	}

	public void setSystems(Set<StarSystem> systems) {
		this.systems = systems;
	}

	public void setTurn(Turn turn) {
		this.turn = turn;
	}

	public Element toXML(Element parent) {
		Element root = parent.addElement("turn-report");
		root.addAttribute("credits", String.valueOf(credits));
		turn.toXML(root);
		Element e = root.addElement("player-entities");
		e.addAttribute("size", String.valueOf(playerEntities.size()));
		for(Iterator<?> i = playerEntities.iterator(); i.hasNext();) {
			IEntity entity = (IEntity) i.next();
			entity.toFullXML(e);
		}
		e = root.addElement("corporations");
		if(corporations != null) {
			e.addAttribute("size", String.valueOf(corporations.size()));
			for(Corporation c : corporations) {
				if(c != null)c.toBasicXML(e);
			}
		}
		e = root.addElement("facilities");
		if(facilities != null) {
			e.addAttribute("size", String.valueOf(facilities.size()));
			for(Facility f : facilities) {
				if(f != null)f.toBasicXML(e);
			}
		}
		e = root.addElement("systems");
		if(systems != null) {
			e.addAttribute("size", String.valueOf(systems.size()));
			for(StarSystem s : systems) {
				if(s != null)s.toBasicXML(e);
			}
		}
		e = root.addElement("planets");
		if(planets != null) {
			e.addAttribute("size", String.valueOf(planets.size()));
			for(Planet p : planets) {
				if(p != null)p.toBasicXML(e);
			}
		}
		e = root.addElement("colonies");
		if(colonies != null) {
			e.addAttribute("size", String.valueOf(colonies.size()));
			for(Colony c : colonies) {
				if(c != null)c.toBasicXML(e);
			}
		}
		e = root.addElement("unemployed");
		if(unemployed != null) {
			e.addAttribute("size", String.valueOf(unemployed.size()));
			for(AColonists c : unemployed) {
				if(c != null)c.toBasicXML(e);
			}
		}
		e = root.addElement("factory-queue");
		if(factoryQueue != null) {
			e.addAttribute("size", String.valueOf(factoryQueue.size()));
			for(FactoryQueueItem item : factoryQueue) {
				if(item != null)item.toBasicXML(e);
			}
		}
		e = root.addElement("employees");
		if(employees != null) {
			e.addAttribute("size", String.valueOf(employees.size()));
			for(AColonists worker : employees) {
				worker.toBasicXML(e);
			}
		}
		e = root.addElement("market");
		if(market != null) {
			e.addAttribute("size", String.valueOf(market.size()));
			for(MarketItem item : market) {
				item.toBasicXML(e);
			}
		}
		e = root.addElement("laws");
		if(laws != null) {
			e.addAttribute("size", String.valueOf(laws.size()));
			for(AGovernmentLaw item : laws) {
				item.toBasicXML(e);
			}
		}
		e = root.addElement("items");
		if(laws != null) {
			e.addAttribute("size", String.valueOf(items.size()));
			for(ColonyItem item : items) {
				item.toBasicXML(e);
			}
		}
		e = root.addElement("systems");
		return root;
	}

	public void write(Writer writer) throws IOException {
		Document doc = DocumentHelper.createDocument();
		toXML(doc.addElement("starcorp"));
		// OutputFormat format = OutputFormat.createCompactFormat();
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(
			writer, format
		);
		
		xmlWriter.write(doc);
		xmlWriter.close();
	}

	public List<AColonists> getUnemployed(long colonyId) {
		List<AColonists> list = new ArrayList<AColonists>();
		for(AColonists c : unemployed) {
			if(c.getColony() == colonyId)
				list.add(c);
		}
		return list;
	}

	public List<AColonists> getUnemployed() {
		return unemployed;
	}

	public void setUnemployed(List<AColonists> unemployed) {
		this.unemployed = unemployed;
	}
}
