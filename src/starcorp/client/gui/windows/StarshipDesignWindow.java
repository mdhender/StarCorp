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
package starcorp.client.gui.windows;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import starcorp.client.gui.AWindow;
import starcorp.client.gui.panes.DesignBuilder;
import starcorp.client.gui.panes.StarshipDesignPane;
import starcorp.common.entities.StarshipDesign;

/**
 * starcorp.client.gui.StarshipDesignWindow
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class StarshipDesignWindow extends AWindow {

	protected final MainWindow mainWindow;
	private Composite leftPanel;
	private Composite rightPanel;
	
	private StarshipDesignPane designPane;
	private DesignBuilder builderPane;
	private StarshipDesign design;
	
	public StarshipDesignWindow(MainWindow mainWindow) {
		super(mainWindow.getShell().getDisplay());
		this.mainWindow = mainWindow;
		design = new StarshipDesign();
		design.setName("New Design");
		design.addHulls("command-deck");
		design.addHulls("crew-deck");
	}
	
	/* (non-Javadoc)
	 * @see starcorp.client.gui.IComponent#dispose()
	 */
	public void dispose() {
		builderPane.dispose();
		designPane.dispose();
		leftPanel.dispose();
		rightPanel.dispose();
		shell.dispose();
		mainWindow.focus();
	}

	/* (non-Javadoc)
	 * @see starcorp.client.gui.IComponent#open(org.eclipse.swt.widgets.Composite)
	 */
	public void open(Composite parent) {
		shell.setLayout(new RowLayout(SWT.VERTICAL));
		shell.setText("Star Corp: Design Starship");
		leftPanel = new Composite(shell,SWT.NONE);
		leftPanel.setLayout(new GridLayout(1,false));
		rightPanel = new Composite(shell,SWT.NONE);
		rightPanel.setLayout(new GridLayout(1,false));
		builderPane = new DesignBuilder(this);
		builderPane.open(leftPanel);
		designPane = new StarshipDesignPane(mainWindow,design);
		designPane.open(rightPanel);
		
		redraw();
		center();
		shell.open();
	}

	/* (non-Javadoc)
	 * @see starcorp.client.gui.IComponent#pack()
	 */
	public void redraw() {
		builderPane.redraw();
		designPane.redraw();
		
		leftPanel.pack();
		leftPanel.redraw();
		
		rightPanel.pack();
		rightPanel.redraw();
		
		shell.pack();
	}
	
	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public StarshipDesign getDesign() {
		return design;
	}
	
	public void clearHulls() {
		design.clearHulls();
		design.addHulls("command-deck");
		design.addHulls("crew-deck");
		redraw();
	}

	public void addHull(String hullType) {
		int crewDecks = design.countCrewHulls();
		int hulls = (design.countHulls() - (design.countCommandHulls() + crewDecks)) + 1;
		int requiredCrew = hulls / 3;
		if(hulls % 3 > 0) requiredCrew++;
		
		System.out.println("crewDecks = " + crewDecks + " hull " + hulls + " requireCrew " + requiredCrew);
		
		if(requiredCrew > crewDecks) {
			design.addHulls("crew-deck");
		}
		design.addHulls(hullType);
		redraw();
	}
}
