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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;

import starcorp.common.entities.IEntity;
import starcorp.common.entities.Planet;
import starcorp.common.types.OrderType;
import starcorp.common.types.PlanetMapSquare;
import starcorp.common.util.Util;

/**
 * starcorp.client.turns.TurnReport
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 17 Sep 2007
 */
public class OrderReport {
	
	private OrderType type;
	private IEntity target;
	private IEntity subject;
	private List<String> msgArgs = new ArrayList<String>();
	private Set scannedEntities = new HashSet();
	private PlanetMapSquare scannedLocation;
	private Planet mappedPlanet;
	
	public OrderReport() {
		
	}
	
	public OrderReport(TurnOrder order, IEntity target, IEntity subject) {
		this.type = order.getType();
		this.subject = subject;
		this.target = target;
	}
	
	public OrderReport(Element e) {
		this.type = OrderType.getType(e.attributeValue("type"));
		Element eSubject = e.element("subject");
		if(eSubject != null) {
			subject = Util.fromXML(eSubject.element("entity"));
		}
		Element eTarget = e.element("target");
		if(eTarget != null) {
			target = Util.fromXML(eTarget.element("entity"));
		}
		for(Iterator<?> i = e.elementIterator("report-arg"); i.hasNext();) {
			Element arg = (Element) i.next();
			msgArgs.add(arg.getText());
		}
		Element scanned = e.element("scanned");
		for(Iterator<?> i = scanned.elementIterator("entity"); i.hasNext();) {
			IEntity entity = Util.fromXML((Element) i.next());
			if(entity != null)
				scannedEntities.add(entity);
		}
		Element eSquare = scanned.element("map-square");
		if(eSquare != null) {
			this.scannedLocation = new PlanetMapSquare(eSquare);
		}
		Element eMap = scanned.element("planet");
		if(eMap != null) {
			this.mappedPlanet = new Planet();
			this.mappedPlanet.readXML(eMap.element("entity"));
		}
	}
	
	public Element toXML(Element parent) {
		Element root = parent.addElement("order-report");
		root.addAttribute("type", type.getKey());
		if(subject != null) {
			subject.toFullXML(root.addElement("subject"));
		}
		if(target != null) {
			target.toBasicXML(root.addElement("target"));
		}
		for(int i = 0; i < msgArgs.size(); i++) {
			Element e = root.addElement("report-arg");
			e.addText(msgArgs.get(i));
		}
		Element scanned = root.addElement("scanned");
		Iterator<?> i = scannedEntities.iterator();
		while(i.hasNext()) {
			IEntity entity = (IEntity) i.next();
			if(entity != null)
				entity.toBasicXML(scanned);
		}
		if(scannedLocation != null) {
			scannedLocation.toXML(scanned);
		}
		if(mappedPlanet != null) {
			mappedPlanet.toFullXML(scanned);
		}
		return root;
	}
	
	public String getDescription() {
		return type.getDescription(msgArgs);
	}
	
	public void add(long msgArg) {
		msgArgs.add(String.valueOf(msgArg));
	}

	public void add(String msgArg) {
		msgArgs.add(msgArg);
	}
	
	public Iterator<String> iterateArgs() {
		return msgArgs.iterator();
	}
	
	public List<String> getMsgArgs() {
		return msgArgs;
	}
	public void setMsgArgs(List<String> msgArgs) {
		this.msgArgs = msgArgs;
	}

	public Set<?> getScannedEntities() {
		return scannedEntities;
	}
	public List<?> getScannedEntities(Class clazz) {
		List list = new ArrayList();
		if(scannedEntities != null) {
			for(Object o : scannedEntities) {
				if(o == null)
					continue;
				if(clazz.isAssignableFrom(o.getClass()))
					list.add(o);
			}
		}
		return list;
	}
	public void addScannedEntities(List<?> scanned) {
		scannedEntities.addAll(scanned);
	}
	public void addScannedEntity(IEntity entity) {
		scannedEntities.add(entity);
	}
	
	public PlanetMapSquare getScannedLocation() {
		return scannedLocation;
	}

	public void setScannedLocation(PlanetMapSquare scannedLocation) {
		this.scannedLocation = scannedLocation;
	}

	public OrderType getType() {
		return type;
	}

	public void setType(OrderType type) {
		this.type = type;
	}

	public Planet getMappedPlanet() {
		return mappedPlanet;
	}

	public void setMappedPlanet(Planet mappedPlanet) {
		this.mappedPlanet = mappedPlanet;
	}

	public String toString()
	{
	    final String TAB = "    ";
	    
	    String retValue = "";
	    
	    retValue = "OrderReport ( "
	        + super.toString() + TAB
	        + "type = " + this.type + TAB
	        + "subject = " + this.subject + TAB
	        + "msgArgs = " + this.msgArgs + TAB
	        + "scannedEntities = " + this.scannedEntities + TAB
	        + "scannedLocation = " + this.scannedLocation + TAB
	        + "mappedPlanet = " + this.mappedPlanet + TAB
	        + " )";
	
	    return retValue;
	}

	public IEntity getSubject() {
		return subject;
	}

	public void setSubject(IEntity subject) {
		this.subject = subject;
	}

	public IEntity getTarget() {
		return target;
	}

	public void setTarget(IEntity target) {
		this.target = target;
	}
	
	
}
