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

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import starcorp.client.gui.windows.MainWindow;

/**
 * starcorp.client.gui.ADataPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public abstract class ADataPane extends AWindowPane {

	protected final MainWindow mainWindow;
	
	public ADataPane(MainWindow mainWindow) {
		super(mainWindow);
		this.mainWindow = mainWindow;
	}

	@Override
	public void open(Composite parent) {
		super.open(parent);
		GridData data = new GridData(500,500);
		getParent().setLayoutData(data);
		GridLayout layout = new GridLayout(1,false);
		layout.marginWidth=20;
		layout.marginHeight=10;
		getParent().setLayout(layout);
	}

}
