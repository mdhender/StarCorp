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
package starcorp.server.turns;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.AGovernmentLaw;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Facility;
import starcorp.common.entities.MarketItem;
import starcorp.common.entities.Planet;
import starcorp.common.entities.StarSystem;
import starcorp.common.entities.Starship;
import starcorp.common.entities.StarshipDesign;
import starcorp.common.turns.Turn;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.turns.TurnReport;
import starcorp.common.types.AItemType;
import starcorp.common.types.GalacticDate;
import starcorp.common.types.Items;
import starcorp.common.types.OrderType;
import starcorp.common.util.SendEmail;
import starcorp.common.util.ZipTools;
import starcorp.server.ServerConfiguration;
import starcorp.server.Util;
import starcorp.server.engine.AServerTask;

/**
 * starcorp.server.turns.TurnProcessor
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class TurnProcessor extends AServerTask {
	private static final Log log = LogFactory.getLog(TurnProcessor.class);
	private final SendEmail sendEmail;
	private final File reportsFolder;
	
	private int processed;
	
	public TurnProcessor() {
		this.sendEmail = new SendEmail(ServerConfiguration.SMTP_HOST_NAME,ServerConfiguration.SMTP_PORT,ServerConfiguration.SMTP_AUTH_USER,ServerConfiguration.SMTP_AUTH_PASSWORD);
		this.reportsFolder = new File(ServerConfiguration.REPORTS_FOLDER);
		if(!reportsFolder.exists()) {
			reportsFolder.mkdirs();
		}
	}
	
	public void zipAndEmail(Corporation corp, File file) throws MessagingException, IOException {
		GalacticDate date = ServerConfiguration.getCurrentDate();
		File[] files = {file};
		String zipFile = "report-" + date + ".zip";
		if(log.isDebugEnabled()) {
			log.debug("Zipping " + file + " to " + zipFile);
		}
		ZipTools.zip(files, zipFile);
		if(corp.getPlayerEmail() == null || corp.getPlayerEmail().length() < 1) {
			return;
		}
		String[] to = {corp.getPlayerName() + "<" + corp.getPlayerEmail() + ">"};
		String subject = "Starcorp turn " + date.getMonth() + "." + date.getYear();
		String message = "Please find your reports file attached.  Save to a convenient location and open it with your StarCorp client.";
		String from = ServerConfiguration.SERVER_EMAIL_TURNS;
		sendEmail.send(to, null, null, subject, message, from, zipFile);
	}
	
	private void processFolder(File folder) {
		File[] turns = folder.listFiles();
		for(int i = 0; i < turns.length; i++) {
			if(turns[i].isDirectory()) {
				processFolder(turns[i]);
			}
			else {
				Turn turn = null;
				boolean authorized = false;
				try {
					turn = new Turn(new FileInputStream(turns[i]));
					if(!Turn.VERSION.equals(turn.getVersion())) {
						turn.add(TurnError.ERROR_VERSION_INVALID);
					}
					else {
						authorized = authorize(turn.getCorporation());
						if(!authorized) {
							authorized = register(turn.getCorporation());
							if(!authorized) {
								turn.add(TurnError.ERROR_AUTHORIZATION_FAILED);
							}
						}
					}
					
				}
				catch(Exception e) {
					log.warn(this + ": Error reading turn file " + turns[i].getName() + " because " + e.getMessage(),e);
				}
				if(turn != null) {
					try {
						TurnReport report = authorized ? process(turn) : new TurnReport(turn);
						GalacticDate date = ServerConfiguration.getCurrentDate();
						Corporation corp = report.getTurn().getCorporation();
						String filename =  corp.getPlayerEmail() + "-turn-" + date.getMonth() + "-" +  date.getYear() + ".xml";
						File file = new File(ServerConfiguration.REPORTS_FOLDER,filename); 
						report.write(new FileWriter(file));
						zipAndEmail(corp, file);
						turns[i].delete();
					}
					catch(Exception e) {
						log.error(this + ": Error sending report for " + turn + " because " + e.getMessage(),e);
					}
				}
			}
		}
	}
	
	public Log getLog() {
		return log;
	}
	
	public void doJob() {
		log.info(this + ": Started processing turns.");
		processed = 0;
		File turnFolder = new File(ServerConfiguration.TURNS_FOLDER);
		if(turnFolder.exists()) {
			processFolder(turnFolder);
		}
		log.info(this + ": Finished processing turns.");
	}
	
	private void addPlanet(TurnReport report, long planetID) {
		if(report.getPlanet(planetID) == null) {
			Planet planet = (Planet)entityStore.load(Planet.class,planetID);
			report.addScanned(planet);
			if(planet.getOrbiting() > 0)
				addPlanet(report,planet.getOrbiting());
		}
	}
	
	private void addColony(TurnReport report, long colonyID) {
		if(report.getColony(colonyID) == null) {
			Colony colony = (Colony)entityStore.load(Colony.class, colonyID);
			report.addScanned(colony);
			addCorporation(report, colony.getGovernment());
			addPlanet(report, colony.getPlanet());
		}
	}
	
	private void addCorporation(TurnReport report, long corpID) {
		if(corpID != report.getTurn().getCorporation().getID() && 
				report.getCorporation(corpID) == null) {
			report.addScanned((Corporation)entityStore.load(Corporation.class,corpID));
		}
	}
	
	private void addFacility(TurnReport report, long facilityID) {
		Facility f = (Facility) entityStore.load(Facility.class, facilityID);
		report.addScanned(f);
		addColony(report,f.getColony());
		addCorporation(report,f.getOwner());
	}
	
	public TurnReport process(Turn turn) {
		if(log.isDebugEnabled()) {
			log.debug(turn);
		}
		Corporation corp = turn.getCorporation();
		TurnReport report = new TurnReport(turn);
		if(corp != null){
			for(TurnOrder order : turn.getOrders()) {
				order.setCorp(corp);
				TurnError error = process(order);
				if(error != null) {
					turn.add(error);
				}
				else {
					for(Object o : order.getReport().getScannedEntities(Colony.class)) {
						Colony c = (Colony)o;
						addColony(report, c.getID());
					}
					for(Object o : order.getReport().getScannedEntities(Planet.class)) {
						addPlanet(report,((Planet)o).getID());
					}
					for(Object o : order.getReport().getScannedEntities(Facility.class)) {
						Facility f = (Facility)o;
						addFacility(report, f.getID());
					}
					for(Object o : order.getReport().getScannedEntities(Corporation.class)) {
						addCorporation(report,((Corporation)o).getID());
					}
				}
				if(log.isDebugEnabled())
					log.debug("Processed " + order + " : error = " + error);
			}
			turn.setProcessedDate(ServerConfiguration.getCurrentDate());
			if(corp.getID() < 1) {
				String email = corp.getPlayerEmail();
				corp = entityStore.getCorporation(email);
				if(corp == null) {
					log.error("No corporation found with email " + email);
					return report;
				}
				else {
					turn.setCorporation(corp);
				}
			}
			report.addPlayerEntities(entityStore.listDesigns(corp.getID()));
			List<Facility> facilities = entityStore.listFacilitiesByOwner(corp.getID());
			for(Facility facility : facilities) {
				addFacility(report, facility.getID());
			}
			
			report.addPlayerEntities(facilities);
			List<Starship> ships = entityStore.listShips(corp.getID());
			for(Starship ship : ships) {
				if(ship.getColony() != 0) {
					addColony(report, ship.getColony());
				}
				if(ship.getPlanet() != 0) {
					addPlanet(report, ship.getPlanet());
				}
			}
			report.addPlayerEntities(ships);
			List<AGovernmentLaw> laws = new ArrayList<AGovernmentLaw>();
			for(long systemId : corp.getKnownSystems()) {
				laws.addAll(entityStore.listLawsByStarSystem(systemId));
			}
			if(log.isDebugEnabled()) {
				log.debug("Laws: " + laws.size());
			}
			report.setLaws(laws);
			report.setUnemployed(entityStore.listUnemployedByGovernment(turn.getCorporation().getID()));
			report.setItems(entityStore.listItems(turn.getCorporation().getID()));
			report.setEmployees(entityStore.listWorkersByEmployer(corp.getID()));
			report.setFactoryQueue(entityStore.listQueueByCorporation(corp.getID()));
			report.setCredits(entityStore.getCredits(corp.getID()));
			List<MarketItem> market = new ArrayList<MarketItem>();
			for(Long system : corp.getKnownSystems()) {
				StarSystem ss = (StarSystem) entityStore.load(StarSystem.class, system);
				report.addScanned(ss);
				for(Planet planet : entityStore.listPlanets(system)) {
					for(Colony colony : entityStore.listColoniesByPlanet(planet.getID())) {
						for(MarketItem item : entityStore.listMarket(colony.getID(),1)) {
							market.add(item);
							addCorporation(report,item.getSeller());
						}
						addColony(report,colony.getID());
					}
				}
			}
			report.setMarket(market);
			// TODO add recent CashTransaction for corporation to report
		}
		log.info(this + ": Processed turn from " + turn.getCorporation() + ". Order: " + turn.getOrders().size() + ". Errors: " + turn.getErrors().size());
		processed++;
		return report;
	}
	
	private boolean register(Corporation corp) {
		Corporation existing = entityStore.getCorporation(corp.getPlayerEmail());
		if(existing == null) {
			corp.setFoundedDate(ServerConfiguration.getCurrentDate());
			corp = (Corporation) entityStore.create(corp);
			entityStore.addCredits(corp.getID(), ServerConfiguration.SETUP_INITIAL_CREDITS, ServerConfiguration.SETUP_DESCRIPTION);
			corp = createStartingPosition(corp);
			return true;
		}
		else {
			return false;
		}
	}
	
	private Corporation createStartingPosition(Corporation corp){
		// TODO make this configurable esp starting colony
		StarshipDesign design = new StarshipDesign();
		design.setDesignDate(ServerConfiguration.getCurrentDate());
		design.setName("Explorer");
		design.setOwner(corp.getID());
		design.setHulls(new Items(AItemType.getType("command-deck"),1));
		design.setHulls(new Items(AItemType.getType("crew-deck"),4));
		design.setHulls(new Items(AItemType.getType("impulse-drive"),2));
		design.setHulls(new Items(AItemType.getType("warp-drive-I"),1));
		design.setHulls(new Items(AItemType.getType("light-cargo"),1));
		design.setHulls(new Items(AItemType.getType("living-quarters"),1));
		design.setHulls(new Items(AItemType.getType("reinforced-cargo"),1));
		design.setHulls(new Items(AItemType.getType("tanker"),1));
		design.setHulls(new Items(AItemType.getType("gas-collector"),1));
		design.setHulls(new Items(AItemType.getType("mining-platform"),1));
		design.setHulls(new Items(AItemType.getType("scanner"),1));
		design.setHulls(new Items(AItemType.getType("lab"),1));
		design.setHulls(new Items(AItemType.getType("probe"),1));
		design = (StarshipDesign) entityStore.create(design);
		List<Colony> colonies = entityStore.listColonies();
		Colony colony = colonies.get(Util.rnd.nextInt(colonies.size()));
		Planet planet = (Planet) entityStore.load(Planet.class, colony.getPlanet());
		corp.add(planet.getSystem());
		entityStore.update(corp);
		Starship ship = new Starship();
		ship.setBuiltDate(ServerConfiguration.getCurrentDate());
		ship.setDesign(design);
		ship.setName("SS " + corp.getName());
		ship.setColony(colony.getID());
		ship.setPlanet(planet.getID());
		ship.setPlanetLocation(colony.getLocation());
		ship.setSystem(planet.getSystem());
		ship.setLocation(planet.getLocation());
		ship.setOwner(corp.getID());
		
		entityStore.create(ship);
		return corp;
	}
	
	private boolean authorize(Corporation corp) {
		return entityStore.authorize(corp.getPlayerEmail(), corp.getPlayerPassword());
	}
	
	private TurnError process(TurnOrder order) {
		TurnError error = null;
		OrderType type = order.getType();
		if(type == null) {
			return new TurnError(TurnError.INVALID_ORDER_TYPE,order);
		}
		AOrderProcessor processor = AOrderProcessor.getProcessor(type.getKey());
		if(processor == null) {
			error = new TurnError(TurnError.INVALID_ORDER_TYPE,order);
		}
		else {
			processor.setEntityStore(entityStore);
			error = processor.process(order);
		}
		return error;
		
	}

	public int getProcessed() {
		return processed;
	}
}
