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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import starcorp.client.gui.IComponent;
import starcorp.client.gui.windows.MainWindow;

/**
 * starcorp.client.gui.Toolbar
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class Toolbar implements IComponent  {

	private final MainWindow mainWindow;
	
	private ToolBar toolbar;
	private List<ToolItem> items = new ArrayList<ToolItem>();
	
	private ToolItem saveTurn;
	private ToolItem submitTurn;
	private ToolItem searchItems;
	private ToolItem searchMarket;
	private ToolItem searchLaws;
	
	private ToolItem backwards;
	private ToolItem forwards;
	private Image[] icons = new Image[11];

	
	public Toolbar(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}
	
	public void dispose() {
		for(Image img : icons) {
			if(img != null && !img.isDisposed())
				img.dispose();
		}
		for(ToolItem item : items) {
			item.dispose();
		}
		toolbar.dispose();
	}
	
	private Image loadIcon(Composite parent, String imageFile) {
		InputStream is = null;
		Image img = null;
		is = getClass().getResourceAsStream(imageFile);
		if(is != null) {
			img = new Image(parent.getDisplay(),is);
			System.out.println("Loaded " + imageFile);
		}
		else {
			System.err.println("Could not load " + imageFile);
		}
		return img;
	}
	
	private void loadIcons(Composite parent) {
		icons[0] = loadIcon(parent,"/images/icons/load-report.gif");
		icons[1] = loadIcon(parent,"/images/icons/new-turn.gif");
		icons[2] = loadIcon(parent,"/images/icons/open-turn.gif");
		icons[3] = loadIcon(parent,"/images/icons/save-turn.gif");
		icons[4] = loadIcon(parent,"/images/icons/submit-turn.gif");
		icons[5] = loadIcon(parent,"/images/icons/search-items.gif");
		icons[6] = loadIcon(parent,"/images/icons/search-market.gif");
		icons[7] = loadIcon(parent,"/images/icons/search-laws.gif");
		icons[8] = loadIcon(parent,"/images/icons/design-ship.gif");
		icons[9] = loadIcon(parent,"/images/icons/left.gif");
		icons[10] = loadIcon(parent,"/images/icons/right.gif");
	}

	public void open(Composite parent) {
		toolbar = new ToolBar(parent,SWT.FLAT | SWT.WRAP);
		
		loadIcons(toolbar);
		
		ToolItem loadTurnReport = new ToolItem(toolbar, SWT.PUSH);
		loadTurnReport.setImage(icons[0]);
		loadTurnReport.setToolTipText("Load Report");
		loadTurnReport.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openLoadReport();
			}
		});
		items.add(loadTurnReport);
		
		items.add(new ToolItem(toolbar, SWT.SEPARATOR));
		
		ToolItem newTurn = new ToolItem(toolbar, SWT.PUSH);
		newTurn.setImage(icons[1]);
		newTurn.setToolTipText("New Turn");
		newTurn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.setCurrentTurn(null);
				mainWindow.openTurnWindow();			
			}
		});
		items.add(newTurn);
		
		ToolItem loadTurn = new ToolItem(toolbar, SWT.PUSH);
		loadTurn.setImage(icons[2]);
		loadTurn.setToolTipText("Load Turn");
		loadTurn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openLoadTurn();	
			}
		});
		items.add(loadTurn);
		
		saveTurn = new ToolItem(toolbar, SWT.PUSH);
		saveTurn.setImage(icons[3]);
		saveTurn.setToolTipText("Save current turn");
		saveTurn.setEnabled(false);
		saveTurn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openSaveTurn();
			}
		});
		items.add(saveTurn);
		
		submitTurn = new ToolItem(toolbar, SWT.PUSH);
		submitTurn.setImage(icons[4]);
		submitTurn.setToolTipText("Submit Turn");
		submitTurn.setEnabled(false);
		submitTurn.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.submitTurn();			}
		});
		items.add(submitTurn);
		
		items.add(new ToolItem(toolbar, SWT.SEPARATOR));
		
		searchItems = new ToolItem(toolbar, SWT.PUSH);
		searchItems.setToolTipText("Search Items");
		searchItems.setImage(icons[5]);
		searchItems.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openSearchItemsWindow();
			}
		});
		items.add(searchItems);
		
		searchMarket= new ToolItem(toolbar, SWT.PUSH);
		searchMarket.setImage(icons[6]);
		searchMarket.setToolTipText("Search Market");
		searchMarket.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openSearchMarketWindow();
			}
		});
		items.add(searchMarket);
		
		searchLaws = new ToolItem(toolbar, SWT.PUSH);
		searchLaws.setImage(icons[7]);
		searchLaws.setToolTipText("Search Laws");
		searchLaws.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openSearchLawWindow();
			}
		});
		items.add(searchLaws);
		
		searchItems.setEnabled(false);
		searchMarket.setEnabled(false);
		searchLaws.setEnabled(false);
		
		items.add(new ToolItem(toolbar, SWT.SEPARATOR));
		
		ToolItem designShip = new ToolItem(toolbar, SWT.PUSH);
		designShip.setImage(icons[8]);
		designShip.setToolTipText("Design Ship");
		designShip.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.openStarshipDesignWindow();
			}
		});
		items.add(designShip);
		
		items.add(new ToolItem(toolbar, SWT.SEPARATOR));
		
		backwards = new ToolItem(toolbar, SWT.PUSH);
		backwards.setImage(icons[9]);
		backwards.setEnabled(false);
		backwards.setToolTipText("Back to previous data");
		backwards.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.back();
			}
		});
		items.add(backwards);
		
		forwards = new ToolItem(toolbar, SWT.PUSH);
		forwards.setImage(icons[10]);
		forwards.setEnabled(false);
		forwards.setToolTipText("Forward to next data");
		forwards.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.forward();
			}
		});
		items.add(forwards);
}
	
	public void setBackwards(boolean yes) {
		backwards.setEnabled(yes);
	}
	
	public void setForwards(boolean yes) {
		forwards.setEnabled(yes);
	}
	
	public void setHasReport(boolean yes) {
		searchItems.setEnabled(yes);
		searchMarket.setEnabled(yes);
		searchLaws.setEnabled(yes);
	}
	public void setEnableSave(boolean yes) {
		saveTurn.setEnabled(yes);
	}
	
	public void setEnableSubmit(boolean yes) {
		submitTurn.setEnabled(yes);
	}

	public void redraw() {
		toolbar.pack();
	}

	public Point computeSize() {
		return toolbar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	}

}
