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
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import starcorp.common.types.GalacticDate;

/**
 * starcorp.common.entities.CashTransaction
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 18 Sep 2007
 */
public class CashTransaction implements IEntity {

	public static final String LEASE_XFER = "lease.xfer";
	public static final String GRANT_PAID = "grant.paid";
	public static final String ITEM_BOUGHT = "item.bought";
	public static final String ITEM_SOLD = "item.sold";
	public static final String MARKET_FEES = "fees.market";
	public static final String SALARY_PAID = "salary.paid";
	public static final String REDUNDANCY_PAY = "salary.redundancy";
	public static final String SERVICE_CHARGE = "service.charge";
	
	private static final ResourceBundle bundle = ResourceBundle.getBundle("starcorp.common.types.cash");
	
	public static String getDescription(String key, Object[] args) {
		try {
			if(args != null) {
				return MessageFormat.format(bundle.getString(key), args);
			}
			else {
				return bundle.getString(key);
			}
		}
		catch(MissingResourceException e) {
			return key;
		}
		catch(NullPointerException e) {
			return "!" + key + "!";
		}
	}
	
	private long ID;
	private int version;
	private long accountID;
	private long amount;
	private String description;
	private GalacticDate date;
	
	public CashTransaction() {
		
	}
	
	public CashTransaction(GalacticDate date, int amount, String description) {
		this.amount = amount;
		this.description = description;
		this.date = date;
	}
	
	public CashTransaction(Element e) {
		this.amount = Integer.parseInt(e.attributeValue("amount","0"));
		this.description = e.attributeValue("description");
		this.date = new GalacticDate(e.element("trans-date").element("date"));
	}
	
	public Element toXML(Element parent) {
		Element root = parent.addElement("transaction");
		root.addAttribute("amount", String.valueOf(amount));
		root.addAttribute("description", description);
		date.toXML(root.addElement("trans-date"));
		
		return root;
	}
	
	public int getYear() {
		return date == null ? 0 : date.getYear();
	}
	
	public void setYear(int year) {
		if(date == null) {
			this.date = new GalacticDate();
		}
		date.setYear(year);
	}
	
	public int getMonth() {
		return date == null ? 0 : date.getMonth();
	}
	
	public void setMonth(int month) {
		if(date == null) {
			this.date = new GalacticDate();
		}
		date.setMonth(month);
	}
	
	public long getAmount() {
		return amount;
	}
	public void setAmount(long amount) {
		this.amount = amount;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public GalacticDate getDate() {
		return date;
	}
	public void setDate(GalacticDate date) {
		this.date = date;
	}

	public String toString()
	{
	    final String TAB = "    ";
	    
	    String retValue = "";
	    
	    retValue = "CashTransaction ( "
	        + super.toString() + TAB
	        + "amount = " + this.amount + TAB
	        + "description = " + this.description + TAB
	        + "date = " + this.date + TAB
	        + " )";
	
	    return retValue;
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

	public long getAccountID() {
		return accountID;
	}

	public void setAccountID(long accountID) {
		this.accountID = accountID;
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
		this.ID = Long.parseLong(e.attributeValue("ID"));
		this.accountID = Long.parseLong(e.attributeValue("account"));
		this.amount = Integer.parseInt(e.attributeValue("amount"));
		this.description = e.element("description").getTextTrim();
		this.date = new GalacticDate(e.element("date"));
	}

	public Element toBasicXML(Element parent) {
		Element e = parent.addElement("cash-transaction");
		e.addAttribute("class", getClass().getSimpleName());
		e.addAttribute("ID", String.valueOf(ID));
		e.addAttribute("account", String.valueOf(accountID));
		e.addAttribute("amount", String.valueOf(amount));
		e.addElement("description").addText(description);
		date.toXML(e);
		return e;
	}

	public Element toFullXML(Element parent) {
		return toBasicXML(parent);
	}

	public String getDisplayName() {
		return getDescription() + " [" + getID() + "]";
	}
	
}
