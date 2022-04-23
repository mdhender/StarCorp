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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.AEntityPane;
import starcorp.client.gui.windows.MainWindow;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Planet;
import starcorp.common.entities.StarSystem;
import starcorp.common.entities.StarSystemEntity;
import starcorp.common.types.PlanetMapSquare;

/**
 * starcorp.client.gui.SystemEntityPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 26 Sep 2007
 */
public class SystemEntityPane extends AEntityPane {

	private final StarSystemEntity entity;
	
	public SystemEntityPane(MainWindow mainWindow,StarSystemEntity entity) {
		super(mainWindow,entity);
		this.entity = entity;
	}

	/* (non-Javadoc)
	 * @see starcorp.client.gui.ADataPane#createWidgets(java.util.List)
	 */
	@Override
	protected void createWidgets(List<Widget> widgets) {
		super.createWidgets(widgets);
		// TODO actions to move a starship to this location
		Group grp = createGroup(getParent(), widgets, "");
		GridLayout layout = new GridLayout(2,false);
		layout.marginWidth=20;
		layout.marginHeight=10;
		grp.setLayout(layout);
		
		createLabel(grp, widgets, "System:");
		StarSystem system = getTurnReport().getSystem(entity.getSystem());
		if(system == null)
			throw new NullPointerException("Invalid system: " + entity.getSystem());
		createLabel(grp, widgets, system.getDisplayName());
		
		createLabel(grp, widgets, "Location:");
		createLabel(grp, widgets, entity.getLocation().toString());
		
		createLabel(grp,widgets, "Type:");
		if(entity instanceof Planet) {
			createLabel(grp, widgets, "Planet");
		}
		else if(entity.isAsteroid()) {
			createLabel(grp, widgets, "Asteroid");
		}
		else if(entity.isGasfield()) {
			createLabel(grp, widgets, "Gas Field");
		}
		
		if(entity instanceof Planet) {
			Planet planet = (Planet) entity;
			if(planet.getOrbiting() > 0) {
				createLabel(grp, widgets, "Orbiting:");
				Planet orbit = getTurnReport().getPlanet(planet.getOrbiting());
				createPlanetLink(grp, widgets, orbit, null);
			}
			createLabel(grp,widgets,"Atmosphere:");
			createLabel(grp, widgets, planet.getAtmosphereTypeClass().getName());
			
			createLabel(grp,widgets,"Base Hazard Level:");
			createLabel(grp, widgets, format(planet.getAtmosphereTypeClass().getHazardLevel()));
			
			createLabel(grp, widgets, "Gravity:");
			createLabel(grp, widgets, planet.getGravityRating() + "g");
			
			Set<PlanetMapSquare> squares = planet.getMap();
			if(squares == null || squares.size() > 0) {
				createPlanetMapLink(grp, widgets, planet, "View Map");
			}
			
			Set<Colony> colonies = mainWindow.getTurnReport().getColoniesByPlanet(planet.getID());
			if(colonies != null && colonies.size() > 0) {
				Group grpColonies = createGroup(getParent(), widgets, "Colonies");
				grpColonies.setLayout(new GridLayout(3,true));
				for(Colony colony : colonies) {
					createColonyLink(grpColonies, widgets, colony, null);
				}
			}
		}
	}

}
