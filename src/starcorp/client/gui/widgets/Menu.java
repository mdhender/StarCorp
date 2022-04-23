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
package starcorp.client.gui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import starcorp.client.gui.IComponent;
import starcorp.client.gui.windows.MainWindow;

/**
 * starcorp.client.gui.Menu
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class Menu implements IComponent  {
	private final MainWindow mainWindow;
	
	private org.eclipse.swt.widgets.Menu menu;
	private org.eclipse.swt.widgets.Menu fileMenu;
	private org.eclipse.swt.widgets.Menu turnMenu;
	private org.eclipse.swt.widgets.Menu searchMenu;
	private org.eclipse.swt.widgets.Menu helpMenu;
	
	private MenuItem submit;
	private MenuItem save;
	private MenuItem search;
	
	public Menu(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}
	
	public void dispose() {
		menu.dispose();
		fileMenu.dispose();
		turnMenu.dispose();
		searchMenu.dispose();
		helpMenu.dispose();
	}

	public void open(Composite parent) {
		final Shell shell = (Shell) parent; 
		// TODO keyboard accelerators for menu items
		menu = new org.eclipse.swt.widgets.Menu(shell, SWT.BAR);
		menu.setVisible(true);
		shell.setMenuBar(menu);
		fileMenu = new org.eclipse.swt.widgets.Menu(shell, SWT.DROP_DOWN);
		turnMenu = new org.eclipse.swt.widgets.Menu(shell, SWT.DROP_DOWN);
		searchMenu = new org.eclipse.swt.widgets.Menu(shell, SWT.DROP_DOWN);
		helpMenu = new org.eclipse.swt.widgets.Menu(shell, SWT.DROP_DOWN);
		
		MenuItem file = new MenuItem(menu, SWT.CASCADE);
		file.setText("&File");
		file.setMenu(fileMenu);
		
		MenuItem newAccount = new MenuItem(fileMenu, SWT.PUSH);
		newAccount.setText("New &account");
		newAccount.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.createNewAccount();
			}
		});
		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		MenuItem newTurn = new MenuItem(fileMenu, SWT.PUSH);
		newTurn.setText("&New turn");
		newTurn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.setCurrentTurn(null);
				mainWindow.openTurnWindow();
			}
		});
		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		MenuItem load = new MenuItem(fileMenu, SWT.PUSH);
		load.setText("&Load report");
		load.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openLoadReport();
			}
		});
		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		MenuItem loadTurn = new MenuItem(fileMenu, SWT.PUSH);
		loadTurn.setText("Load &turn");
		loadTurn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openLoadTurn();
			}
		});
		
		save = new MenuItem(fileMenu, SWT.PUSH);
		save.setText("&Save turn");
		save.setEnabled(false);
		save.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openSaveTurn();
			}
		});
		
		new MenuItem(fileMenu, SWT.SEPARATOR);
		
		MenuItem exit = new MenuItem(fileMenu, SWT.PUSH);
		exit.setText("E&xit");
		exit.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.close();
			}
		});
		
		MenuItem turn = new MenuItem(menu, SWT.CASCADE);
		turn.setText("&Turn");
		turn.setMenu(turnMenu);
		
		MenuItem newOrder = new MenuItem(turnMenu, SWT.PUSH);
		newOrder.setText("&Prepare");
		newOrder.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openTurnWindow();
			}
		});
		
		submit = new MenuItem(turnMenu, SWT.PUSH);
		submit.setText("&Submit");
		submit.setEnabled(false);
		submit.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.submitTurn();
			}
		});
		
		search = new MenuItem(menu, SWT.CASCADE);
		search.setEnabled(false);
		search.setText("Search");
		search.setMenu(searchMenu);
		
		MenuItem markets = new MenuItem(searchMenu,SWT.PUSH);
		markets.setText("Search markets");
		markets.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openSearchMarketWindow();
			}
		});
		
		MenuItem items = new MenuItem(searchMenu,SWT.PUSH);
		items.setText("Search items");
		items.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openSearchItemsWindow();
			}
		});
		
		MenuItem laws = new MenuItem(searchMenu,SWT.PUSH);
		laws.setText("Search grants and leases");
		laws.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openSearchLawWindow();
			}
		});
		
		MenuItem help = new MenuItem(menu, SWT.CASCADE);
		help.setText("Help");
		help.setMenu(helpMenu);
		
		MenuItem about = new MenuItem(helpMenu,SWT.PUSH);
		about.setText("About");
		about.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.showAboutDialog();
			}
		});
	}
	
	public void setHasReport(boolean yes) {
		search.setEnabled(yes);
	}
	
	public void setEnableSave(boolean yes) {
		save.setEnabled(yes);
	}
	
	public void setEnableSubmit(boolean yes) {
		submit.setEnabled(yes);
	}
	
	public void redraw() {
		// do nothing
		
	}

	public Point computeSize() {
		return null;
	}

}
