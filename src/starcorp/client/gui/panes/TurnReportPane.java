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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.ADataPane;
import starcorp.client.gui.windows.MainWindow;
import starcorp.common.turns.Turn;
import starcorp.common.turns.TurnError;
import starcorp.common.turns.TurnReport;
import starcorp.common.types.GalacticDate;

/**
 * starcorp.client.gui.TurnReportPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class TurnReportPane extends ADataPane {

	private Group reportGroup;
	private TurnReport report;
	
	public TurnReportPane(MainWindow mainWindow, TurnReport report) {
		super(mainWindow);
		this.report = report;
	}
	
	/* (non-Javadoc)
	 * @see starcorp.client.gui.ADataPane#createWidgets(java.util.List)
	 */
	@Override
	protected void createWidgets(List<Widget> widgets) {
		reportGroup = createGroup(getParent(), widgets, "Turn Report Summary");
		reportGroup.setLayout(new GridLayout(2,true));
		
		Turn turn = report.getTurn();
		if(turn == null) {
			createLabel(reportGroup, widgets, "No turn data.");
		}
		else {
			GalacticDate turnDate = turn.getProcessedDate();
			int orderCount = turn.getOrders().size();
			int errorCount = turn.getErrors().size();
			
			createLabel(reportGroup, widgets, "Galactic Date:");
			if(turnDate != null) {
				createLabel(reportGroup, widgets, turn.getProcessedDate().toString());
			}
			else {
				createLabel(reportGroup, widgets, "N/A");
			}
			
			createLabel(reportGroup, widgets, "Orders:");
			createLabel(reportGroup, widgets, format(orderCount));
			createLabel(reportGroup, widgets, "Errors:");
			createLabel(reportGroup, widgets, format(errorCount));

			if(errorCount > 0) {
				Group grpErrors = createGroup(reportGroup, widgets, "Errors");
				grpErrors.setLayout(new RowLayout(SWT.VERTICAL));
				
				for(TurnError error :  turn.getErrors()) {
					String msg = error.getMessage();
					createLabel(grpErrors, widgets, msg);
				}
			}
		}
	}

}
