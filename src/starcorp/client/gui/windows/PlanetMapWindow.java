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
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import starcorp.client.gui.AWindow;
import starcorp.client.gui.widgets.PlanetMap;
import starcorp.common.entities.Planet;

/**
 * starcorp.client.gui.windows.PlanetMapWindow
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 27 Sep 2007
 */
public class PlanetMapWindow extends AWindow {
	private final Planet planet;
	private final MainWindow mainWindow;
	private final PlanetMap map;
	
	public PlanetMapWindow(MainWindow mainWindow, Planet planet) {
		super(mainWindow.getShell().getDisplay(),SWT.DIALOG_TRIM);
		this.planet = planet;
		this.mainWindow = mainWindow;
		this.map = new PlanetMap(planet.getMap());
	}

	@Override
	public MainWindow getMainWindow() {
		return mainWindow;
	}

	public void dispose() {
		map.dispose();
		shell.dispose();
		mainWindow.focus();
	}

	public void open(Composite parent) {
		shell.setLayout(new RowLayout(SWT.VERTICAL));
		shell.setText(planet.getDisplayName());
		map.open(shell);
		
		redraw();
		shell.open();
	}

	public void redraw() {
		map.redraw();
		shell.pack();
		center();
	}

	@Override
	protected void close() {
		super.close();
		mainWindow.mapWindow = null;
	}
}
