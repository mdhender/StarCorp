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

import starcorp.common.types.GalacticDate;

/**
 * starcorp.common.entities.CreditAccount
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 22 Sep 2007
 */
public class CreditAccount implements IEntity {

	private long ID;
	private int version;
	private long credits;

	public long add(int credits, GalacticDate date) {
		this.credits += credits;
		return this.credits;
	}
	
	public void remove(int credits, GalacticDate date) {
		this.credits -= credits;
	}
	
	public boolean hasCredits(int credits) {
		return this.credits >= credits;
	}
	
	public long getCredits() {
		return credits;
	}
	public void setCredits(long credits) {
		this.credits = credits;
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

	public String toString()
	{
		return "CreditAccount [" + ID + "] Balance: " + credits;
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
		final CreditAccount other = (CreditAccount) obj;
		if (ID != other.ID)
			return false;
		if (version != other.version)
			return false;
		return true;
	}

	public void printXML(Writer out) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter writer = new XMLWriter(out,format);
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("starcorp");
		writer.write(toFullXML(root));
		writer.close();
	}

	public void readXML(Element e) {
		this.ID = Long.parseLong(e.attributeValue("ID","0"));
		this.credits = Long.parseLong(e.attributeValue("credits","0"));
	}

	public Element toBasicXML(Element parent) {
		Element e = parent.addElement("credit-account");
		e.addAttribute("ID", String.valueOf(ID));
		e.addAttribute("credits", String.valueOf(credits));
		return e;
	}

	public Element toFullXML(Element parent) {
		return toBasicXML(parent);
	}

	public String getDisplayName() {
		return getID() +" balance: " + getCredits();
	}
	
}
