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

import org.eclipse.swt.widgets.Composite;

import starcorp.client.gui.ABuilderPane;
import starcorp.client.gui.ADataEntryWindow;
import starcorp.client.gui.ATablePane;
import starcorp.client.gui.panes.OrderBuilder;
import starcorp.client.gui.panes.TurnOrdersTable;
import starcorp.common.entities.Corporation;
import starcorp.common.turns.Turn;
import starcorp.common.turns.TurnReport;

/**
 * starcorp.client.gui.TurnOrderWindow
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class TurnOrderWindow extends ADataEntryWindow {

	private final Turn turn;
	
	public TurnOrderWindow(MainWindow mainWindow) {
		super(mainWindow);
		if(mainWindow.getCurrentTurn() != null) {
			turn = mainWindow.getCurrentTurn();
		}
		else {
			turn = new Turn();
			mainWindow.setCurrentTurn(turn);
		}
		TurnReport report = mainWindow.getTurnReport();
		if(report != null) {
			Turn oldTurn = report.getTurn();
			if(oldTurn != null) {
				Corporation corp = oldTurn.getCorporation();
				turn.setCorporation(corp);
			}
		}
	}

	@Override
	protected ABuilderPane createBuilder() {
		return new OrderBuilder(this);
	}
	
	@Override
	protected ATablePane createTable() {
		return new TurnOrdersTable(turn, this);
	}
	
	public void clearTurn() {
		mainWindow.setCurrentTurn(null);
		mainWindow.setTurnDirty(false);
		close();
	}

	public void turnEdited() {
		mainWindow.setTurnDirty(true);
	}
	
	public Turn getTurn() {
		return mainWindow.getCurrentTurn();
	}
	
	public Corporation promptCredentials() {
		return mainWindow.promptCredentials();
	}

	@Override
	public void open(Composite parent) {
		super.open(parent);
		shell.setText("StarCorp: Prepare Turn");
		if(turn.getCorporation() == null) {
			Corporation corp = promptCredentials();
			turn.setCorporation(corp);
		}
	}

	@Override
	protected void close() {
		super.close();
		mainWindow.turnWindow = null;
	}


}
