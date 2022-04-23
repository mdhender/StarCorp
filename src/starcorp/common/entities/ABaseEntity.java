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

/**
 * starcorp.common.entities.BaseEntity
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public abstract class ABaseEntity implements IEntity, Comparable<ABaseEntity> {
	
	private long ID;
	private int version;
	public ABaseEntity() {
		
	}
	
	/* (non-Javadoc)
	 * @see starcorp.common.entities.IEntity#readXML(org.dom4j.Element)
	 */
	public void readXML(Element e) {
		this.ID = Long.parseLong(e.attributeValue("ID","0"));
	}
	
	/* (non-Javadoc)
	 * @see starcorp.common.entities.IEntity#toBasicXML(org.dom4j.Element)
	 */
	public Element toBasicXML(Element parent) {
		Element root = parent.addElement("entity");
		root.addAttribute("ID", String.valueOf(ID));
		root.addAttribute("class", getClass().getSimpleName());
		return root;
	}
	
	/* (non-Javadoc)
	 * @see starcorp.common.entities.IEntity#toFullXML(org.dom4j.Element)
	 */
	public Element toFullXML(Element parent) {
		return toBasicXML(parent);
	}
	
	/* (non-Javadoc)
	 * @see starcorp.common.entities.IEntity#getID()
	 */
	public long getID() {
		return ID;
	}
	/* (non-Javadoc)
	 * @see starcorp.common.entities.IEntity#setID(long)
	 */
	public void setID(long id) {
		ID = id;
	}
	@Override
	public String toString() {
		return "(" + getClass().getSimpleName() + ": " + ID + ")";
	}
	
	/* (non-Javadoc)
	 * @see starcorp.common.entities.IEntity#printXML(java.io.Writer)
	 */
	public void printXML(Writer out) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(out,format);
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("starcorp");
		writer.write(toFullXML(root));
		writer.close();
	}

	/* (non-Javadoc)
	 * @see starcorp.common.entities.IEntity#getVersion()
	 */
	public int getVersion() {
		return version;
	}

	/* (non-Javadoc)
	 * @see starcorp.common.entities.IEntity#setVersion(int)
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (ID ^ (ID >>> 32));
		result = prime * result + (int) (version ^ (version >>> 32));
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
		final ABaseEntity other = (ABaseEntity) obj;
		if (ID != other.ID)
			return false;
		return true;
	}

	public int compareTo(ABaseEntity o) {
		return (int) ((o == null ? 0 :o.ID) - this.ID);
	}

	
}
