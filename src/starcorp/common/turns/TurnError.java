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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.dom4j.Element;

/**
 * starcorp.client.turns.TurnError
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class TurnError {
	
	protected static final ResourceBundle bundle = ResourceBundle.getBundle("starcorp.common.turns.errors");

	public static final TurnError ERROR_AUTHORIZATION_FAILED = new TurnError("error.authorization",null);
	public static final TurnError ERROR_VERSION_INVALID = new TurnError("error.version",null);
	
	public static final String INVALID_ORDER_TYPE = "error.order.type";
	public static final String INVALID_LOCATION = "error.order.location";
	public static final String INVALID_COLONY = "error.colony";
	public static final String INVALID_FACILITY = "error.facility";
	public static final String INVALID_FACILITY_TYPE = "error.facility.type";
	public static final String INSUFFICIENT_BUILDING_MODULES = "error.facility.modules";
	public static final String INSUFFICIENT_CREDITS = "error.credits";
	public static final String NO_SUCH_LAW = "error.law.none";
	public static final String NO_LEASE = "error.lease.none";
	public static final String INVALID_SHIP = "error.ship";
	public static final String INVALID_SHIP_DESIGN = "error.ship.design";
	public static final String INSUFFICIENT_TIME = "error.ship.time";
	public static final String OUT_OF_RANGE = "error.ship.range";
	public static final String GRAVITY_TOO_HIGH = "error.ship.gravity";
	public static final String INSUFFICIENT_SHIP_HULLS = "error.ship.hulls";
	public static final String INSUFFICIENT_SPACE = "error.ship.cargo";
	public static final String INVALID_ITEM = "error.item";
	public static final String MARKET_OUT_OF_TRANSACTIONS = "error.market.transactions";
	public static final String INVALID_POP_CLASS = "error.pop.class";
	public static final String INVALID_TARGET = "error.target";
	
	private String msgKey;
	
	public TurnError() {
		
	}
	
	public TurnError(String msgKey, TurnOrder order) {
		this.msgKey = msgKey;
		if(order != null)
			order.setError(this);
	}
	
	public TurnError(Element e) {
		this.msgKey = e.attributeValue("key");
	}
	
	public Element toXML(Element parent) {
		Element root = parent.addElement("error");
		root.addAttribute("key", msgKey);
		return root;
	}
	
	public String getMessage() {
		
		try {
			return bundle.getString(msgKey);
		}
		catch(MissingResourceException e) {
			return msgKey;
		}
		catch(NullPointerException e) {
			return "!" + msgKey + "!";
		}
	}
	
	public String getMsgKey() {
		return msgKey;
	}
	public void setMsgKey(String msgKey) {
		this.msgKey = msgKey;
	}

	public String toString()
	{
	    final String TAB = "    ";
	    
	    String retValue = "";
	    
	    retValue = "TurnError ( "
	        + super.toString() + TAB
	        + "msgKey = " + this.msgKey + TAB
	        + " )";
	
	    return retValue;
	}
}
