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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.mail.MessagingException;

import org.dom4j.DocumentException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import starcorp.client.ClientConfiguration;
import starcorp.client.gui.ADataPane;
import starcorp.client.gui.AWindow;
import starcorp.client.gui.panes.CorporationPane;
import starcorp.client.gui.panes.SimpleTextPane;
import starcorp.client.gui.widgets.ConfigurationDialog;
import starcorp.client.gui.widgets.CredentialsDialog;
import starcorp.client.gui.widgets.Menu;
import starcorp.client.gui.widgets.SendEmailDialog;
import starcorp.client.gui.widgets.Toolbar;
import starcorp.client.gui.widgets.TreeBrowser;
import starcorp.common.entities.Corporation;
import starcorp.common.entities.Planet;
import starcorp.common.turns.Turn;
import starcorp.common.turns.TurnOrder;
import starcorp.common.turns.TurnReport;
import starcorp.common.types.GalacticDate;
import starcorp.common.util.SendEmail;
import starcorp.common.util.ZipTools;

/**
 * starcorp.client.gui.MainWindow
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class MainWindow extends AWindow {

	private static final String[] REPORT_FILTER_EXTS = { "*.zip","*.xml", "*.*"};
	private static final String[] REPORT_FILTER_NAMES = {
	      "Turn Reports (*.zip)",
		  "Turn Reports (*.xml)",
	      "All Files (*.*)"};
	private static final String[] TURN_FILTER_EXTS = { "*.xml", "*.*"};
	private static final String[] TURN_FILTER_NAMES = {
	      "Turn Orders (*.xml)",
	      "All Files (*.*)"};

	private ADataPane currentDataPane;
	private Turn currentTurn;
	private Composite dataPanel;
	private List<ADataPane> history = new ArrayList<ADataPane>();
	
	private int historyIndex = -1;
	private Menu menu;
	private Composite panel;
	private Toolbar toolbar;
	private TreeBrowser treeBrowser;
	private boolean turnDirty;
	
	private TurnReport turnReport;
	StarshipDesignWindow designWindow;
	
	PlanetMapWindow mapWindow;
	SearchItemsWindow searchItemsWindow;
	
	SearchLawsWindow searchLawsWindow;
	
	SearchMarketWindow searchMarketWindow;
	
	TurnOrderWindow turnWindow;
	
	public MainWindow() {
		super(new Display());
	}
	
	private boolean extractZippedReport(String zipFile) {
		String dir = zipFile.substring(0, zipFile.lastIndexOf("."));
		File reports = new File(dir);
		reports.mkdirs();
		try {
			ZipTools.unzip(zipFile, dir);
			for(File f : reports.listFiles()) {
				String name = f.getAbsolutePath();
				if(loadReport(name))
					return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean loadReport(String fileName) {
		if(fileName != null && fileName.endsWith(".zip")) {
			return extractZippedReport(fileName);
		}
		setCurrentTurn(null);
		closeChildWindows();
		if(fileName != null) {
			try {
				turnReport = new TurnReport(new FileInputStream(fileName));
			} catch (FileNotFoundException e) {
				int buttonID = messageBox("File Not Found", "Could not find " + fileName, SWT.ICON_ERROR | SWT.ABORT | SWT.RETRY);
				if(SWT.RETRY == buttonID) {
					loadReport(fileName);
				}
			} catch (Throwable e) {
				e.printStackTrace();
				messageBox("Invalid Report", fileName + " is not a valid StarCorp turn report", SWT.ICON_ERROR | SWT.OK);
			}
		}
		if(turnReport != null) {
			treeBrowser.setReport(turnReport);
			menu.setHasReport(true);
			toolbar.setHasReport(true);
			ADataPane corpPane =new CorporationPane(this, turnReport.getTurn().getCorporation());
			set(corpPane);
			clearHistory();
			Turn turn = turnReport.getTurn();
			Corporation corp = turn == null ? null : turn.getCorporation();
			GalacticDate date = turn == null ? null : turn.getProcessedDate();
			StringBuffer sb = new StringBuffer("StarCorp");
			if(corp != null) {
				sb.append(": ");
				sb.append(corp.getDisplayName());
			}
			if(date != null) {
				sb.append(": ");
				sb.append(date.toString());
			}
			shell.setText(sb.toString());
			redraw();
			return true;
		}
		return false;
	}

	private void loadTurn(String fileName) {
		setCurrentTurn(null);
		if(fileName != null) {
			try {
				currentTurn = new Turn(new FileInputStream(fileName));
			} catch (FileNotFoundException e) {
				int buttonID = messageBox("Error Loading Turn", fileName + " not found.", SWT.ICON_ERROR | SWT.RETRY | SWT.ABORT);
				if(buttonID == SWT.RETRY) {
					loadTurn(fileName);
				}
			} catch (DocumentException e) {
				e.printStackTrace();
				int buttonID = messageBox("Error Loading Turn", fileName + " not valid turn format.", SWT.ICON_ERROR | SWT.RETRY | SWT.ABORT);
				if(buttonID == SWT.RETRY) {
					loadTurn(fileName);
				}
			}
			setTurnDirty(false);
		}
	}
	
	private void saveTurn(String fileName) {
		if(fileName != null) {
			try {
				currentTurn.write(new FileWriter(fileName));
				setTurnDirty(false);
			} catch (IOException e) {
				int buttonID = messageBox("File Save Error", "Could not save to " + fileName + " because " + e.getMessage(), SWT.ICON_ERROR | SWT.ABORT | SWT.RETRY);
				if(SWT.RETRY == buttonID) {
					saveTurn(fileName);
				}
			}
		}
	}
	
	private void toggleToolbarDirections() {
		if(historyIndex > 0) {
			toolbar.setBackwards(true);
		}
		else {
			toolbar.setBackwards(false);
		}
		if(historyIndex + 1 < history.size()) {
			toolbar.setForwards(true);
		}
		else {
			toolbar.setForwards(false);
		}
	}

	public void addTurnOrder(TurnOrder order) {
		openTurnWindow();
		currentTurn.add(order);
		setTurnDirty(true);
		turnWindow.reload();
	}
	
	public void addTurnOrders(List<TurnOrder> orders) {
		openTurnWindow();
		for(TurnOrder order : orders) {
			currentTurn.add(order);
		}
		turnWindow.reload();
	}

	public void back() {
//		System.out.println("back: " + historyIndex + " (" + history.size() + ")");
		if(historyIndex > 0) {
			ADataPane dataPane = history.get(historyIndex - 1);
			if(dataPane.equals(this.currentDataPane))
				return;
			if(this.currentDataPane != null) {
				this.currentDataPane.dispose();
			}
			dataPane.open(dataPanel);
			this.currentDataPane = dataPane;
			historyIndex--;
			redraw();
		}
		toggleToolbarDirections();
	}
	public void clearHistory() {
		historyIndex = -1;
		history.clear();
		toolbar.setBackwards(false);
		toolbar.setForwards(false);
	}
	
	public void close() {
		if(isTurnDirty()) {
			int buttonID = messageBox("Save Current Turn", "Do you wish to save the current turn first?", SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			if(buttonID == SWT.CANCEL) {
				return;
			}
			else if(buttonID == SWT.YES) {
				openSaveTurn();
			}
		}
		closeChildWindows();
		super.close();
		System.exit(0);
	}
	
	public void closeChildWindows() {
		if(mapWindow != null) {
			mapWindow.dispose();
			mapWindow = null;
		}
		if(turnWindow != null) {
			turnWindow.dispose();
			turnWindow = null;
		}
		if(searchMarketWindow != null) {
			searchMarketWindow.dispose();
			searchMarketWindow = null;
		}
		if(searchItemsWindow != null) {
			searchItemsWindow.dispose();
			searchItemsWindow = null;
		}
		if(searchLawsWindow != null) {
			searchLawsWindow.dispose();
			searchLawsWindow = null;
		}
		if(designWindow != null) {
			designWindow.dispose();
			designWindow = null;
		}
	}
	
	public Point computeSize() {
		int width;
		int height;
		
		Point pTree = treeBrowser.computeSize();
		Point pData = currentDataPane == null ? new Point(0,0) : currentDataPane.computeSize();
		Point pToolbar = toolbar.computeSize();
		
		width = pTree.x + pData.x + 50;
		height = pToolbar.y + pTree.y + pData.y + 50;
		
		if(pToolbar.x > width) width = pToolbar.x;
		
		return new Point(width,height);
	}
	
	public void createNewAccount() {
		Corporation corp = promptCredentials();
		if(corp != null) {
			Turn turn = new Turn();
			turn.setCorporation(corp);
			setCurrentTurn(turn);
			submitTurn();
		}
	}

	public void dispose() {
		menu.dispose();
		toolbar.dispose();
		treeBrowser.dispose();
		currentDataPane.dispose();
		dataPanel.dispose();
		panel.dispose();
		if(searchMarketWindow != null)
			searchMarketWindow.dispose();
		if(searchItemsWindow != null)
			searchItemsWindow.dispose();
		if(searchLawsWindow != null)
			searchLawsWindow.dispose();
		if(designWindow != null)
			designWindow.dispose();
		display.dispose();
	}
	
	public void forward() {
//		System.out.println("forward: " + historyIndex + " (" + history.size() + ")");
		if(historyIndex + 1 < history.size()) {
			ADataPane dataPane = history.get(historyIndex + 1);
			if(dataPane.equals(this.currentDataPane))
				return;
			if(this.currentDataPane != null) {
				this.currentDataPane.dispose();
			}
			dataPane.open(dataPanel);
			this.currentDataPane = dataPane;
			historyIndex++;
			redraw();
		}
		toggleToolbarDirections();
	}
	
	public Corporation getCorporation() {
		if(currentTurn != null && currentTurn.getCorporation() != null) {
			return currentTurn.getCorporation();
		}
		else if(turnReport != null && turnReport.getTurn() != null) {
			return turnReport.getTurn().getCorporation();
		}
		return null;
	}

	public Turn getCurrentTurn() {
		return currentTurn;
	}
	
	public ADataPane getDataPane() {
		return currentDataPane;
	}
	
	@Override
	public MainWindow getMainWindow() {
		return this;
	}
	
	public TurnReport getTurnReport() {
		return turnReport;
	}
	
	public boolean isTurnDirty() {
		return this.currentTurn != null && turnDirty;
	}
	
	public void open(Composite parent) {
		shell.setLayout(new GridLayout(1,true));
		shell.setText("StarCorp");
		menu = new Menu(this);
		menu.open(shell);
		
		toolbar = new Toolbar(this);
		toolbar.open(shell);
		
		panel = new Composite(shell,SWT.BORDER);
		panel.setLayout(new GridLayout(2,false));

		treeBrowser = new TreeBrowser(this);
		treeBrowser.open(panel);
		
		dataPanel = new Composite(panel, SWT.NONE);
		dataPanel.setLayout(new GridLayout(1,true));
		GridData data = new GridData();
		data.verticalAlignment = SWT.TOP;
		data.horizontalAlignment=SWT.LEFT;
		data.minimumWidth=400;
		data.minimumHeight=500;
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		dataPanel.setLayoutData(data);
		
		currentDataPane = new SimpleTextPane(this,"Welcome","Load a turn report or create a new account.");
		currentDataPane.open(dataPanel);
		
		data = new GridData();
		data.verticalAlignment=SWT.TOP;
		data.horizontalAlignment=SWT.LEFT;
		data.minimumWidth=500;
		data.minimumHeight=500;
		data.grabExcessHorizontalSpace=true;
		data.grabExcessVerticalSpace=true;
		panel.setLayoutData(data);

		redraw();
		center();
		shell.open();
	}

	public void openLoadReport() {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterNames(REPORT_FILTER_NAMES);
		dialog.setFilterExtensions(REPORT_FILTER_EXTS);
		String fileName = dialog.open();
		loadReport(fileName);
	}
	
	public void openLoadTurn() {
		FileDialog dialog = new FileDialog(shell, SWT.OPEN);
		dialog.setFilterNames(TURN_FILTER_NAMES);
		dialog.setFilterExtensions(TURN_FILTER_EXTS);
		String fileName = dialog.open();
		loadTurn(fileName);
		if(currentTurn != null) {
			openTurnWindow();
		}
	}
	
	public PlanetMapWindow openPlanetMap(Planet planet) {
//		System.out.println("Open map " + planet);
		if(mapWindow != null) {
			mapWindow.dispose();
		}
		mapWindow = new PlanetMapWindow(this,planet);
		mapWindow.open(shell);
		return mapWindow;
	}
	
	public void openSaveTurn() {
		FileDialog dialog = new FileDialog(shell, SWT.SAVE);
		Corporation corp = currentTurn.getCorporation();
		GalacticDate date = turnReport == null ? null : turnReport.getTurn().getProcessedDate();
		String file = "turn-" + corp.getPlayerEmail() + "-" + (date == null ? "new" : date);
		dialog.setFileName( file);
		dialog.setFilterNames(TURN_FILTER_NAMES);
		dialog.setFilterExtensions(TURN_FILTER_EXTS);
		String fileName = dialog.open();
		saveTurn(fileName);
	}
	
	public SearchItemsWindow openSearchItemsWindow() {
		if(searchItemsWindow != null) {
			searchItemsWindow.focus();
		}
		else {
			searchItemsWindow = new SearchItemsWindow(this);
			searchItemsWindow.open(shell);
		}
		return searchItemsWindow;
	}

	public SearchLawsWindow openSearchLawWindow() {
		if(searchLawsWindow != null) {
			searchLawsWindow.focus();
		}
		else {
			searchLawsWindow = new SearchLawsWindow(this);
			searchLawsWindow.open(shell);
		}
		return searchLawsWindow;
	}

	public SearchMarketWindow openSearchMarketWindow() {
		if(searchMarketWindow != null) {
			searchMarketWindow.focus();
		}
		else {
			searchMarketWindow = new SearchMarketWindow(this);
			searchMarketWindow.open(shell);
		}
		return searchMarketWindow;
	}
	
	public StarshipDesignWindow openStarshipDesignWindow() {
		if(designWindow != null) {
			designWindow.focus();
		}
		else {
			designWindow = new StarshipDesignWindow(this);
			designWindow.open(shell);
		}
		return designWindow;
	}

	public TurnOrderWindow openTurnWindow() {
		if(turnWindow == null) {
			turnWindow = new TurnOrderWindow(this);
			turnWindow.open(turnWindow.getShell());
			turnWindow.focus();
		}
		else {
			turnWindow.focus();
		}
		return turnWindow;
	}
	
	public void promptSendEmail(Corporation to) {
		Corporation from = currentTurn == null ? null : currentTurn.getCorporation() == null ? null : currentTurn.getCorporation();
		if(from == null && turnReport != null) {
			from = turnReport.getTurn().getCorporation();
		}
		if(from == null) {
			messageBox("Send Email Error", "Cannot send emails without authenticating your corporation", SWT.ICON_ERROR | SWT.OK);
		}
		else {
			SendEmailDialog dialog = new SendEmailDialog(shell,from,to);
			dialog.open();
		}
	}
	
	public void promptConfiguration() {
		ConfigurationDialog dialog = new ConfigurationDialog(shell);
		dialog.open();
	}

	public Corporation promptCredentials() {
		Corporation existing = getCorporation();
		CredentialsDialog dialog = new CredentialsDialog(shell,existing);
		
		return dialog.open();
	}
	
	public void redraw() {
//		System.out.println("MainWindow pack");
		
		menu.redraw();
		toolbar.redraw();
		
		treeBrowser.redraw();
		currentDataPane.redraw();
		
		dataPanel.pack();
		dataPanel.redraw();
		panel.pack();
		panel.redraw();
		
		super.redraw();
	}
	
	public void run() {
		open(shell);
		while (!shell.isDisposed()) {
		    if (!display.readAndDispatch())
		        display.sleep();
		}
		dispose();
	}

	public void set(ADataPane dataPane) {
		if(dataPane == null)
			return;
		if(dataPane.equals(this.currentDataPane))
			return;
		if(this.currentDataPane != null) {
			this.currentDataPane.dispose();
		}
		dataPane.open(dataPanel);
		this.currentDataPane = dataPane;
		history.add(dataPane);
		historyIndex = history.size() - 1;
		toggleToolbarDirections();
//		System.out.println("set: " + historyIndex);
		redraw();
	}

	public void setCurrentTurn(Turn currentTurn) {
		if(isTurnDirty()) {
			int buttonID = messageBox("Save Current Turn", "Do you wish to save the current turn first?", SWT.ICON_WARNING | SWT.YES | SWT.NO | SWT.CANCEL);
			if(buttonID == SWT.CANCEL) {
				return;
			}
			else if(buttonID == SWT.YES) {
				openSaveTurn();
			}
		}
		this.currentTurn = currentTurn;
		menu.setEnableSave(currentTurn != null);
		menu.setEnableSubmit(currentTurn != null);
		toolbar.setEnableSave(currentTurn != null);
		toolbar.setEnableSubmit(currentTurn != null);
	}

	public void setTurnDirty(boolean turnDirty) {
		this.turnDirty = turnDirty;
		menu.setEnableSave(turnDirty);
		toolbar.setEnableSave(turnDirty);
	}

	public void setTurnReport(TurnReport turnReport) {
		this.turnReport = turnReport;
	}
	
	public void showAboutDialog() {
		String aboutMessage = "StarCorp © 2007 Seyed Razavi.\n";
		messageBox("About", aboutMessage, SWT.ICON_INFORMATION | SWT.OK);
	}

	public void submitTurn() {
		if(currentTurn == null) {
			openTurnWindow();
			return;
		}
		Corporation credentials = currentTurn.getCorporation();
		if(credentials == null) {
			openTurnWindow();
			credentials = turnWindow.promptCredentials();
			if(credentials == null) {
				return;
			}
			else {
				currentTurn.setCorporation(credentials);
			}
		}
		try {
			if(ClientConfiguration.getSmtpHost() == null) {
				promptConfiguration();
			}
			if(ClientConfiguration.getSmtpHost() == null) {
				messageBox("Send Mail Error", "You have not configured your email server!", SWT.ICON_ERROR | SWT.OK);
			}
			else if(currentTurn.getCorporation().getPlayerEmail() == null) {
				messageBox("Send Mail Error", "You have not specified an email address!", SWT.ICON_ERROR | SWT.OK);
			}
			else {
				SendEmail email = new SendEmail(ClientConfiguration.getSmtpHost(), ClientConfiguration.getSmtpPort(), ClientConfiguration.getSmtpUser(), ClientConfiguration.getSmtpPassword());
				String filename = "submitted-turn.xml";
				currentTurn.write(new FileWriter(filename));
				
				String from = currentTurn.getCorporation().getPlayerEmail();
				String[] to = {ClientConfiguration.getServerEmailTurns()};
				String[] cc = {currentTurn.getCorporation().getPlayerEmail()};
				email.send(to, cc, null, "StarCorp Turn Submission", "", from, filename);
	
				menu.setEnableSubmit(false);
				toolbar.setEnableSubmit(false);
				if(turnWindow != null) {
					turnWindow.dispose();
					turnWindow = null;
				}
				messageBox("Turn Submitted", "Your turn has successfully been submitted.", SWT.ICON_INFORMATION | SWT.OK);
			}
		} catch (IOException e) {
			int buttonID = messageBox("Turn Submission Error", e.getMessage(), SWT.ICON_ERROR | SWT.ABORT | SWT.RETRY);
			if(SWT.RETRY == buttonID) {
				submitTurn();
			}
		} catch (MessagingException e) {
			int buttonID = messageBox("Turn Submission Error", e.getMessage(), SWT.ICON_ERROR | SWT.ABORT | SWT.RETRY);
			if(SWT.RETRY == buttonID) {
				submitTurn();
			}
		}
		
	}
	
}
