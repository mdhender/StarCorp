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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.Colony;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Facility;
import starcorp.common.entities.Planet;
import starcorp.common.entities.StarSystem;
import starcorp.common.entities.Starship;
import starcorp.common.entities.StarshipDesign;
import starcorp.common.types.AFacilityType;
import starcorp.server.ServerConfiguration;
import starcorp.server.engine.AServerTask;
import starcorp.server.setup.AColonyTemplate;
import starcorp.server.setup.APlanetTemplate;
import starcorp.server.setup.ASystemTemplate;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.Create
 * 
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class Create extends ACommand {
	private static Log log = LogFactory.getLog(Create.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "create planet (Template) (System ID) (quadrant) (orbit) (Name)\n\nCreates a planet based on the PlanetTemplate at the specified location with the given name.\n\n"
				+ "create moon (Template) (Planet ID) (Name)\n\nCreates a planet based on the PlanetTemplate orbiting the specified planet with the given name.\n\n"
				+ "create system (Template) (x) (y) (z) (Name)\n\nCreates a new star system on the SystemTemplate at the specified location.\n\n"
				+ "create corp (Name) (Credits)\n\nCreates a new corporation.\n\n"
				+ "create colony (Template) (Corporation ID) (Planet ID) (x) (y)\n\nCreates a new colony based on the ColonyTemplate? owned by the specified corporation at the specified location.\n\n"
				+ "create design (Corporation ID) (Name) (Hull type 1) (Hull type 2) ... (Hull type n)\n\nCreates a new starship design owned by the specified corporation.\n\n"
				+ "create ship (Corporation ID) (Design ID) (Colony ID) (Name)\n\nCreates a new starship.\n\n"
				+ "create facility (Corporation ID) (Colony ID) (Type)\n\nCreates a new facility.";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "create";
	}

	public AServerTask task(final Arguments args, final PrintWriter out) {
		return new AServerTask() {
			public String toString() {
				return super.toString() + (args.count() > 0 ?  " [" + args + "]" : "");
			}
			protected Log getLog() {
				return log;
			}

			protected String getName() {
				return "create";
			}

			protected void doJob() throws Exception {
				String function = args.get(0);
				if ("planet".equalsIgnoreCase(function)) {
					String templateName = args.get(1);
					int systemId = args.getAsInt(2);
					int quadrant = args.getAsInt(3);
					int orbit = args.getAsInt(4);
					String name = args.concat(5);
					APlanetTemplate template = APlanetTemplate
							.getTemplate(templateName);
					if (template == null) {
						out.println();
						out.println("Unknown template");
					} else {
						template.setEntityStore(entityStore);
						Planet p = template.create(systemId, quadrant, orbit,
								name);
						if (p == null) {
							out.println();
							out.println("Failed.");
						} else {
							out.println();
							out.println("Created " + p);
						}
					}
				} else if ("moon".equalsIgnoreCase(function)) {
					String templateName = args.get(1);
					int planetId = args.getAsInt(2);
					String name = args.concat(3);
					APlanetTemplate template = APlanetTemplate
							.getTemplate(templateName);
					if (template == null) {
						out.println();
						out.println("Unknown template");
					} else {
						template.setEntityStore(entityStore);
						Planet p = template.create(planetId, name);
						if (p == null) {
							out.println();
							out.println("Failed.");
						} else {
							out.println();
							out.println("Created " + p);
						}
					}
				} else if ("system".equalsIgnoreCase(function)) {
					String templateName = args.get(1);
					int x = args.getAsInt(2);
					int y = args.getAsInt(3);
					int z = args.getAsInt(4);
					String name = args.concat(5);
					ASystemTemplate template = ASystemTemplate
							.getTemplate(templateName);
					if (template == null) {
						out.println();
						out.println("Unknown template");
					} else {
						template.setEntityStore(entityStore);
						StarSystem s = template.create(x, y, z, name);
						if (s == null) {
							out.println();
							out.println("Failed.");
						} else {
							out.println();
							out.println("Created " + s);
						}
					}
				} else if ("corp".equalsIgnoreCase(function)) {
					Corporation corp = new Corporation();
					int credits = args.getAsInt(1);
					corp.setFoundedDate(ServerConfiguration.getCurrentDate());
					corp.setName(args.concat(2));
					corp = (Corporation) entityStore.create(corp);
					entityStore.addCredits(corp.getID(), credits, "NPC Setup");
					out.println("Created " + corp);
				} else if ("colony".equalsIgnoreCase(function)) {
					String templateName = args.get(1);
					int govtId = args.getAsInt(2);
					int planetId = args.getAsInt(3);
					int x = args.getAsInt(4);
					int y = args.getAsInt(5);
					String name = args.concat(6);
					AColonyTemplate template = AColonyTemplate
							.getTemplate(templateName);
					if (template == null) {
						out.println();
						out.println("Unknown template");
					} else {
						template.setEntityStore(entityStore);
						Colony c = template
								.create(govtId, planetId, x, y, name);
						if (c == null) {
							out.println();
							out.println("Failed.");
						} else {
							out.println();
							out.println("Created " + c);
						}
					}
				} else if ("design".equalsIgnoreCase(function)) {
					int corpId = args.getAsInt(1);
					Corporation corp = (Corporation) entityStore.load(
							Corporation.class, corpId);
					if (corp != null) {
						String name = args.get(2);
						StarshipDesign design = new StarshipDesign();
						design.setDesignDate(ServerConfiguration
								.getCurrentDate());
						design.setName(name);
						design.setOwner(corp.getID());
						for (int i = 3; i < args.count(); i++) {
							design.addHulls(args.get(i));
						}
						if (design.isValid()) {
							entityStore.create(design);
							out.println();
							out.println("Created " + design);
						} else {
							out.println();
							out.println("Invalid design");
						}
					} else {
						out.println();
						out.println("Invalid corporation");
					}
				} else if ("ship".equalsIgnoreCase(function)) {
					int corpId = args.getAsInt(1);
					int designId = args.getAsInt(2);
					int colonyId = args.getAsInt(3);
					String name = args.get(4);
					Corporation corp = (Corporation) entityStore.load(
							Corporation.class, corpId);
					StarshipDesign design = (StarshipDesign) entityStore.load(
							StarshipDesign.class, designId);
					Colony colony = (Colony) entityStore.load(Colony.class,
							colonyId);

					if (corp == null || design == null || colony == null) {
						out.println();
						out.println("Invalid arguments");
					} else {
						Starship ship = new Starship();
						ship.setBuiltDate(ServerConfiguration.getCurrentDate());
						ship.setDesign(design);
						Planet planet = ((Planet) entityStore.load(Planet.class, colony.getPlanet()));
						ship.setLocation(planet.getLocation());
						ship.setName(name);
						ship.setOwner(corp.getID());
						ship.setPlanet(planet.getID());
						ship.setSystem(planet.getSystem());
						ship.setColony(colony.getID());
						entityStore.create(ship);
						out.println();
						out.println("Created " + ship);
					}
				} else if ("facility".equalsIgnoreCase(function)) {
					int corpId = args.getAsInt(1);
					int colonyId = args.getAsInt(2);
					String typeName = args.get(3);

					Corporation corp = (Corporation) entityStore.load(
							Corporation.class, corpId);
					Colony colony = (Colony) entityStore.load(Colony.class,
							colonyId);
					AFacilityType type = AFacilityType.getType(typeName);

					if (corp == null || colony == null || type == null) {
						out.println();
						out.println("Invalid arguments");
					} else {
						Facility facility = new Facility();
						facility.setBuiltDate(ServerConfiguration
								.getCurrentDate());
						facility.setColony(colony.getID());
						facility.setOpen(true);
						facility.setOwner(corp.getID());
						facility.setTypeClass(type);
						entityStore.create(facility);
						out.println();
						out.println("Created " + facility);
					}
				} else {
					out.println();
					out.println("Invalid arguments.");
				}
				out.print(Shell.PROMPT);
				out.flush();
			}
		};
	}

}
