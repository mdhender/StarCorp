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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.dom4j.Element;

import starcorp.common.types.GalacticDate;

/**
 * starcorp.common.entities.Corporation
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 15 Sep 2007
 */
public class Corporation extends ANamedEntity {

	private String playerName;
	private String playerEmail;
	private String playerPassword;
	private GalacticDate foundedDate;
	private Set<Long> knownSystems = new HashSet<Long>();
	
	public Corporation() {
		
	}
	
	public Corporation(String name, String email, String password) {
		this.playerName = name;
		this.playerEmail = email;
		this.playerPassword = password;
	}
	
	public void readXML(Element e) {
		super.readXML(e);
		Element player = e.element("player");
		if(player != null) {
			this.playerEmail = player.attributeValue("email");
			this.playerName = player.attributeValue("name");
			this.playerPassword = player.attributeValue("password");
		}
		Element eKnown = e.element("known-systems");
		if(eKnown != null) {
			for(Iterator i = eKnown.elementIterator("system"); i.hasNext();) {
				long sysID = Long.parseLong(((Element)i.next()).attributeValue("ID","0"));
				add(sysID);
			}
		}
		Element eDate = e.element("founded");
		if(eDate != null) {
			this.foundedDate = new GalacticDate(eDate.element("date"));
		}
		eDate = e.element("last-turn");
	}
	
	public Element toFullXML(Element parent) {
		Element root = super.toFullXML(parent);
		Element p = root.addElement("player");
		p.addAttribute("email", playerEmail);
		p.addAttribute("name", playerName);
		p.addAttribute("password", playerPassword);
		Element e = root.addElement("known-systems");
		for(Long system : knownSystems) {
			e.addElement("system").addAttribute("ID", String.valueOf(system));
		}
		if(foundedDate != null)
			foundedDate.toXML(root.addElement("founded"));
		return root;
	}
	
	public void add(long system) {
		knownSystems.add(system);
	}
	
	public boolean isKnown(StarSystem system) {
		return knownSystems.contains(system);
	}
	
	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	public String getPlayerEmail() {
		return playerEmail;
	}
	public void setPlayerEmail(String playerEmail) {
		this.playerEmail = playerEmail;
	}
	public GalacticDate getFoundedDate() {
		return foundedDate;
	}
	public void setFoundedDate(GalacticDate foundedDate) {
		this.foundedDate = foundedDate;
	}

	public String getPlayerPassword() {
		return playerPassword;
	}

	public void setPlayerPassword(String playerPassword) {
		this.playerPassword = playerPassword;
	}

	public Set<Long> getKnownSystems() {
		return knownSystems;
	}

	public void setKnownSystems(Set<Long> knownSystems) {
		this.knownSystems = knownSystems;
	}

}
