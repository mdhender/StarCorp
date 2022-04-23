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
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.AEntityPane;
import starcorp.client.gui.windows.MainWindow;
import starcorp.common.entities.StarshipDesign;
import starcorp.common.types.Items;

/**
 * starcorp.client.gui.StarshipDesignPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class StarshipDesignPane extends AEntityPane {

	private StarshipDesign design;
	
	private Label lblHullCount;
	private Label lblMass;
	private Label lblImpulse;
	private Label lblJump;
	private Label lblOrbit;
	private Label lblDock;
	private Label lblShortScan;
	private Label lblLongScan;
	
	private Label lblCargoConsumer;
	private Label lblCargoIndustrial;
	private Label lblCargoModules;
	private Label lblCargoOrganics;
	private Label lblCargoLiquids;
	
	private Label lblSpecialAbilities;
	
	private Label lblHulls;
	
	public StarshipDesignPane(MainWindow mainWindow, StarshipDesign design) {
		super(mainWindow, design);
		this.design = design;
	}

	@Override
	protected void createWidgets(List<Widget> widgets) {
		super.createWidgets(widgets);
		
		Group grpData =  createGroup(getParent(), widgets, "Data");
		grpData.setLayout(new GridLayout(2,true));
		
		lblHullCount = createLabel(grpData, widgets, "");
		lblMass = createLabel(grpData, widgets, "");
		lblImpulse = createLabel(grpData, widgets, "");
		lblJump = createLabel(grpData, widgets, "");
		lblOrbit = createLabel(grpData, widgets, "");
		lblDock = createLabel(grpData, widgets, "");
		lblShortScan = createLabel(grpData, widgets, "");
		lblLongScan = createLabel(grpData, widgets, "");

		Group grpCargo = createGroup(grpData, widgets, "Cargo Capacity");
		grpCargo.setLayout(new RowLayout(SWT.VERTICAL));
		GridData data = new GridData();
		data.minimumWidth=250;
		data.verticalAlignment=SWT.TOP;
		grpCargo.setLayoutData(data);
		
		lblCargoConsumer = createLabel(grpCargo, widgets, "");
		lblCargoIndustrial = createLabel(grpCargo, widgets, "");
		lblCargoModules = createLabel(grpCargo, widgets, "");
		lblCargoOrganics = createLabel(grpCargo, widgets, "");
		lblCargoLiquids = createLabel(grpCargo, widgets, "");

		Group grpAbilities = createGroup(grpData,widgets,"Special Abilities");
		grpAbilities.setLayout(new FillLayout(SWT.VERTICAL));
		data = new GridData();
		data.minimumWidth=250;
		data.verticalAlignment=SWT.TOP;
		grpAbilities.setLayoutData(data);
		lblSpecialAbilities = createLabel(grpAbilities, widgets, "");

		Group grpHulls = createGroup(getParent(),widgets,"Hulls");
		grpHulls.setLayout(new GridLayout(3,true));
		data = new GridData();
		data.minimumWidth=500;
		data.verticalAlignment=SWT.TOP;
		data.horizontalSpan=2;
		grpHulls.setLayoutData(data);
		lblHulls = createLabel(grpHulls, widgets, "");
		
		// TODO create build drop down filtered by colonies where sufficient hulls are available
}

	@Override
	public void redraw() {
		lblHullCount.setText("Hulls: " +design.countHulls());
		lblMass.setText("Mass: " +design.getTotalMass() + "mu");
		lblImpulse.setText("Impulse Speed: " +design.getImpulseSpeed());
		lblJump.setText("Jump Range: " +design.getJumpRange());
		lblOrbit.setText("Orbit: " +format(design.getMaxOrbitGravity()) + "g");
		lblDock.setText("Dock: " +format(design.getMaxDockGravity()) + "g");
		lblShortScan.setText("Short Range Scanners: " +(design.canScanStarSystem() ? "yes" : "no"));
		lblLongScan.setText("Long Range Scanners: " +design.getScanGalaxyRange());
		
		lblCargoConsumer.setText("Consumer: " +design.getConsumerCapacity() + "mu");
		lblCargoIndustrial.setText("Industrial: " +design.getIndustrialCapacity() + "mu");
		lblCargoModules.setText("Modules: " +design.getModulesCapacity() + "mu");
		lblCargoOrganics.setText("Organics: " +design.getOrganicsCapacity() + "mu");
		lblCargoLiquids.setText("Liquids / Gas: " +design.getLiquidGasCapacity() + "mu");

		String ability = "";
		boolean hasAbility = false;
		if(design.canMineAsteroid()) {
			ability += "Mine Asteroids\n";
			hasAbility = true;
		}
		
		if(design.canMineGasField()) {
			ability += "Mine Gas Fields\n";
			hasAbility = true;
		}
		
		if(design.canProbeField()) {
			ability += "Probe Asteroids / Gas Fields\n";
			hasAbility = true;
		}
		
		if(design.canProbePlanet()) {
			ability += "Probe Planets\n";
			hasAbility = true;
		}
		
		if(design.hasBioLab()) {
			ability += "Prospect (Organics)\n";
			hasAbility = true;
		}
		
		if(design.hasPhysicsLab()) {
			ability += "Prospect (Mineral / Metal / Fissile)\n";
			hasAbility = true;
		}
		
		if(design.hasGeoLab()) {
			ability += "Prospect (Fuel / Gas / Metal / Liquid)\n";
			hasAbility = true;
		}
		
		if(!hasAbility) {
			ability = "None";
		}
		
		lblSpecialAbilities.setText(ability);
		
		String strHulls = "";
		Set<Items> hulls = design.getHulls();
		if(hulls != null && hulls.size() > 0) {
			for(Items item : hulls) {
				strHulls += item + "\n";
			}
		}
		else {
			strHulls = "None";
		}
		
		lblHulls.setText(strHulls);
		super.redraw();
	}
}
