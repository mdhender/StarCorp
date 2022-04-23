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
package starcorp.common.types;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * starcorp.common.types.GalacticDate
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class GalacticDate {

	private int year;
	private int month;
	
	public GalacticDate() {
		
	}
	
	public GalacticDate(int year, int month) {
		this.year = year;
		this.month = month;
	}
	
	public GalacticDate(GalacticDate copyFrom) {
		this.year = copyFrom.year;
		this.month = copyFrom.month;
	}
	
	public GalacticDate(Element e) {
		readXML(e);
	}
	
	public GalacticDate(InputStream is) throws DocumentException {
		SAXReader sax = new SAXReader();
		Document doc = sax.read(is);
		readXML(doc.getRootElement().element("date"));
	}
	
	public void readXML(Element e) {
		this.year = Integer.parseInt(e.attributeValue("year"));
		this.month = Integer.parseInt(e.attributeValue("month"));
	}
	
	public void write(Writer writer) throws IOException {
		OutputFormat format = OutputFormat.createCompactFormat();
		// OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(
			writer, format
		);
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("date");
		toXML(root);
		
		xmlWriter.write(doc);
		xmlWriter.close();
		writer.close();
	}
	
	public Element toXML(Element parent) {
		Element root = parent.addElement("date");
		root.addAttribute("year", String.valueOf(year));
		root.addAttribute("month", String.valueOf(month));
		return root;
	}
	
	public boolean before(GalacticDate other) {
		if(this.year < other.year) {
			return true;
		}
		else if(this.year == other.year) {
			if(this.month < other.month) {
				return true;
			}
		}
		return false;
	}
	
	public boolean after(GalacticDate other) {
		if(this.year > other.year) {
			return true;
		}
		else if(this.year == other.year) {
			if(this.month > other.month) {
				return true;
			}
		}
		return false;
	}
	
	public boolean same(GalacticDate other) {
		return this.year == other.year && this.month == other.month;
	}
	
	public int monthsDiff(GalacticDate other) {
		int months = (other.year - this.year) * 12;
		months += (other.month - this.month);
		return months;
	}
	
	public int yearsDiff(GalacticDate other) {
		return other.year - this.year;
	}
	
	public GalacticDate add(int years, int months) {
		this.year += years;
		return add(months);
	}
	
	public GalacticDate add(int months) {
		this.month += months;
		if(this.month > 12) {
			int y = (this.month) / 12;
			this.year += y;
			this.month = this.month % 12;
		}
		return this;
	}
	
	public int getYear() {
		return year;
	}
	public GalacticDate setYear(int year) {
		this.year = year;
		return this;
	}
	public int getMonth() {
		return month;
	}
	public GalacticDate setMonth(int month) {
		this.month = month;
		return this;
	}

	@Override
	public String toString() {
		return month + "." + year + " GY";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + month;
		result = prime * result + year;
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
		final GalacticDate other = (GalacticDate) obj;
		if (month != other.month)
			return false;
		if (year != other.year)
			return false;
		return true;
	}
	
	
}
