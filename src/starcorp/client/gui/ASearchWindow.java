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

import org.eclipse.swt.widgets.Composite;

import starcorp.client.gui.widgets.SearchToolbar;
import starcorp.client.gui.windows.MainWindow;
import starcorp.common.turns.TurnReport;

/**
 * starcorp.client.gui.ASearchWindow
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 5 Oct 2007
 */
public abstract class ASearchWindow extends ADataEntryWindow {

	private final SearchToolbar toolbar;
	private final TurnReport report;
	public static final int ITEMS_PER_PAGE = 20;
	private int page;

	public ASearchWindow(MainWindow mainWindow) {
		super(mainWindow);
		this.report = mainWindow.getTurnReport();
		this.toolbar = new SearchToolbar(this);
	}

	public abstract int countAllItems();

	public abstract int countFilteredItems();

	public int countPages() {
		int total = countFilteredItems();
		int pages = total / ITEMS_PER_PAGE;
		if(total % ITEMS_PER_PAGE > 0)
			pages++;
		return pages;
	}

	protected abstract void filter();

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		int countPages = countPages();
		if(page > countPages) {
			page = countPages;
		}
		else if(page < 1) {
			page = 1;
		}
		if(page == 1) {
			toolbar.setPrevious(false);
		}
		else {
			toolbar.setPrevious(true);
		}
		if(page < countPages) {
			toolbar.setNext(true);
		}
		else {
			toolbar.setNext(false);
		}
		toolbar.setPage(page);	
		this.page = page;
		reload();
	}

	public TurnReport getReport() {
		return report;
	}
	
	public abstract void clear();
	public abstract void search();

	@Override
	public void open(Composite parent) {
		toolbar.open(shell);
		super.open(parent);
		search();
	}

	@Override
	public void redraw() {
		toolbar.redraw();
		super.redraw();
	}

	@Override
	public void reload() {
		toolbar.redraw();
		super.reload();
	}

}