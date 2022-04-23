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

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.windows.MainWindow;
import starcorp.common.types.ABaseType;

/**
 * starcorp.client.gui.ATypePane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 26 Sep 2007
 */
public class ATypePane extends ADataPane {

	private Group typeGroup;
	private final ABaseType type;
	
	/**
	 * @param mainWindow
	 */
	public ATypePane(MainWindow mainWindow, ABaseType type) {
		super(mainWindow);
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see starcorp.client.gui.AWindowPane#createWidgets(java.util.List)
	 */
	@Override
	protected void createWidgets(List<Widget> widgets) {
		typeGroup = createGroup(super.getParent(), widgets, type.getName());
		GridLayout layout = new GridLayout(2,false);
		layout.marginWidth=20;
		layout.marginHeight=10;
		typeGroup.setLayout(layout);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ATypePane other = (ATypePane) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	@Override
	protected Group getParent() {
		return typeGroup;
	}
	
	@Override
	public void redraw() {
		typeGroup.pack();
		typeGroup.redraw();
		super.redraw();
	}

}
