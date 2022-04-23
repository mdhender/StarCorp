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

import starcorp.common.entities.IEntity;

/**
 * starcorp.common.util.Util
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 24 Sep 2007
 */
public class Util {

	public static void write(List<?> entities, String fileName, String rootElementName, boolean fullXML) throws IOException {
		write(entities, new FileWriter(fileName), rootElementName, fullXML);
	}
	
	public static void write(List<?> entities, Writer writer, String rootElementName, boolean fullXML) throws IOException {
		// OutputFormat format = OutputFormat.createCompactFormat();
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(
			writer, format
		);
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(rootElementName);
		for(Object o : entities) {
			IEntity entity = (IEntity) o;
			if(fullXML) {
				entity.toFullXML(root);
			}
			else {
				entity.toBasicXML(root);
			}
		}
		
		xmlWriter.write(doc);
		xmlWriter.close();
	}
	
	public static void write(IEntity entity, String fileName, String rootElementName, boolean fullXML) throws IOException {
		write(entity, new FileWriter(fileName), rootElementName, fullXML);
	}
	
	public static void write(IEntity entity, Writer writer, String rootElementName, boolean fullXML) throws IOException {
		// OutputFormat format = OutputFormat.createCompactFormat();
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(
			writer, format
		);
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement(rootElementName);
		if(fullXML) {
			entity.toFullXML(root);
		}
		else {
			entity.toBasicXML(root);
		}
		
		xmlWriter.write(doc);
		xmlWriter.close();
	}
	
	public static List<IEntity> readXML(String fileName) throws DocumentException, FileNotFoundException {
		return readXML(new FileInputStream(fileName));
	}
	
	public static List<IEntity> readXML(InputStream is) throws DocumentException {
		SAXReader sax = new SAXReader();
		Document doc = sax.read(is);
		Element root = doc.getRootElement();
		List<IEntity> list = new ArrayList<IEntity>();
		for(Iterator<?> i = root.elementIterator("entity"); i.hasNext();) {
			Element e = (Element) i.next();
			list.add(fromXML(e));
		}
		return list;
	}

	public static IEntity fromXML(Element e) {
		if(e == null) {
			return null;
		}
		String className = "starcorp.common.entities." + e.attributeValue("class");
		IEntity entity;
		try {
			entity = (IEntity) Class.forName(className).newInstance();
			entity.readXML(e);
			return entity;
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		return null;
	}

}
