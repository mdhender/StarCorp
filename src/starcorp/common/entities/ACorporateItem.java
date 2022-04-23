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
package starcorp.common.entities;

import java.io.IOException;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import starcorp.common.types.Items;

/**
 * starcorp.common.entities.ACorporateItem
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 24 Sep 2007
 */
public abstract class ACorporateItem implements IEntity, Comparable<ACorporateItem> {
	private long ID;
	private int version;
	private long owner;
	private long colony;
	private Items item;
	
	public int getQuantity() {
		return item == null ? 0 : item.getQuantity();
	}
	
	public int add(int qty) {
		if(item == null) {
			return 0;
		}
		return item.add(qty);
	}
	
	public int remove(int qty) {
		if(item == null) {
			return 0;
		}
		return item.remove(qty);
	}
	
	public long getOwner() {
		return owner;
	}
	public void setOwner(long owner) {
		this.owner = owner;
	}
	public long getColony() {
		return colony;
	}
	public void setColony(long colony) {
		this.colony = colony;
	}
	public Items getItem() {
		return item;
	}
	public void setItem(Items item) {
		this.item = item;
	}

	public void readXML(Element e) {
		this.ID = Long.parseLong(e.attributeValue("ID","0"));
		this.owner = Long.parseLong(e.attributeValue("owner","0"));
		this.colony = Long.parseLong(e.attributeValue("colony","0"));
		this.item = new Items(e);
	}

	public Element toBasicXML(Element parent) {
		Element root = parent.addElement("entity");
		root.addAttribute("ID", String.valueOf(ID));
		root.addAttribute("class", getClass().getSimpleName());
		root.addAttribute("owner", String.valueOf(owner));
		root.addAttribute("colony", String.valueOf(colony));
		if(item != null)
			item.toXML(root);
		return root;
	}

	@Override
	public String toString() {
		return item.toString();
	}

	public long getID() {
		return ID;
	}

	public void setID(long id) {
		ID = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public void printXML(Writer out) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(out,format);
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("starcorp");
		writer.write(toFullXML(root));
		writer.close();
	}

	public Element toFullXML(Element parent) {
		return toBasicXML(parent);
	}

	public String getDisplayName() {
		return item == null ? "" : item.toString();
	}

	public int compareTo(ACorporateItem o) {
		return (o == null) ? 0 : o.item.compareTo(this.item);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (ID ^ (ID >>> 32));
		result = prime * result + (int) (colony ^ (colony >>> 32));
		result = prime * result + ((item == null) ? 0 : item.hashCode());
		result = prime * result + (int) (owner ^ (owner >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final ACorporateItem other = (ACorporateItem) obj;
		if (ID != other.ID)
			return false;
		if (colony != other.colony)
			return false;
		if (item == null) {
			if (other.item != null)
				return false;
		} else if (!item.equals(other.item))
			return false;
		if (owner != other.owner)
			return false;
		return true;
	}

}
