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
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;

import starcorp.common.entities.Corporation;
import starcorp.common.types.OrderType;

/**
 * starcorp.client.turns.TurnOrder
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class TurnOrder {

	private Corporation corp;
	private OrderType type;
	private List<String> args = new ArrayList<String>(); 
	private OrderReport report;
	
	private TurnError error;
	
	public TurnOrder() {
		
	}
	
	public TurnOrder(Turn turn, OrderType type) {
		this.corp = turn.getCorporation();
		this.type = type;
	}
	
	public TurnOrder(Element e) {
		this.type = OrderType.getType(e.attributeValue("type"));
		for(Iterator<?> i = e.elementIterator("order-arg"); i.hasNext();) {
			Element arg = (Element) i.next();
			args.add(arg.getTextTrim());
		}
		Element eRep = e.element("order-report");
		if(eRep != null) {
			report = new OrderReport(eRep);
		}
		Element eError = e.element("error");
		if(eError != null) {
			error = new TurnError(eError);
		}
	}
	
	public Element toXML(Element parent) {
		Element root = parent.addElement("order");
		if(type != null) { 
			root.addAttribute("type", type.getKey());
			for(int i = 0; i < args.size(); i++) {
				Element e = root.addElement("order-arg");
				if(args.get(i) != null)
					e.addText(args.get(i));
			}
			if(report != null) {
				report.toXML(root);
			}
			if(error != null) {
				error.toXML(root);
			}
		}
		return root;
	}
	
	public void add(long arg) {
		args.add(String.valueOf(arg));
	}
	
	public void add(String arg) {
		args.add(arg);
	}
	
	public Iterator<String> args() {
		return args.iterator();
	}
	
	public int size() {
		return args.size();
	}
	
	public String get(int index) {
		return index >= 0 && index < args.size() ? args.get(index) : "";
	}
	
	public void set(int index, String value) {
		if(value != null && index >= 0 && index < args.size())
			args.set(index, value);
	}
	
	public int getAsInt(int index) {
		try {
			return Integer.parseInt(get(index));
		}
		catch(NumberFormatException e) {
			return -1;
		}
		
	}
	
	public void set(int index, long value) {
		try {
			set(index, String.valueOf(value));
		}
		catch(NumberFormatException e) {
			set(index, "-1");
		}
	}
	
	public Corporation getCorp() {
		return corp;
	}
	public void setCorp(Corporation corp) {
		this.corp = corp;
	}
	public List<String> getArgs() {
		return args;
	}
	public void setArgs(List<String> args) {
		this.args = args;
	}
	public OrderType getType() {
		return type;
	}
	public void setType(OrderType type) {
		this.type = type;
	}

	public OrderReport getReport() {
		return report;
	}

	public void setReport(OrderReport report) {
		this.report = report;
	}

	public String toString()
	{
		StringBuffer sb = new StringBuffer(type == null ? "" : type.getKey());
		for(String s : args) {
			sb.append(" ");
			sb.append(s);
		}
		return sb.toString();
	}

	public TurnError getError() {
		return error;
	}

	public void setError(TurnError error) {
		this.error = error;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((args == null) ? 0 : args.hashCode());
		result = prime * result + ((corp == null) ? 0 : corp.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		final TurnOrder other = (TurnOrder) obj;
		if (args == null) {
			if (other.args != null)
				return false;
		} else if (!args.equals(other.args))
			return false;
		if (corp == null) {
			if (other.corp != null)
				return false;
		} else if (!corp.equals(other.corp))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
