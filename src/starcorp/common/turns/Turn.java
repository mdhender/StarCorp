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
package starcorp.common.turns;

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

import starcorp.common.entities.Corporation;
import starcorp.common.types.GalacticDate;

/**
 * starcorp.client.turns.Turn
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class Turn {
	public static final String VERSION = "0.9.1.0";
	
	private String version;
	private GalacticDate processedDate;
	private Corporation corporation;
	private List<TurnOrder> orders = new ArrayList<TurnOrder>();
	private List<TurnError> errors = new ArrayList<TurnError>();
	
	public Turn() {
		this.version = VERSION;
	}
	
	public Turn(Corporation corp) {
		corporation = corp;
		this.version = VERSION;
	}
	
	public List<OrderReport> getOrderReports() {
		List<OrderReport> reports = new ArrayList<OrderReport>();
		for(TurnOrder order : orders) {
			if(order.getReport() != null)
				reports.add(order.getReport());
		}
		return reports;
	}
	
	public List<?> getScannedEntities() {
		List list = new ArrayList();
		for(OrderReport report : getOrderReports()) {
			list.addAll(report.getScannedEntities());
		}
		return list;
	}
	
	public List<?> getScannedEntities(Class clazz) {
		List list = new ArrayList();
		for(Object o : getScannedEntities()) {
			if(clazz.isAssignableFrom(o.getClass()))
				list.add(o);
		}
		return list;
	}
	
	public Turn(InputStream is) throws DocumentException {
		SAXReader sax = new SAXReader();
		Document doc = sax.read(is);
		readXML(doc.getRootElement().element("turn"));
	}
	
	public void write(Writer writer) throws IOException {
		// OutputFormat format = OutputFormat.createCompactFormat();
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(
			writer, format
		);
		
		Document doc = DocumentHelper.createDocument();
		Element root = doc.addElement("starcorp");
		toXML(root);
		
		xmlWriter.write(doc);
		xmlWriter.close();
	}
	
	public Turn(Element root) {
		readXML(root);
	}
	
	public void readXML(Element root) {
		if(root == null)
			return;
		this.version = root.attributeValue("version");
		this.corporation = new Corporation();
		Element eCorp = root.element("corporation");
		if(eCorp != null) this.corporation.readXML(eCorp.element("entity"));
		Element eDate = root.element("processed");
		if(eDate != null) {
			this.processedDate = new GalacticDate(eDate.element("date"));
		}
		Element eOrders = root.element("orders");
		if(eOrders != null) {
			for(Iterator<?> i = eOrders.elementIterator("order"); i.hasNext();) {
				this.orders.add(new TurnOrder((Element)i.next()));
			}
		}
		
		Element eErrors = root.element("errors");
		if(eErrors != null) {
			for(Iterator<?> i = eErrors.elementIterator("error"); i.hasNext();) {
				this.errors.add(new TurnError((Element)i.next()));
			}
		}
	}
	
	public Element toXML(Element parent) {
		Element root = parent.addElement("turn");
		root.addAttribute("version", VERSION);
		if(corporation != null)
			corporation.toFullXML(root.addElement("corporation"));
		if(processedDate != null)
			processedDate.toXML(root.addElement("processed"));
		if(orders.size() > 0) {
			Element eOrders = root.addElement("orders");
			eOrders.addAttribute("size", String.valueOf(orders.size()));
			for(int i = 0; i < orders.size(); i++) {
				orders.get(i).toXML(eOrders);
			}
		}
		if(errors.size() > 0) {
			Element eErrors = root.addElement("errors");
			eErrors.addAttribute("size", String.valueOf(errors.size()));
			for(int i = 0; i < errors.size(); i++) {
				errors.get(i).toXML(eErrors);
			}
		}
		return root;
	}
	
	public void add(TurnOrder order) {
		orders.add(order);
	}
	
	public void remove(int orderIndex) {
		orders.remove(orderIndex);
	}
	
	public Iterator<TurnOrder> orders() {
		return orders.iterator();
	}
	
	public void add(TurnError error) {
		errors.add(error);
	}
	
	public Iterator<TurnError> errors() {
		return errors.iterator();
	}
	
	public GalacticDate getProcessedDate() {
		return processedDate;
	}
	public void setProcessedDate(GalacticDate processedDate) {
		this.processedDate = processedDate;
	}
	public Corporation getCorporation() {
		return corporation;
	}
	public void setCorporation(Corporation corporation) {
		this.corporation = corporation;
	}
	public List<TurnOrder> getOrders() {
		return orders;
	}
	public void setOrders(List<TurnOrder> orders) {
		this.orders = orders;
	}
	public List<TurnError> getErrors() {
		return errors;
	}
	public void setErrors(List<TurnError> errors) {
		this.errors = errors;
	}

	public String toString()
	{
	    final String TAB = "    ";
	    
	    String retValue = "";
	    
	    retValue = "Turn ( "
	        + super.toString() + TAB
	        + "processedDate = " + this.processedDate + TAB
	        + "corporation = " + this.corporation + TAB
	        + "orders = " + this.orders + TAB
	        + "errors = " + this.errors + TAB
	        + " )";
	
	    return retValue;
	}

	public String getVersion() {
		return version;
	}
}
