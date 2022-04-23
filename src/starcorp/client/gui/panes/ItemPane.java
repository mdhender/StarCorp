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
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.ATypePane;
import starcorp.client.gui.windows.MainWindow;
import starcorp.common.types.AFactoryItem;
import starcorp.common.types.AItemType;
import starcorp.common.types.BuildingModules;
import starcorp.common.types.ConsumerGoods;
import starcorp.common.types.IndustrialGoods;
import starcorp.common.types.Items;
import starcorp.common.types.Resources;
import starcorp.common.types.StarshipHulls;

/**
 * starcorp.client.gui.panes.ItemPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 26 Sep 2007
 */
public class ItemPane extends ATypePane {
	private AItemType type;
	
	/**
	 * @param mainWindow
	 * @param type
	 */
	public ItemPane(MainWindow mainWindow, AItemType type) {
		super(mainWindow, type);
		this.type = type;
		if(type == null) {
			throw new NullPointerException("Invalid type");
		}
	}

	@Override
	protected void createWidgets(List<Widget> widgets) {
		super.createWidgets(widgets);
		
		Group grp = createGroup(getParent(), widgets, "");
		GridLayout layout = new GridLayout(2,false);
		layout.marginWidth=20;
		layout.marginHeight=10;
		grp.setLayout(layout);
		
		createLabel(grp, widgets, "Key:");
		createLabel(grp, widgets, type.getKey());
		
		createLabel(grp, widgets, "Mass:");
		createLabel(grp, widgets, format(type.getMassUnits()));
		
		if(type instanceof ConsumerGoods) {
			createLabel(grp, widgets, "Consumer Goods:");
		}
		
		if(type instanceof IndustrialGoods) {
			createLabel(grp, widgets, "Industrial Goods:");
		}
		
		if(type instanceof Resources) {
			createLabel(grp, widgets, "Resources:");
		}
		
		if(type instanceof StarshipHulls) {
			createLabel(grp, widgets, "Starship Hulls:");
		}
		
		if(type instanceof BuildingModules) {
			createLabel(grp, widgets, "Building Modules:");
		}
		
		createLabel(grp, widgets, type.getSubCategory());

		if(type instanceof AFactoryItem) {
			AFactoryItem f = (AFactoryItem) type;
			Set<Items> components = f.getComponent();
			grp = createGroup(getParent(), widgets, "Components");
			grp.setLayout(new RowLayout(SWT.VERTICAL));
			GridData data = new GridData();
			data.horizontalSpan=2;
			grp.setLayoutData(data);
			for(Items item : components) {
				createItemLink(grp, widgets, item, null);
			}
		}
		
		Set<AItemType> components = type.usedIn();
		if(components.size() > 0) {
			grp = createGroup(getParent(), widgets, "Needed For");
			grp.setLayout(new GridLayout(5,false));
			GridData data = new GridData();
			data.horizontalSpan=2;
			grp.setLayoutData(data);
			for(AItemType type : components) {
				createItemLink(grp, widgets, type, null);
			}
		}
	}

}
