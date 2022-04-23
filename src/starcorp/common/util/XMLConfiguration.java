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
package starcorp.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

/**
 * starcorp.common.util.XMLConfiguration
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 1 Oct 2007
 */
public class XMLConfiguration {

	private Document configDoc;
	private Element root;
	
	public XMLConfiguration(Document doc) {
		this.configDoc = doc;
	}
	
	public XMLConfiguration(InputStream is) {
		read(is);
	}
	
	private void read(InputStream is) {
		SAXReader sax = new SAXReader();
		try {
			configDoc = sax.read(is);
		} catch (DocumentException e) {
			configDoc = DocumentHelper.createDocument();
			configDoc.addElement("starcorp-config");
		}
		root = configDoc.getRootElement();
	}
	
	public XMLConfiguration(String fileName) {
		try {
			read(new FileInputStream(fileName));
		} catch (FileNotFoundException e) {
			configDoc = DocumentHelper.createDocument();
			configDoc.addElement("starcorp-config");
			root = configDoc.getRootElement();
		}
	}
	
	public void write(String fileName) throws IOException {
		write(new FileWriter(fileName));
	}
	
	public void write(Writer writer) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(
			writer, format
		);
		
		xmlWriter.write(configDoc);
		xmlWriter.close();
	}
	
	private Element getElement(String identifier) {
		return root.element(identifier);
	}
	
	public int getValueAsInt(String identifier) {
		try {
			String s = getValue(identifier,"0");
			if(s == null) {
				return 0;
			}
			return Integer.parseInt(s);
		}
		catch(NumberFormatException e) {
			return 0;
		}
	}
	
	public double getValueAsDouble(String identifier) {
		try {
			String s = getValue(identifier,"0");
			if(s == null) {
				return 0.0;
			}
			return Double.parseDouble(s);
		}
		catch(NumberFormatException e) {
			return 0.0;
		}
	}
	
	public boolean isValueTrue(String identifier) {
		String s = getValue(identifier,"false");
		if(s == null) {
			return false;
		}
		return Boolean.parseBoolean(s);
	}
	
	public long getValueAsLong(String identifier) {
		try {
			String s = getValue(identifier,"0");
			if(s == null) {
				return 0L;
			}
			return Long.parseLong(s);
		}
		catch(NumberFormatException e) {
			return 0L;
		}
	}
	
	public String getValue(String identifier, String defaultValue) {
		String s = getValue(identifier);
		if(s == null) {
			setValue(identifier,defaultValue);
			return defaultValue;
		}
		return s;
	}
	
	public String getValue(String identifier) {
		Element e = getElement(identifier);
		if(e == null) {
			return null;
		}
		else {
			return e.getTextTrim();
		}
	}
	
	public void setValue(String identifier, int value) {
		setValue(identifier, String.valueOf(value));
	}
	
	public void setValue(String identifier, long value) {
		setValue(identifier, String.valueOf(value));
	}
	
	public void setValue(String identifier, double value) {
		setValue(identifier, String.valueOf(value));
	}
	
	public void setValue(String identifier, String value) {
		Element e = getElement(identifier);
		if(e == null) {
			e = root.addElement(identifier);
		}
		e.setText(value);
	}
	
	public void setValue(String identifier, List<String> values) {
		for(Iterator i = root.elementIterator(identifier); i.hasNext(); ) {
			i.next();
			i.remove();
		}
		for(String s : values) {
			Element e = root.addElement(identifier);
			e.setText(s);
		}
	}
	
	public List<String> getValues(String identifier) {
		List<String> list = new ArrayList<String>();
		for(Iterator i = root.elementIterator(identifier); i.hasNext();) {
			Element e = (Element) i.next();
			list.add(e.getTextTrim());
		}
		return list;
	}
	
	public List<Integer> getValuesAsInts(String identifier) {
		List<Integer> list = new ArrayList<Integer>();
		for(Iterator i = root.elementIterator(identifier); i.hasNext();) {
			Element e = (Element) i.next();
			try {
				list.add(Integer.parseInt(e.getTextTrim()));
			}
			catch(NumberFormatException ex) {
				// ignore
			}
		}
		return list;
	}
	
}
