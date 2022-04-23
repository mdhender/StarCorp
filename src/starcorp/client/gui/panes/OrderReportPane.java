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
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.ADataPane;
import starcorp.client.gui.windows.MainWindow;
import starcorp.common.entities.AColonists;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Facility;
import starcorp.common.entities.IEntity;
import starcorp.common.entities.Planet;
import starcorp.common.entities.StarSystem;
import starcorp.common.entities.StarSystemEntity;
import starcorp.common.entities.Starship;
import starcorp.common.entities.StarshipDesign;
import starcorp.common.turns.OrderReport;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnOrder;
import starcorp.common.types.PlanetMapSquare;
import starcorp.common.types.TerrainType;

/**
 * starcorp.client.gui.OrderReportPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class OrderReportPane extends ADataPane {
	private final TurnOrder order;
	private final OrderReport report;
	private final TurnError error;
	
	public OrderReportPane(MainWindow mainWindow, TurnOrder order) {
		super(mainWindow);
		this.order = order;
		this.report = order.getReport();
		this.error = order.getError();
	}

	@Override
	protected void createWidgets(List<Widget> widgets) {
		Group grpSummary = createGroup(getParent(), widgets, order.toString());
		grpSummary.setLayout(new GridLayout(2,false));
		String txt = null;
		if(report != null) {
			if(report.getDescription() != null) {
				txt = report.getDescription();
			}
		}
		else if(error != null) {
			txt = error.getMessage();
		}
		
		if(txt == null) {
			txt = "No report for this order.";
		}
		createLabel(grpSummary, widgets, txt).setLayoutData(new GridData(SWT.CENTER,SWT.TOP,true,true,2,1));
		
		if(report != null) {
			IEntity subject = report.getSubject();
			if(subject != null) {
				createLabel(grpSummary, widgets, "Subject:");
				displayEntity(grpSummary, widgets, subject);
			}
			
			IEntity target = report.getTarget();
			if(target != null) {
				createLabel(grpSummary, widgets, "Target:");
				displayEntity(grpSummary, widgets, target);
			}
			
			PlanetMapSquare sq = report.getScannedLocation();
			
			if(sq != null) {
				Group grp = createGroup(getParent(), widgets, "Prospecting Report");
				grp.setLayout(new GridLayout(2,true));
				createLabel(grp, widgets, "X: " + sq.getX());
				createLabel(grp, widgets, "Y: " + sq.getY());
				TerrainType terrain = sq.getTerrainType();
				createLabel(grp, widgets, "Terrain:");
				createLabel(grp,widgets,terrain.getName());
				createLabel(grp, widgets, "Hazard Level:");
				createLabel(grp,widgets,String.valueOf(terrain.getHazardLevel()));
			}
			
			Set<?> scanned = report.getScannedEntities(); 
			
			if(scanned.size() > 0) {
				Group grp = createGroup(getParent(), widgets, "Scanned");
				grp.setLayoutData(new GridData(SWT.LEFT,SWT.TOP,true,true,2,1));
				grp.setLayout(new GridLayout(4,true));
				for(Object o : scanned) {
					displayEntity(grp, widgets, o);
				}
			}
		}
	}
	
	private void displayEntity(Group grp, List<Widget> widgets, Object o) {
		if(o instanceof Colony) {
			Colony colony = (Colony) o;
			createColonyLink(grp, widgets, colony, null);
		}
		else if(o instanceof AColonists) {
			AColonists colonist = (AColonists) o;
			createColonistLink(grp, widgets, colonist, null);
		}
		else if(o instanceof Corporation) {
			Corporation corp = (Corporation) o;
			createCorporationLink(grp, widgets, corp, null);
		}
		else if(o instanceof Facility) {
			Facility facility = (Facility)o;
			createFacilityLink(grp, widgets, facility, null);
		}
		else if(o instanceof StarshipDesign) {
			StarshipDesign design = (StarshipDesign)o;
			createDesignLink(grp, widgets, design, null);
		}
		else if(o instanceof Starship) {
			Starship ship = (Starship) o;
			createShipLink(grp, widgets, ship, null);
		}
		else if(o instanceof Planet) {
			Planet planet = (Planet) o;
			createPlanetLink(grp, widgets, planet, null);
		}
		else if(o instanceof StarSystemEntity) {
			StarSystemEntity entity = (StarSystemEntity)o;
			createSystemEntityLink(grp, widgets, entity, null);
		}
		else if(o instanceof StarSystem) {
			StarSystem system = (StarSystem) o;
			createLabel(grp, widgets, system.getDisplayName() + " @ " + system.getLocation());
		}
		else if(o instanceof IEntity) {
			IEntity entity = (IEntity) o;
			createLabel(grp, widgets, entity.getDisplayName());
		}
		else {
			createLabel(grp, widgets, o.toString());
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((order == null) ? 0 : order.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final OrderReportPane other = (OrderReportPane) obj;
		if (order == null) {
			if (other.order != null)
				return false;
		} else if (!order.equals(other.order))
			return false;
		return true;
	}
}
