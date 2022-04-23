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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.IComponent;
import starcorp.common.types.PlanetMapSquare;
import starcorp.common.types.TerrainType;

/**
 * starcorp.client.gui.widgets.PlanetMap
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 27 Sep 2007
 */
public class PlanetMap implements IComponent {

	private Composite panel;
	private List<Widget> widgets = new ArrayList<Widget>();
	
	private Map<Integer, Map<Integer,TerrainType>> map = new HashMap<Integer, Map<Integer,TerrainType>>();
	private Map<TerrainType, Image> terrainImages = new HashMap<TerrainType, Image>();

	private int height;
	private int width;
	
	public PlanetMap(Set<PlanetMapSquare> set) {
		readSet(set);
	}
	
	private void readSet(Set<PlanetMapSquare> set) {
		for(PlanetMapSquare sq : set) {
			Map<Integer,TerrainType> row = map.get(sq.getY());
			if(row == null) {
				row = new HashMap<Integer, TerrainType>();
				map.put(sq.getY(),row);
			}
			row.put(sq.getX(),sq.getTerrainType());
			if(sq.getX() > width)
				width = sq.getX();
			if(sq.getY() > height)
				height = sq.getY();
		}
	}
	
	public Point computeSize() {
		return panel.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	}

	public void dispose() {
		for(Widget w : widgets) {
			if(!w.isDisposed())
				w.dispose();
		}
		for(TerrainType type : terrainImages.keySet()) {
			Image img = terrainImages.get(type);
			if(!img.isDisposed())
				img.dispose();
		}
		panel.dispose();
	}
	
	public void open(Composite parent) {
		panel = new Composite(parent,SWT.NONE);
		GridLayout layout = new GridLayout(width,true);
		layout.marginWidth=0;
		layout.marginHeight=0;
		panel.setLayout(layout);

		loadTerrainImages(parent);
		openMap();
		redraw();
	}
	
	private TerrainType get(int x, int y) {
		Map<Integer,TerrainType> row = map.get(y);
		if(row != null) {
			return row.get(x);
		}
		return null;
	}
	
	private void openMap() {
		GridData data = new GridData();
		data.horizontalAlignment=SWT.RIGHT;
		data.verticalAlignment=SWT.CENTER;
		data.minimumWidth=30;
		data.minimumHeight=30;
		
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++) {
				TerrainType type = get(j+1,i+1);
				Image img = terrainImages.get(type);
				Label lbl = new Label(panel,SWT.NONE);
				lbl.setLayoutData(data);
				lbl.setImage(img);
				lbl.setToolTipText(type.getName() + " (" + (j + 1) + "," + (i + 1) + ")");
				widgets.add(lbl);
			}
		}
	}
	
	private void loadTerrainImages(Composite parent) {
		List<TerrainType> types = TerrainType.listTypes();
		for(TerrainType type : types) {
			String imageFile = type.getImageFilename();
			InputStream is = getClass().getResourceAsStream(imageFile);
			if(is != null) {
				Image img = new Image(parent.getDisplay(),is);
				terrainImages.put(type, img);
			}
			else {
				System.err.println("Could not load " + imageFile);
			}
		}
	}

	public void redraw() {
		panel.pack();
	}

}
