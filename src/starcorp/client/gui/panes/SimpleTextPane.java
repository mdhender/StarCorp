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

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Widget;

import starcorp.client.gui.ADataPane;
import starcorp.client.gui.windows.MainWindow;

/**
 * starcorp.client.gui.SimpleTextPane
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 25 Sep 2007
 */
public class SimpleTextPane extends ADataPane {

	private Group textGroup;
	private final String heading;
	private final String text;
	
	public SimpleTextPane(MainWindow mainWindow, String heading, String text) {
		super(mainWindow);
		this.text = text;
		this.heading = heading;
	}
	
	/* (non-Javadoc)
	 * @see starcorp.client.gui.ADataPane#createWidgets(java.util.List)
	 */
	@Override
	protected void createWidgets(List<Widget> widgets) {
		textGroup = createGroup(getParent(), widgets, heading);
		textGroup.setLayout(new FillLayout(SWT.HORIZONTAL));
		createLabel(textGroup, widgets, text);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((heading == null) ? 0 : heading.hashCode());
		result = prime * result + ((text == null) ? 0 : text.hashCode());
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
		final SimpleTextPane other = (SimpleTextPane) obj;
		if (heading == null) {
			if (other.heading != null)
				return false;
		} else if (!heading.equals(other.heading))
			return false;
		if (text == null) {
			if (other.text != null)
				return false;
		} else if (!text.equals(other.text))
			return false;
		return true;
	}

}
