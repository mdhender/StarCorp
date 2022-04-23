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
package starcorp.client.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;

import starcorp.client.gui.windows.MainWindow;

/**
 * starcorp.client.gui.ADataEntryWindow
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public abstract class ADataEntryWindow extends AWindow {

	protected final MainWindow mainWindow;
	
	private Composite topPanel;
	private Composite bottomPanel;
	
	protected ABuilderPane builderPane;
	protected ATablePane tablePane;
	
	public ADataEntryWindow(MainWindow mainWindow) {
		super(mainWindow.display);
		this.mainWindow = mainWindow;
	}
	
	public void dispose() {
		builderPane.dispose();
		tablePane.dispose();
		topPanel.dispose();
		bottomPanel.dispose();
		shell.dispose();
		mainWindow.focus();
	}
	
	public void reload() {
		builderPane.dispose();
		builderPane = createBuilder();
		builderPane.open(getTop());
		tablePane.clear();
		tablePane.populate();
		redraw();
	}
	
	protected Composite getTop() {
		return topPanel;
	}
	
	protected Composite getBottom() {
		return bottomPanel;
	}
	public void open(Composite parent) {
		shell.setLayout(new RowLayout(SWT.VERTICAL));
		shell.setSize(700, 500);
		
		topPanel = new Composite(shell,SWT.NONE);
		topPanel.setLayout(new GridLayout(1,false));
		
		bottomPanel = new Composite(shell,SWT.NONE);
		bottomPanel.setLayout(new GridLayout(1,false));
		
		builderPane = createBuilder();
		tablePane = createTable();
		
		builderPane.open(topPanel);
		tablePane.open(bottomPanel);
		
		redraw();
		center();
		shell.open();
	}
	
	protected abstract ABuilderPane createBuilder();
	protected abstract ATablePane createTable();
	
	public void redraw() {
		builderPane.redraw();
		tablePane.redraw();
		topPanel.pack(true);
		topPanel.redraw();
		bottomPanel.pack(true);
		bottomPanel.redraw();
		shell.pack();
		center();
	}

	@Override
	public MainWindow getMainWindow() {
		return mainWindow;
	}

}
