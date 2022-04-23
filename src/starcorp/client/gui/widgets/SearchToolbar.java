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
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import starcorp.client.gui.ASearchWindow;
import starcorp.client.gui.IComponent;
import starcorp.client.gui.windows.MainWindow;

/**
 * starcorp.client.gui.widgets.SearchToolbar
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 5 Oct 2007
 */
public class SearchToolbar implements IComponent {

	private ASearchWindow mainWindow;
	private ToolBar toolbar;
	private List<ToolItem> items = new ArrayList<ToolItem>();

	private ToolItem clear;
	private ToolItem search;
	private Text txtPage;
	private ToolItem pageNumber;
	private ToolItem first;
	private ToolItem last;
	private ToolItem previous;
	private ToolItem next;
	private Image[] icons = new Image[6];

	public SearchToolbar(ASearchWindow mainWindow) {
		this.mainWindow = mainWindow;
	}
	
	public void dispose() {
		txtPage.dispose();
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
		icons[0] = loadIcon(parent,"/images/icons/first.gif");
		icons[1] = loadIcon(parent,"/images/icons/previous.gif");
		icons[2] = loadIcon(parent,"/images/icons/next.gif");
		icons[3] = loadIcon(parent,"/images/icons/last.gif");
		icons[4] = loadIcon(parent,"/images/icons/trash.gif");
		icons[5] = loadIcon(parent,"/images/icons/search.gif");
	}

	public void setNext(boolean enabled) {
		next.setEnabled(enabled);
		last.setEnabled(enabled);
	}
	
	public void setPrevious(boolean enabled) {
		previous.setEnabled(enabled);
		first.setEnabled(enabled);
	}

	public void open(Composite parent) {
		toolbar = new ToolBar(parent,SWT.FLAT | SWT.WRAP);
		loadIcons(toolbar);
		
		first = new ToolItem(toolbar,SWT.PUSH);
		first.setToolTipText("First Page");
		first.setImage(icons[0]);
		first.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.setPage(1);
			}
		});
		items.add(first);
		
		previous = new ToolItem(toolbar, SWT.PUSH);
		previous.setToolTipText("Previous Page");
		previous.setImage(icons[1]);
		previous.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.setPage(mainWindow.getPage() - 1);
			}
		});
		items.add(previous);
		
		items.add(new ToolItem(toolbar, SWT.SEPARATOR));
		
		pageNumber = new ToolItem(toolbar,SWT.SEPARATOR);
		txtPage = new Text(toolbar,SWT.BORDER);
		txtPage.setEditable(false);
		GC gc = new GC(txtPage);
		FontMetrics fm = gc.getFontMetrics();
		int width = 10 * fm.getAverageCharWidth();
		int height = fm.getHeight();
		gc.dispose();
		txtPage.setSize(width, height);
		pageNumber.setControl(txtPage);
		pageNumber.setWidth(width);
		items.add(pageNumber);
		
		items.add(new ToolItem(toolbar, SWT.SEPARATOR));
		
		next = new ToolItem(toolbar, SWT.PUSH);
		next.setImage(icons[2]);
		next.setToolTipText("Next Page");
		next.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.setPage(mainWindow.getPage() + 1);
			}
		});
		items.add(next);
		
		last = new ToolItem(toolbar, SWT.PUSH);
		last.setImage(icons[3]);
		last.setToolTipText("Last Page");
		last.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.setPage(mainWindow.countPages());
			}
		});
		items.add(last);
		
		items.add(new ToolItem(toolbar, SWT.SEPARATOR));
		
		clear = new ToolItem(toolbar, SWT.PUSH);
		clear.setImage(icons[4]);
		clear.setToolTipText("Clear");
		clear.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.clear();
			}
		});
		items.add(clear);
		
		search = new ToolItem(toolbar, SWT.PUSH);
		search.setImage(icons[5]);
		search.setToolTipText("Search");
		search.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				mainWindow.search();
			}
		});
		items.add(search);
		
		if(mainWindow.countPages() > 1) 
			setNext(true); 
		else
			setNext(false);
		setPrevious(false);
		setPage(1);
	}
	
	public void setPage(int page) {
		System.out.println("SearchToolbar: setPage " + page);
		txtPage.setText(String.valueOf(page) + " of " + mainWindow.countPages());
		redraw();
	}

	public void redraw() {
		toolbar.pack();
		toolbar.redraw();
	}

	public Point computeSize() {
		return toolbar.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	}
}
