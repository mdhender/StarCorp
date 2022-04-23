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

import starcorp.common.types.AItemType;
import starcorp.common.types.Coordinates2D;

/**
 * starcorp.common.entities.ResourceDeposit
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class ResourceDeposit implements IEntity {
	private long ID;
	private int version;
	private long systemEntity;
	private Coordinates2D location;
	private AItemType type;
	private int totalQuantity;
	private int yield;
	
	/**
	 * @return
	 */
	public String getType() {
		return type == null ? null : type.getKey();
	}
	
	/**
	 * @param key
	 */
	public void setType(String key) {
		type = AItemType.getType(key);
	}
	
	/**
	 * @return
	 */
	public AItemType getTypeClass() {
		return type;
	}
	
	/**
	 * @param type
	 */
	public void setTypeClass(AItemType type) {
		this.type = type;
	}
	
	/**
	 * @return
	 */
	public int getYield() {
		return yield;
	}
	/**
	 * @param quantity
	 */
	public void setYield(int quantity) {
		this.yield = quantity;
	}
	
	/**
	 * @param qty
	 * @return
	 */
	/**
	 * @param qty
	 * @return
	 */
	public int remove(int qty) {
		if(qty > totalQuantity) {
			qty = totalQuantity;
		}
		totalQuantity -= qty;
		return qty;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (ID ^ (ID >>> 32));
		result = prime * result + version;
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
		final ResourceDeposit other = (ResourceDeposit) obj;
		if (ID != other.ID)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	public int getTotalQuantity() {
		return totalQuantity;
	}

	public void setTotalQuantity(int totalQuantity) {
		this.totalQuantity = totalQuantity;
	}

	public long getSystemEntity() {
		return systemEntity;
	}

	public void setSystemEntity(long systemEntity) {
		this.systemEntity = systemEntity;
	}

	public Coordinates2D getLocation() {
		return location;
	}

	public void setLocation(Coordinates2D location) {
		this.location = location;
	}

	public void setType(AItemType type) {
		this.type = type;
	}

	public void readXML(Element e) {
		this.ID = Long.parseLong(e.attributeValue("ID","0"));
		this.systemEntity = Long.parseLong(e.attributeValue("entity","0"));
		this.location = new Coordinates2D(e);
		this.type = AItemType.getType(e.attributeValue("type"));
		this.totalQuantity = Integer.parseInt(e.attributeValue("total","0"));
		this.yield = Integer.parseInt(e.attributeValue("yield","0"));
	}

	public Element toBasicXML(Element parent) {
		Element root = parent.addElement("entity");
		root.addAttribute("ID", String.valueOf(ID));
		root.addAttribute("class", getClass().getSimpleName());
		root.addAttribute("entity",String.valueOf(systemEntity));
		if(location != null) location.toXML(root);
		root.addAttribute("type", type.getKey());
		root.addAttribute("total", String.valueOf(totalQuantity));
		root.addAttribute("yield", String.valueOf(yield));
		return root;
	}

	@Override
	public String toString() {
		return type.getKey() + " x " + totalQuantity + " [" + getID() + "] @ " + systemEntity + ") " + location;
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
		return getTypeClass().getName() + " x " + getYield();
	}
}
