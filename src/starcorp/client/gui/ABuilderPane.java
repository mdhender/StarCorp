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

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

/**
 * starcorp.client.gui.ASearchPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public abstract class ABuilderPane extends AWindowPane {

	private Group builderGroup;
	
	public ABuilderPane(AWindow mainWindow) {
		super(mainWindow);
	}
	
	/* (non-Javadoc)
	 * @see starcorp.client.gui.AWindowPane#createWidgets(java.util.List)
	 */
	@Override
	protected void createWidgets(List<Widget> widgets) {
		builderGroup = createGroup(super.getParent(), widgets, "");
		GridData data = new GridData(SWT.LEFT,SWT.TOP,true,true,1,1);
		RowLayout layout = new RowLayout(SWT.HORIZONTAL);
		layout.marginWidth=20;
		layout.marginHeight=10;
		builderGroup.setLayout(layout);
		builderGroup.setLayoutData(data);
	}
	
	@Override
	protected Group getParent() {
		return builderGroup;
	}
	
	@Override
	public void redraw() {
		builderGroup.pack();
		builderGroup.redraw();
		super.redraw();
	}
}
