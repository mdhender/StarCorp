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
package starcorp.client.gui.panes;

import java.util.List;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.AEntityPane;
import starcorp.client.gui.windows.MainWindow;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Planet;
import starcorp.common.entities.StarSystem;
import starcorp.common.entities.StarSystemEntity;
import starcorp.common.entities.Starship;
import starcorp.common.entities.StarshipDesign;
import starcorp.common.entities.StellarAnomoly;
import starcorp.common.turns.TurnOrder;
import starcorp.common.turns.TurnReport;
import starcorp.common.types.CoordinatesPolar;
import starcorp.common.types.Items;

/**
 * starcorp.client.gui.StarshipPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class StarshipPane extends AEntityPane {
	private final Starship ship;
	
	public StarshipPane (MainWindow mainWindow, Starship ship) {
		super(mainWindow,ship);
		this.ship = ship;
	}
	
	private String getLocationDescription(Starship ship) {
		String desc;
		if(ship.inOpenSpace()) {
			StarSystem system = getTurnReport().getSystem(ship.getSystem());
			desc = ship.getLocation() + " in " + system.getDisplayName();
		}
		else if(ship.inOrbit()) {
			Planet planet = getTurnReport().getPlanet(ship.getPlanet());
			desc = "Orbiting " + planet.getDisplayName();
		}
		else if(ship.getColony() != 0) {
			Colony colony = getTurnReport().getColony(ship.getColony());
			desc = "Docked at " + colony.getDisplayName();
		}
		else {
			Planet planet = getTurnReport().getPlanet(ship.getPlanet());
			desc = "Docked at " + ship.getPlanetLocation() + " on " + planet.getDisplayName();
		}
		return desc;
	}
	
	private void createDataGroup(List<Widget> widgets) {
		Group grp = createGroup(getParent(), widgets, ""); 
		grp.setLayout(new GridLayout(2,false));
		grp.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,true));
		createLabel(grp, widgets, "Design:");
		
		createDesignLink(grp, widgets, ship.getDesign(), null);
		
		createLabel(grp, widgets, "Location:");
		
		String locDesc = getLocationDescription(ship);
		
		if(ship.getColony() > 0) {
			Colony colony = getTurnReport().getColony(ship.getColony());
			createColonyLink(grp, widgets, colony, locDesc);
		}
		else if(ship.getPlanet() > 0) {
			Planet planet = getTurnReport().getPlanet(ship.getPlanet());
			createPlanetLink(grp, widgets, planet, locDesc);
		}
		else {
			createLabel(grp, widgets, locDesc);
		}
		
		createLabel(grp, widgets, "TU Remaining:");
		createLabel(grp, widgets, String.valueOf(ship.getTimeUnitsRemaining()));

	}
	
	private void createActionGroups(List<Widget> widgets) {
		boolean hasSpecialAction = false;
		
		TurnReport report = getTurnReport();
		StarshipDesign design = ship.getDesign();
		CoordinatesPolar location = ship.getLocation();
		
		Set<StarSystemEntity> asteroids = report.getScannedAsteroids(location);
		Set<StarSystemEntity> gasfields = report.getScannedGasFields(location);
		Set<StellarAnomoly> anomolies = report.getScannedAnomolies(location);
		
		Group grpActions = createGroup(getParent(), widgets, "Special Actions");
		grpActions.setLayout(new GridLayout(4,false));
		grpActions.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,true));
		
		if(ship.inOrbit() && design.canProbePlanet()) {
			hasSpecialAction = true;
			Planet planet = getTurnReport().getPlanet(ship.getPlanet());
			TurnOrder order = probePlanetOrder(ship, planet);
			createOrderLink(grpActions, widgets, order, "Probe " + planet.getDisplayName());
		}
		else if(ship.isDocked()) {
			hasSpecialAction = true;
			TurnOrder order = prospectOrder(ship);
			createOrderLink(grpActions, widgets, order, "Prospect");
		}
		else if(ship.inOpenSpace()) {
			if(design.canScanStarSystem()) {
				hasSpecialAction = true;
				TurnOrder order = scanSystemOrder(ship);
				createOrderLink(grpActions, widgets, order, "Scan System");
			}
			if(design.getScanGalaxyRange() > 0) {
				hasSpecialAction = true;
				TurnOrder order = scanGalaxyOrder(ship);
				createOrderLink(grpActions, widgets, order, "Scan Galaxy");
			}
			if(anomolies.size() > 0) {
				hasSpecialAction = true;
				for(StellarAnomoly anomoly : anomolies) {
					TurnOrder order = investigateOrder(ship, anomoly);
					createOrderLink(grpActions, widgets, order, "Investigate " + anomoly.getDisplayName());
				}
			}
			if(design.canMineAsteroid() && asteroids.size() > 0) {
				hasSpecialAction = true;
				for(StarSystemEntity asteroid : asteroids) {
					TurnOrder order = mineAsteroidOrder(ship, asteroid);
					createOrderLink(grpActions, widgets, order, "Mine " + asteroid.getDisplayName());
				}
			}
			if(design.canMineGasField() && gasfields.size() > 0) {
				hasSpecialAction = true;
				for(StarSystemEntity gasfield : gasfields) {
					TurnOrder order = mineGasFieldOrder(ship, gasfield);
					createOrderLink(grpActions, widgets, order, "Mine " + gasfield.getDisplayName());
				}
			}
			if(design.canProbeField() && (asteroids.size() > 0 || gasfields.size() > 0)) {
				hasSpecialAction = true;
				for(StarSystemEntity entity : gasfields) {
					TurnOrder order = probeSystemOrder(ship, entity);
					createOrderLink(grpActions, widgets, order, "Probe " + entity.getDisplayName());
				}
				for(StarSystemEntity entity : asteroids) {
					TurnOrder order = probeSystemOrder(ship, entity);
					createOrderLink(grpActions, widgets, order, "Probe " + entity.getDisplayName());
				}
			}
		}
		
		if(!hasSpecialAction) {
			createLabel(grpActions, widgets, "None available");
		}
		
	}
	
	private void createMovementGroup(List<Widget> widgets) {
		Group grpMove = createGroup(getParent(), widgets, "Movement");
		grpMove.setLayout(new GridLayout(5,false));
		grpMove.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,true,2,1));
		if(ship.isDocked()) {
			// take off
			final TurnOrder takeOff = takeOffOrder(ship);
			createOrderLink(grpMove, widgets, takeOff, "Take Off")
			.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,5,1));
			final TurnOrder leaveOrbit = leaveOrbitOrder(ship);
			Set<Colony> colonies = getTurnReport().getColoniesByPlanet(ship.getPlanet());
			final Combo c = createEntitySelection(grpMove, widgets, colonies, "Colony:");
			c.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,3,1));
			final Button btnDockColony = createButton(grpMove, widgets, "Dock");
			btnDockColony.addListener(SWT.Selection, new Listener() {
				public void handleEvent (Event event) {
					Colony colony = (Colony) getComboValue(c);
					mainWindow.addTurnOrder(takeOff);
					TurnOrder order = dockOrder(ship, colony);
					mainWindow.addTurnOrder(order);
				}
			});
			Set<StarSystemEntity> entities = getTurnReport().getScannedEntitiesExcludeShips(ship.getSystem());
			if(entities.size() > 0) {
				final Combo cEntities = createEntitySelection(grpMove, widgets, entities, "In Star System:");
				cEntities.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,3,1));
				final Button btnMove = createButton(grpMove, widgets, "Move To");
				btnMove.addListener(SWT.Selection, new Listener() {
					public void handleEvent (Event event) {
						StarSystemEntity entity = (StarSystemEntity) getComboValue(cEntities);
						mainWindow.addTurnOrder(takeOff);
						mainWindow.addTurnOrder(leaveOrbit);
						TurnOrder order = moveOrder(ship, entity.getLocation().getQuadrant(), entity.getLocation().getOrbit());
						mainWindow.addTurnOrder(order);
						if(entity instanceof Planet) {
							order = orbitOrder(ship, (Planet)entity);
							mainWindow.addTurnOrder(order);
						}
					}
				});
			}
			final Text txtQuadrant = createIntegerInput(grpMove, widgets, "Qudrant:");
			final Text txtOrbit = createIntegerInput(grpMove, widgets, "Orbit:");
			final Button btnMove = createButton(grpMove, widgets, "Move");
			btnMove.addListener(SWT.Selection, new Listener() {
				public void handleEvent (Event event) {
					int quadrant = getIntegerTextValue(txtQuadrant);
					int orbit = getIntegerTextValue(txtOrbit);
					mainWindow.addTurnOrder(takeOff);
					mainWindow.addTurnOrder(leaveOrbit);
					TurnOrder order = moveOrder(ship, quadrant, orbit);
					mainWindow.addTurnOrder(order);
				}
			});
			Set<StarSystem> systems = getTurnReport().getSystems();
			final Combo cSystems = createEntitySelection(grpMove, widgets, systems, "Systems:");
			cSystems.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,3,1));
			final Button btnJump = createButton(grpMove, widgets, "Jump");
			btnJump.addListener(SWT.Selection, new Listener() {
				public void handleEvent (Event event) {
					StarSystem system = (StarSystem) getComboValue(cSystems);
					mainWindow.addTurnOrder(takeOff);
					mainWindow.addTurnOrder(leaveOrbit);
					TurnOrder order = jumpOrder(ship, system);
					mainWindow.addTurnOrder(order);
				}
			});
		}
		else if(ship.inOrbit()) {
			// dock or leave orbit
			final TurnOrder leaveOrbit = leaveOrbitOrder(ship);
			createOrderLink(grpMove, widgets, leaveOrbit, "Leave Orbit")
			.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,5,1));
			final Text txtX = createIntegerInput(grpMove, widgets, "X:");
			final Text txtY = createIntegerInput(grpMove, widgets, "Y:");
			final Button btnDockPlanet = createButton(grpMove, widgets, "Dock");
			btnDockPlanet.addListener(SWT.Selection, new Listener() {
				public void handleEvent (Event event) {
					int x = getIntegerTextValue(txtX);
					int y = getIntegerTextValue(txtY);
					TurnOrder order = dockOrder(ship, x, y);
					mainWindow.addTurnOrder(order);
				}
			});
			Set<Colony> colonies = getTurnReport().getColoniesByPlanet(ship.getPlanet());
			final Combo c = createEntitySelection(grpMove, widgets, colonies, "Colony:");
			c.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,3,1));
			final Button btnDockColony = createButton(grpMove, widgets, "Dock");
			btnDockColony.addListener(SWT.Selection, new Listener() {
				public void handleEvent (Event event) {
					Colony colony = (Colony) getComboValue(c);
					TurnOrder order = dockOrder(ship, colony);
					mainWindow.addTurnOrder(order);
				}
			});
			Set<StarSystemEntity> entities = getTurnReport().getScannedEntitiesExcludeShips(ship.getSystem());
			if(entities.size() > 0) {
				final Combo cEntities = createEntitySelection(grpMove, widgets, entities, "In Star System:");
				cEntities.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,3,1));
				final Button btnMove = createButton(grpMove, widgets, "Move To");
				btnMove.addListener(SWT.Selection, new Listener() {
					public void handleEvent (Event event) {
						StarSystemEntity entity = (StarSystemEntity) getComboValue(cEntities);
						mainWindow.addTurnOrder(leaveOrbit);
						TurnOrder order = moveOrder(ship, entity.getLocation().getQuadrant(), entity.getLocation().getOrbit());
						mainWindow.addTurnOrder(order);
						if(entity instanceof Planet) {
							order = orbitOrder(ship, (Planet)entity);
							mainWindow.addTurnOrder(order);
						}
					}
				});
			}
			final Text txtQuadrant = createIntegerInput(grpMove, widgets, "Qudrant:");
			final Text txtOrbit = createIntegerInput(grpMove, widgets, "Orbit:");
			final Button btnMove = createButton(grpMove, widgets, "Move");
			btnMove.addListener(SWT.Selection, new Listener() {
				public void handleEvent (Event event) {
					int quadrant = getIntegerTextValue(txtQuadrant);
					int orbit = getIntegerTextValue(txtOrbit);
					mainWindow.addTurnOrder(leaveOrbit);
					TurnOrder order = moveOrder(ship, quadrant, orbit);
					mainWindow.addTurnOrder(order);
				}
			});
			Set<StarSystem> systems = getTurnReport().getSystems();
			final Combo cSystems = createEntitySelection(grpMove, widgets, systems, "Systems:");
			cSystems.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,3,1));
			final Button btnJump = createButton(grpMove, widgets, "Jump");
			btnJump.addListener(SWT.Selection, new Listener() {
				public void handleEvent (Event event) {
					StarSystem system = (StarSystem) getComboValue(cSystems);
					mainWindow.addTurnOrder(leaveOrbit);
					TurnOrder order = jumpOrder(ship, system);
					mainWindow.addTurnOrder(order);
				}
			});
		}
		else {
			// orbit, move or jump
			Set<Planet> planets = getTurnReport().getPlanetsByLocation(ship.getSystem(), ship.getLocation());
			if(planets.size() > 0) {
				final Combo cPlanets = createEntitySelection(grpMove, widgets, planets, "Nearby Planets:");
				cPlanets.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,3,1));
				final Button btnOrbit = createButton(grpMove, widgets, "Orbit");
				btnOrbit.addListener(SWT.Selection, new Listener() {
					public void handleEvent (Event event) {
						Planet planet = (Planet) getComboValue(cPlanets);
						TurnOrder order = orbitOrder(ship, planet);
						mainWindow.addTurnOrder(order);
					}
				});
			}
			Set<StarSystemEntity> entities = getTurnReport().getScannedEntitiesExcludeShips(ship.getSystem());
			if(entities.size() > 0) {
				final Combo cEntities = createEntitySelection(grpMove, widgets, entities, "In Star System:");
				cEntities.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,3,1));
				final Button btnMove = createButton(grpMove, widgets, "Move To");
				btnMove.addListener(SWT.Selection, new Listener() {
					public void handleEvent (Event event) {
						StarSystemEntity entity = (StarSystemEntity) getComboValue(cEntities);
						TurnOrder order = moveOrder(ship, entity.getLocation().getQuadrant(), entity.getLocation().getOrbit());
						mainWindow.addTurnOrder(order);
						if(entity instanceof Planet) {
							order = orbitOrder(ship, (Planet)entity);
							mainWindow.addTurnOrder(order);
						}
					}
				});
			}
			final Text txtQuadrant = createIntegerInput(grpMove, widgets, "Qudrant:");
			final Text txtOrbit = createIntegerInput(grpMove, widgets, "Orbit:");
			final Button btnMove = createButton(grpMove, widgets, "Move");
			btnMove.addListener(SWT.Selection, new Listener() {
				public void handleEvent (Event event) {
					int quadrant = getIntegerTextValue(txtQuadrant);
					int orbit = getIntegerTextValue(txtOrbit);
					TurnOrder order = moveOrder(ship, quadrant, orbit);
					mainWindow.addTurnOrder(order);
				}
			});
			Set<StarSystem> systems = getTurnReport().getSystems();
			final Combo cSystems = createEntitySelection(grpMove, widgets, systems, "Systems:");
			cSystems.setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,3,1));
			final Button btnJump = createButton(grpMove, widgets, "Jump");
			btnJump.addListener(SWT.Selection, new Listener() {
				public void handleEvent (Event event) {
					StarSystem system = (StarSystem) getComboValue(cSystems);
					TurnOrder order = jumpOrder(ship, system);
					mainWindow.addTurnOrder(order);
				}
			});
		}
		
	}
	
	private void createCargoGroup(List<Widget> widgets) {
		StarshipDesign design = ship.getDesign();
		Group grpCargo = createGroup(getParent(), widgets, "Cargo");
		grpCargo.setLayout(new GridLayout(1,true));
		grpCargo.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,true,2,1));
		
		Group grpCapacity = createGroup(grpCargo, widgets, "Capacity");
		grpCapacity.setLayout(new GridLayout(3,false));
		grpCapacity.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
		
		if(design.getConsumerCapacity() > 0)
			createLabel(grpCapacity, widgets, "Consumer: " + ship.getCargoConsumerGoodsMass() + " of " + design.getConsumerCapacity() + "mu");
		if(design.getIndustrialCapacity() > 0)
			createLabel(grpCapacity, widgets, "Industrial: " + ship.getCargoIndustrialGoodsMass() + " of " +design.getIndustrialCapacity() + "mu");
		if(design.getModulesCapacity() > 0)
			createLabel(grpCapacity, widgets, "Modules: " + ship.getCargoModulesMass() + " of " +design.getModulesCapacity() + "mu");
		if(design.getOrganicsCapacity() > 0)
			createLabel(grpCapacity, widgets, "Organics: " + ship.getCargoOrganicsMass() + " of " +design.getOrganicsCapacity() + "mu");
		if(design.getLiquidGasCapacity() > 0)
			createLabel(grpCapacity, widgets, "Liquids / Gas: " + ship.getCargoLiquidGasMass() + " of " +design.getLiquidGasCapacity() + "mu");
		
		final int size = ship.getCargo().size();
		if(size > 0) {
			Group grpContents = createGroup(grpCargo, widgets, "Contents");
			grpContents.setLayout(new GridLayout(8,false));
			grpContents.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
			final Button[] checkboxes = new Button[size];
			int i = 0;
			for(Items item : ship.getCargo()) {
				if(item.getQuantity() > 0) {
					createItemLink(grpContents, widgets, item, null);
					checkboxes[i] = createCheckbox(grpContents, widgets, null);
					checkboxes[i].setData(item);
				}
				i++;
			}
			Group grpCargoActions = createGroup(grpCargo, widgets, "Actions");
			grpCargoActions.setLayout(new GridLayout(5,false));
			grpCargoActions.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false));
			final Text txtQty = createTextInput(grpCargoActions, widgets, "Quantity:");
			int txtColSpan = ship.getColony() == 0 ? 3 : 2; 
			txtQty.setLayoutData(new GridData(SWT.LEFT,SWT.DEFAULT,true,true,txtColSpan,1));
			final Button btnJettison = createButton(grpCargoActions, widgets, "Jettison");
			btnJettison.addListener(SWT.Selection, new Listener() {
				public void handleEvent (Event event) {
					int qty = getIntegerTextValue(txtQty);
					for(int i = 0; i < size; i++) {
						if(checkboxes[i] != null && checkboxes[i].getSelection()) {
							Items item = (Items) checkboxes[i].getData();
							TurnOrder order = jettisonOrder(ship, item.getTypeClass(), qty);
							mainWindow.addTurnOrder(order);
						}
					}
				}
			});
			if(ship.getColony() > 0) {
				final Button btnDeliver = createButton(grpCargoActions, widgets, "Deliver");
				btnDeliver.addListener(SWT.Selection, new Listener() {
					public void handleEvent (Event event) {
						int qty = getIntegerTextValue(txtQty);
						for(int i = 0; i < size; i++) {
							if(checkboxes[i] != null && checkboxes[i].getSelection()) {
								Items item = (Items) checkboxes[i].getData();
								TurnOrder order = deliverOrder(ship, item.getTypeClass(), qty);
								mainWindow.addTurnOrder(order);
							}
						}
					}
				});
				final Text txtSellQty = createTextInput(grpCargoActions, widgets, "Quantity:");
				final Text txtPrice = createTextInput(grpCargoActions, widgets, "Price:");
				final Button btnSell = createButton(grpCargoActions, widgets, "Sell");
				btnSell.addListener(SWT.Selection, new Listener() {
					public void handleEvent (Event event) {
						int qty = getIntegerTextValue(txtSellQty);
						int price = getIntegerTextValue(txtPrice);
						for(int i = 0; i < size; i++) {
							if(checkboxes[i] != null && checkboxes[i].getSelection()) {
								Items item = (Items) checkboxes[i].getData();
								TurnOrder order = sellOrder(ship, ship.getColony(),item.getTypeClass(), qty,price);
								mainWindow.addTurnOrder(order);
							}
						}
					}
				});
			}
			else if(ship.inOrbit()) {
				Set<Colony> colonies = getTurnReport().getColoniesByPlanet(ship.getPlanet());
				if(colonies.size() > 0) {
					final Combo c = createEntitySelection(grpCargoActions, widgets, colonies, "Colony:");
					c.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,false,false,4,1));
					final Text txtSellQty = createTextInput(grpCargoActions, widgets, "Quantity:");
					final Text txtPrice = createTextInput(grpCargoActions, widgets, "Price:");
					final Button btnSell = createButton(grpCargoActions, widgets, "Sell");
					btnSell.addListener(SWT.Selection, new Listener() {
						public void handleEvent (Event event) {
							int qty = getIntegerTextValue(txtSellQty);
							int price = getIntegerTextValue(txtPrice);
							Colony colony = (Colony) getComboValue(c);
							for(int i = 0; i < size; i++) {
								if(checkboxes[i] != null && checkboxes[i].getSelection()) {
									Items item = (Items) checkboxes[i].getData();
									TurnOrder order = sellOrder(ship, colony.getID(),item.getTypeClass(), qty,price);
									mainWindow.addTurnOrder(order);
								}
							}
						}
					});
				}
			}
		}
		
	}
	
	
	@Override
	protected void createWidgets(List<Widget> widgets) {
		super.createWidgets(widgets);
		createDataGroup(widgets);
		createActionGroups(widgets);
		createMovementGroup(widgets);
		createCargoGroup(widgets);
	}

}
