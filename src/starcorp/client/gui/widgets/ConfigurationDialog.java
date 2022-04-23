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

import starcorp.client.ClientConfiguration;

/**
 * starcorp.client.gui.ConfigurationDialog
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class ConfigurationDialog extends Dialog {
	private static final String ICON_RESOURCE_NAME = "/images/32x32.png";
	
	private String message;
	private String smtpHost;
	private String smtpUser;
	private String smtpPassword;
	private int smtpPort;
	
	public ConfigurationDialog(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}
	
	public ConfigurationDialog(Shell parent, int style) {
		super(parent, style);
		setText("Email Configuration");
		setMessage("Please enter your email server (SMTP) details below:");
	}

	public void open() {
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
		
		Label lblSmtpHost = new Label(shell, SWT.NONE);
		lblSmtpHost.setText("Host:");
		
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.minimumHeight=100;

		final Text txtSmtpHost = new Text(shell, SWT.BORDER);
		txtSmtpHost.setLayoutData(data);
				
		Label lblSmtpUser = new Label(shell,SWT.NONE);
		lblSmtpUser.setText("Username:");
		
		final Text txtSmtpUser = new Text(shell,SWT.BORDER);
		txtSmtpUser.setLayoutData(data);
		
		Label lblSmtpPassword = new Label(shell,SWT.NONE);
		lblSmtpPassword.setText("Password:");
		
		final Text txtSmtpPassword = new Text(shell,SWT.BORDER);
		txtSmtpPassword.setLayoutData(data);
		
		Label lblSmtpPort = new Label(shell, SWT.NONE);
		lblSmtpPort.setText("Port:");
		
		final Text txtSmtpPort = new Text(shell, SWT.BORDER);
		txtSmtpPort.setLayoutData(data);
		
		Button ok = new Button(shell, SWT.PUSH);
		ok.setText("OK");
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
		  public void widgetSelected(SelectionEvent event) {
			  smtpHost = txtSmtpHost.getText();
			  smtpPassword = txtSmtpPassword.getText();
			  try {
				  smtpPort = Integer.parseInt(txtSmtpPort.getText());
			  }
			  catch(NumberFormatException e) {
				  // ignore
			  }
			  smtpUser = txtSmtpUser.getText();
			  ClientConfiguration.setSmtpHost(smtpHost);
			  ClientConfiguration.setSmtpPassword(smtpPassword);
			  ClientConfiguration.setSmtpPort(smtpPort);
			  ClientConfiguration.setSmtpUser(smtpUser);
			  ClientConfiguration.save();
			  shell.close();
		  }
		});
		    
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
		  public void widgetSelected(SelectionEvent event) {
			  shell.close();
		  }
		});
		    
		smtpHost = ClientConfiguration.getSmtpHost();
		smtpUser = ClientConfiguration.getSmtpUser();
		smtpPassword = ClientConfiguration.getSmtpPassword();
		smtpPort = ClientConfiguration.getSmtpPort();
		
		if(smtpHost != null) txtSmtpHost.setText(smtpHost);
		if(smtpUser != null) txtSmtpUser.setText(smtpUser);
		if(smtpPassword != null) txtSmtpPassword.setText(smtpPassword);
		if(smtpPort > 0) txtSmtpPort.setText(String.valueOf(smtpPort));
		
		shell.setDefaultButton(ok);
	}
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSmtpHost() {
		return smtpHost;
	}

	public String getSmtpUser() {
		return smtpUser;
	}

	public String getSmtpPassword() {
		return smtpPassword;
	}

	public int getSmtpPort() {
		return smtpPort;
	}

}
