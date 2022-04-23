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

import javax.mail.MessagingException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
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
import starcorp.common.entities.Corporation;
import starcorp.common.util.SendEmail;

/**
 * starcorp.client.gui.SendEmailDialog
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 3 Oct 2007
 */
public class SendEmailDialog extends Dialog {
	private static final String ICON_RESOURCE_NAME = "/images/32x32.png";
	
	private Corporation fromCorporation;
	private Corporation toCorporation;
	private boolean sent;

	public SendEmailDialog(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, null,null);
	}
	
	public SendEmailDialog(Shell parent, Corporation from, Corporation to) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, from, to);
	}

	public SendEmailDialog(Shell parent, int style) {
		this(parent, style, null,null);
	}

	public SendEmailDialog(Shell parent, int style, Corporation from, Corporation to) {
		super(parent,style);
		setFromCorporation(from);
		setToCorporation(to);
		setText("Send Email");
	}
	
	public boolean open() {
		final Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		InputStream is = getClass().getResourceAsStream(ICON_RESOURCE_NAME);
		if(is != null) {
			Image icon = new Image(shell.getDisplay(),is);
			shell.setImage(icon);
		}
		createContents(shell);
		shell.pack();
		center(shell);
		shell.open();
		Display display = getParent().getDisplay();
	    while (!shell.isDisposed()) {
	      if (!display.readAndDispatch()) {
	        display.sleep();
	      }
	    }
	    return sent;
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
		GridLayout layout = new GridLayout(2,false);
		layout.marginHeight=5;
		layout.marginWidth=5;
		shell.setLayout(layout);
		
		Label lblTo = new Label(shell, SWT.NONE);
		lblTo.setText("To:");
		
		Label lblToName = new Label(shell, SWT.NONE);
		lblToName.setText(toCorporation.getDisplayName());

		Label lblSubject = new Label(shell, SWT.NONE);
		lblSubject.setText("Subject:");

		final Text txtSubject = new Text(shell,SWT.BORDER);
		GC gc = new GC(txtSubject);
		FontMetrics fm = gc.getFontMetrics();
		int width = 90 * fm.getAverageCharWidth();
		int height = fm.getHeight();
		gc.dispose();
		GridData data = new GridData();
		data.widthHint = width;
		data.heightHint = height;
		txtSubject.setLayoutData(data);
		
		Label lblBody = new Label(shell,SWT.NONE);
		data = new GridData();
		data.horizontalSpan=2;
		lblBody.setText("Message:");
		
		final Text txtBody = new Text(shell,SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		gc = new GC(txtSubject);
		fm = gc.getFontMetrics();
		width = 100 * fm.getAverageCharWidth();
		height = 20 * fm.getHeight();
		gc.dispose();
		data = new GridData();
		data.horizontalSpan=2;
		data.widthHint = width;
		data.heightHint = height;
		txtBody.setLayoutData(data);
		
		Button send = new Button(shell, SWT.PUSH);
		send.setText("Send");
		send.addSelectionListener(new SelectionAdapter() {
		  public void widgetSelected(SelectionEvent event) {
			  SendEmail sender = new SendEmail(ClientConfiguration.getSmtpHost(),ClientConfiguration.getSmtpPort(),ClientConfiguration.getSmtpUser(),ClientConfiguration.getSmtpPassword());
			  String[] to = {toCorporation.getPlayerEmail()};
			  String subject = txtSubject.getText();
			  String body = txtBody.getText();
			  String from = fromCorporation.getPlayerEmail();
			  try {
				sender.send(to, null, null, subject, body, from, null);
				sent = true;
				} catch (MessagingException e) {
					e.printStackTrace();
				}
			  shell.close();
		  }
		});
		    
		Button cancel = new Button(shell, SWT.PUSH);
		cancel.setText("Cancel");
		cancel.addSelectionListener(new SelectionAdapter() {
		  public void widgetSelected(SelectionEvent event) {
			  shell.close();
		  }
		});
		    
		shell.setDefaultButton(send);
	}

	public Corporation getFromCorporation() {
		return fromCorporation;
	}

	public void setFromCorporation(Corporation fromCorporation) {
		this.fromCorporation = fromCorporation;
	}

	public Corporation getToCorporation() {
		return toCorporation;
	}

	public void setToCorporation(Corporation toCorporation) {
		this.toCorporation = toCorporation;
	}

	public boolean isSent() {
		return sent;
	}

}
