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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import starcorp.common.entities.Corporation;

/**
 * starcorp.client.gui.CredentialsDialog
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class CredentialsDialog extends Dialog {
	private static final String ICON_RESOURCE_NAME = "/images/32x32.png";
	
	private String message;
	private Corporation credentials;
	
	public CredentialsDialog(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, null);
	}
	
	public CredentialsDialog(Shell parent, Corporation credentials) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, credentials);
	}

	public CredentialsDialog(Shell parent, int style) {
		this(parent, style, null);
	}

	public CredentialsDialog(Shell parent, int style, Corporation credentials) {
		super(parent,style);
		setCredentials(credentials);
		setText("Player Credentials");
		setMessage("Please enter your account details below:");
	}
	
	public Corporation open() {
		final Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		InputStream is = getClass().getResourceAsStream(ICON_RESOURCE_NAME);
		if(is != null) {
			Image icon = new Image(shell.getDisplay(),is);
			shell.setImage(icon);
		}
		createContents(shell);
		shell.setSize(300, 180);
		center(shell);
		shell.open();
		Display display = getParent().getDisplay();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch()) {
	        display.sleep();
	      }
	    }
	    return credentials;
	}
	
	protected void center(Shell shell) {
		Display display = shell.getDisplay();
		Rectangle bounds = display.getBounds();
		Point size = shell.getSize();
		
		int x = (bounds.width - size.x) / 2;
		int y = (bounds.height - size.y) / 2;
		
		shell.setBounds(x, y, size.x, size.y);
	}
	
	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(2,false));
		
		Label lblCorpName = new Label(shell, SWT.NONE);
		lblCorpName.setText("Corporation:");
		
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.minimumHeight=100;

		final Text txtCorpName = new Text(shell, SWT.BORDER);
		txtCorpName.setLayoutData(data);
				
		Label lblPlayerName = new Label(shell,SWT.NONE);
		lblPlayerName.setText("Name:");
		
		final Text txtPlayerName = new Text(shell,SWT.BORDER);
		txtPlayerName.setLayoutData(data);
		
		Label lblPlayerEmail = new Label(shell,SWT.NONE);
		lblPlayerEmail.setText("Email:");
		
		final Text txtPlayerEmail = new Text(shell,SWT.BORDER);
		txtPlayerEmail.setLayoutData(data);
		
		Label lblPlayerPassword = new Label(shell, SWT.NONE);
		lblPlayerPassword.setText("Password:");
		
		final Text txtPlayerPassword = new Text(shell, SWT.BORDER);
		txtPlayerPassword.setLayoutData(data);
		
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
		  public void widgetSelected(SelectionEvent event) {
			  credentials = new Corporation(txtPlayerName.getText(),txtPlayerEmail.getText(),txtPlayerPassword.getText());
			  credentials.setName(txtCorpName.getText());
			  shell.close();
		  }
		});
		    
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
		  public void widgetSelected(SelectionEvent event) {
			  credentials = null;
			  shell.close();
		  }
		});
		    
		if(credentials != null) {
			String name = credentials.getName();
			if(name == null) name = "";
			String playerName = credentials.getPlayerName();
			if(playerName == null) playerName = "";
			String email = credentials.getPlayerEmail();
			if(email == null) email = "";
			String password = credentials.getPlayerPassword();
			if(password == null) password = "";
			txtCorpName.setText(name);
			txtPlayerName.setText(playerName);
			txtPlayerEmail.setText(email);
			txtPlayerPassword.setText(password);
		}
		
		shell.setDefaultButton(ok);
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Corporation getCredentials() {
		return credentials;
	}

	public void setCredentials(Corporation credentials) {
		this.credentials = credentials;
	}

	
}
