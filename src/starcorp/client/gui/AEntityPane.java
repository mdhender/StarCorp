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
import starcorp.common.entities.IEntity;

/**
 * starcorp.client.gui.AEntityPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public abstract class AEntityPane extends ADataPane {

	private final IEntity entity;
	private Group entityGroup;
	
	public AEntityPane(MainWindow mainWindow, IEntity entity) {
		super(mainWindow);
		this.entity = entity;
	}

	@Override
	protected void createWidgets(List<Widget> widgets) {
		entityGroup = createGroup(super.getParent(), widgets, entity.getDisplayName());
		GridLayout layout = new GridLayout(2,false);
		layout.marginWidth=20;
		layout.marginHeight=10;
		entityGroup.setLayout(layout);
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
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
		final AEntityPane other = (AEntityPane) obj;
		if (entity == null) {
			if (other.entity != null)
				return false;
		} else if (!entity.equals(other.entity))
			return false;
		return true;
	}
	
	@Override
	protected Group getParent() {
		return entityGroup;
	}

	@Override
	public void redraw() {
		entityGroup.pack();
		entityGroup.redraw();
		super.redraw();
	}

	
}
