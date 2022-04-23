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
package starcorp.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.AColonists;
import starcorp.common.entities.CashTransaction;
import starcorp.common.entities.Colony;
import starcorp.common.entities.Facility;
import starcorp.common.entities.MarketItem;
import starcorp.common.entities.Planet;
import starcorp.common.types.AFacilityType;
import starcorp.common.types.AItemType;
import starcorp.common.types.Coordinates2D;
import starcorp.common.types.GalacticDate;
import starcorp.common.types.Items;
import starcorp.server.entitystore.IEntityStore;

/**
 * starcorp.server.Util
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 22 Sep 2007
 */
public class Util {
	private static final Log log = LogFactory.getLog(Util.class);
	
	public static class SuitableLocation implements Comparable<SuitableLocation> {
		public Coordinates2D location;
		public Planet planet;
		public int rating;
		
		public int compareTo(SuitableLocation o) {
			return o.rating - this.rating;
		}
	
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((location == null) ? 0 : location.hashCode());
			result = prime * result
					+ ((planet == null) ? 0 : planet.hashCode());
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
			final SuitableLocation other = (SuitableLocation) obj;
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (!location.equals(other.location))
				return false;
			if (planet == null) {
				if (other.planet != null)
					return false;
			} else if (!planet.equals(other.planet))
				return false;
			return true;
		} 
	}
	public static class ServiceResult {
		public int quantityServiced;
		public int totalCost;
	}
	public static class BuyResult {
		public int quantityBought;
		public int totalPrice;
		public List<Items> bought = new ArrayList<Items>();
		
		public String toString() {
			StringBuffer sb = new StringBuffer("Quantity Bought: " + quantityBought + " Total Price: " + totalPrice + "\n");
			for(Items item : bought) {
				sb.append(item.toString());
			}
			return sb.toString();
		}
	}
	
	public static ServiceResult service(GalacticDate date, Map<Facility, List<AColonists>> facilities, int quantity, long cashAvailable, IEntityStore entityStore) {
		ServiceResult result = new ServiceResult();
		
		for(Facility facility : facilities.keySet()) {
			if(result.quantityServiced >= quantity) {
				break;
			}
			if(!facility.isPowered()) {
				continue;
			}
			List<AColonists> workers = facilities.get(facility);
			int avail = facility.getTransactionsRemaining(workers);
			if(avail < 1)
				continue;
			int qty = quantity - result.quantityServiced;
			long afford = cashAvailable / facility.getServiceCharge();
			if(afford < qty) {
				qty = (int) afford;
			}
			if(avail < qty) {
				qty = avail;
			}
			AFacilityType type = facility.getTypeClass();
			int price = qty * facility.getServiceCharge();
			Colony colony = (Colony) entityStore.load(Colony.class,facility.getColony());
			Object[] args = {type.getName(), String.valueOf(qty), colony.getName(), String.valueOf(colony.getID())};
			String desc = CashTransaction.getDescription(CashTransaction.SERVICE_CHARGE, args);
			entityStore.addCredits(facility.getOwner(), price, desc);
			facility.incTransactionCount();
			entityStore.update(facility);
			result.quantityServiced += qty;
			result.totalCost += price;
			cashAvailable -= price;
		}
		return result;
	}
	
	public static BuyResult buy(GalacticDate date, List<MarketItem> items, int quantity, long cashAvailable, IEntityStore entityStore) {
		BuyResult result = new BuyResult();
		if(log.isDebugEnabled()) {
			log.debug("Buy: " + cashAvailable + " to spend to buy " + quantity + " of " + items);
		}
		for(MarketItem item : items) {
			if(log.isDebugEnabled()) {
				log.debug("Buy: " + result + " : " + item);
			}
			if(result.quantityBought >= quantity) {
				if(log.isDebugEnabled()) {
					log.debug("Buy: " + result);
				}
				return result;
			}
			int avail = item.getQuantity();
			if(avail < 1)
				continue;
			Colony colony = (Colony) entityStore.load(Colony.class,item.getColony());
			AItemType type = item.getItem().getTypeClass();
			int qty = quantity - result.quantityBought;
			long afford = cashAvailable / item.getCostPerItem();
			if(afford < qty) {
				qty = (int) afford;
			}
			if(avail < qty) {
				qty = avail;
			}
			long price = qty * item.getCostPerItem();
			
			Object[] args = {String.valueOf(qty), type.getName(),colony.getName(),String.valueOf(colony.getID())};
			String desc = CashTransaction.getDescription(CashTransaction.ITEM_SOLD, args);
			entityStore.addCredits(item.getSeller(), price, desc);
			item.remove(qty);
			if(item.getItem().getQuantity() < 1) {
				item.setSoldDate(date);
			}
			entityStore.update(item);
			result.quantityBought += qty;
			result.totalPrice += price;
			cashAvailable -= price;
			result.bought.add(new Items(type,qty));
		}
		if(log.isDebugEnabled()) {
			log.debug("Buy: " + result);
		}
		return result;
	}

	public static final String getDisplayDuration(long milliseconds) {
		if(milliseconds < 1000)
			return "0s";
		
		int seconds = (int) milliseconds / 1000;
		if(seconds < 60) { // less than a minute
			return seconds + "s";
		}
		else if(seconds < 3600){ // less than an hour
			int minutes = (int) (seconds / 60);
			int secs = seconds % (minutes * 60);
			return minutes +"m "+secs+"s";
		}
		else {
			int hours = (int) (seconds / 3600);
			int secs = seconds % (hours * 3600);
			int minutes = (int) (secs / 60);
			secs = seconds % (minutes * 60);
			return hours + "h "+minutes +"m "+secs+"s";
		}
	}

	public static final Random rnd = new Random(System.currentTimeMillis());

	public static Object pick(Map<?, Integer> mapOfChoices) {
		Object[] choices = mapOfChoices.keySet().toArray();
		int[] chances = new int[choices.length];
		for(int i = 0; i < choices.length; i++) {
			chances[i] = mapOfChoices.get(choices[i]);
		}
		return pick(choices,chances);
	}

	public static Object pick(Object[] choices, int[] chances) {
		int rand = Util.rnd.nextInt(100);
		int x = 0;
		int n = 0;
		for(int i : chances) {
			x += i;
			if(rand < x)
				return choices[n];
			n++;
		}
		return null;
	}
	// TODO move these name options to a configuration file
	private static final String[] colonyNamePrefixes = new String[17];
	private static final String[] colonyNameMiddles = new String[54];
	private static final String[] colonyNameSuffixes = new String[39];
	
	private static final String[] corpNamePrefixes = new String[15];
	private static final String[] corpNameMiddles = new String[35];
	private static final String[] corpNameSuffixes = new String[20];
	
	private static final String[] govtSuffixes = new String[20];

	static {
		govtSuffixes[0] = " Federation";
		govtSuffixes[1] = " Empire";
		govtSuffixes[2] = " Technocracy";
		govtSuffixes[3] = " Republic";
		govtSuffixes[4] = " Kingdom";
		govtSuffixes[5] = " Emirates";
		govtSuffixes[6] = " Alliance";
		govtSuffixes[7] = " People's Republic";
		govtSuffixes[8] = " Federal Republic";
		govtSuffixes[9] = " Theocracy";
		govtSuffixes[10] = " Union";
		govtSuffixes[11] = " Sultanates";
		govtSuffixes[12] = " Free Republic";
		govtSuffixes[13] = " Dominion";
		govtSuffixes[14] = " Singularity";
		govtSuffixes[15] = " Matrix";
		govtSuffixes[16] = " Cooperative";
		govtSuffixes[17] = " Free Holdings";
		govtSuffixes[18] = " Commune";
		govtSuffixes[19] = " Collective";

		corpNamePrefixes[0] = "New ";
		corpNamePrefixes[1] = "Interstellar ";
		corpNamePrefixes[2] = "Advanced ";
		corpNamePrefixes[3] = "Total ";
		corpNamePrefixes[4] = "Federated ";
		corpNamePrefixes[5] = "Cyber ";
		corpNamePrefixes[6] = "Superior ";
		corpNamePrefixes[7] = "";
		corpNamePrefixes[8] = "";
		corpNamePrefixes[9] = "";
		corpNamePrefixes[10] = "";
		corpNamePrefixes[11] = "";
		corpNamePrefixes[12] = "";
		corpNamePrefixes[13] = "";
		corpNamePrefixes[14] = "";
		
		corpNameMiddles[0] = "Omega";
		corpNameMiddles[1] = "Alpha";
		corpNameMiddles[2] = "Corella";
		corpNameMiddles[3] = "Amber";
		corpNameMiddles[4] = "Orion";
		corpNameMiddles[5] = "Nebula";
		corpNameMiddles[6] = "Andromeda";
		corpNameMiddles[7] = "Aquarias";
		corpNameMiddles[8] = "Capricorn";
		corpNameMiddles[9] = "Cygnus";
		corpNameMiddles[10] = "Draco";
		corpNameMiddles[11] = "Lynx";
		corpNameMiddles[12] = "Pegasus";
		corpNameMiddles[13] = "Phoenix";
		corpNameMiddles[14] = "Scorpius";
		corpNameMiddles[15] = "Velan";
		corpNameMiddles[16] = "Persues";
		corpNameMiddles[17] = "Lyra";
		corpNameMiddles[18] = "Hydrus";
		corpNameMiddles[19] = "Sirius";
		corpNameMiddles[20] = "Protux";
		corpNameMiddles[21] = "Scimitar";
		corpNameMiddles[22] = "Zeus";
		corpNameMiddles[23] = "Apollo";
		corpNameMiddles[24] = "Athena";
		corpNameMiddles[25] = "Hermes";
		corpNameMiddles[26] = "Ares";
		corpNameMiddles[27] = "Jupiter";
		corpNameMiddles[28] = "Neptune";
		corpNameMiddles[29] = "Pluto";
		corpNameMiddles[30] = "Mars";
		corpNameMiddles[31] = "Saturn";
		corpNameMiddles[32] = "Janus";
		corpNameMiddles[33] = "Camelot";
		corpNameMiddles[34] = "Totem";
		
		corpNameSuffixes[0] = " Alliance";
		corpNameSuffixes[1] = " Conglomerate";
		corpNameSuffixes[2] = " Consortium";
		corpNameSuffixes[3] = " Partners";
		corpNameSuffixes[4] = ", Inc.";
		corpNameSuffixes[5] = " Construction";
		corpNameSuffixes[6] = " Technology";
		corpNameSuffixes[7] = " Ventures";
		corpNameSuffixes[8] = " Mining";
		corpNameSuffixes[9] = " Corporation";
		corpNameSuffixes[10] = " Associates";
		corpNameSuffixes[11] = " Trust";
		corpNameSuffixes[12] = " Group";
		corpNameSuffixes[13] = " Intergalactic";
		corpNameSuffixes[14] = " Solutions";
		corpNameSuffixes[15] = " Society";
		corpNameSuffixes[16] = " Service";
		corpNameSuffixes[17] = " Holdings";
		corpNameSuffixes[18] = " Company";
		corpNameSuffixes[19] = ", Inc.";
		
		colonyNamePrefixes[0] = "Fort ";
		colonyNamePrefixes[1] = "Port ";
		colonyNamePrefixes[2] = "Sinus ";
		colonyNamePrefixes[3] = "New ";
		colonyNamePrefixes[4] = "Mare ";
		colonyNamePrefixes[5] = "Old ";
		colonyNamePrefixes[6] = "";
		colonyNamePrefixes[7] = "";
		colonyNamePrefixes[8] = "";
		colonyNamePrefixes[9] = "";
		colonyNamePrefixes[10] = "";
		colonyNamePrefixes[11] = "";
		colonyNamePrefixes[12] = "";
		colonyNamePrefixes[13] = "";
		colonyNamePrefixes[14] = "";
		colonyNamePrefixes[15] = "";
		colonyNamePrefixes[16] = "";

		colonyNameMiddles[0] = "Green";
		colonyNameMiddles[1] = "Conclave";
		colonyNameMiddles[2] = "Yellow";
		colonyNameMiddles[3] = "Blue";
		colonyNameMiddles[4] = "Small";
		colonyNameMiddles[5] = "Tallen";
		colonyNameMiddles[6] = "Humorum";
		colonyNameMiddles[7] = "Limb";
		colonyNameMiddles[8] = "Hammer";
		colonyNameMiddles[9] = "Holly";
		colonyNameMiddles[10] = "Purbach";
		colonyNameMiddles[11] = "Rune";
		colonyNameMiddles[12] = "Silver";
		colonyNameMiddles[13] = "Potters";
		colonyNameMiddles[14] = "Dark";
		colonyNameMiddles[15] = "Red";
		colonyNameMiddles[16] = "Golden";
		colonyNameMiddles[17] = "Gold";
		colonyNameMiddles[18] = "Ice";
		colonyNameMiddles[19] = "Hollow";
		colonyNameMiddles[20] = "Lan";
		colonyNameMiddles[21] = "Os";
		colonyNameMiddles[22] = "Allans";
		colonyNameMiddles[23] = "Timber";
		colonyNameMiddles[24] = "Fallen";
		colonyNameMiddles[25] = "Strat";
		colonyNameMiddles[26] = "Hali";
		colonyNameMiddles[27] = "Epidemiarum";
		colonyNameMiddles[28] = "Pine";
		colonyNameMiddles[29] = "Apple";
		colonyNameMiddles[30] = "By";
		colonyNameMiddles[31] = "York";
		colonyNameMiddles[32] = "Earth";
		colonyNameMiddles[33] = "Wind";
		colonyNameMiddles[34] = "Fire";
		colonyNameMiddles[35] = "Sod";
		colonyNameMiddles[36] = "Sandy";
		colonyNameMiddles[37] = "Beds";
		colonyNameMiddles[38] = "Kent";
		colonyNameMiddles[39] = "Jutes";
		colonyNameMiddles[40] = "Wednes";
		colonyNameMiddles[41] = "Oswald";
		colonyNameMiddles[42] = "Habrig";
		colonyNameMiddles[43] = "Kedal";
		colonyNameMiddles[44] = "Gantrick";
		colonyNameMiddles[45] = "Jonas";
		colonyNameMiddles[46] = "Ossic";
		colonyNameMiddles[47] = "Sid";
		colonyNameMiddles[48] = "Pound";
		colonyNameMiddles[49] = "Sliccic";
		colonyNameMiddles[50] = "Brase";
		colonyNameMiddles[51] = "Ash";
		colonyNameMiddles[52] = "Wild";
		colonyNameMiddles[53] = "Tree";
		colonyNameMiddles[53] = "Windy";

		colonyNameSuffixes[0] = "burgh";
		colonyNameSuffixes[1] = "vale";
		colonyNameSuffixes[2] = "down";
		colonyNameSuffixes[3] = " River";
		colonyNameSuffixes[4] = " Valley";
		colonyNameSuffixes[5] = " River Valley";
		colonyNameSuffixes[6] = "hill";
		colonyNameSuffixes[7] = "berg";
		colonyNameSuffixes[8] = "dale";
		colonyNameSuffixes[9] = "shine";
		colonyNameSuffixes[10] = "grove";
		colonyNameSuffixes[11] = "bere";
		colonyNameSuffixes[12] = "ton";
		colonyNameSuffixes[13] = "";
		colonyNameSuffixes[14] = "wick";
		colonyNameSuffixes[15] = "ford";
		colonyNameSuffixes[16] = "ham";
		colonyNameSuffixes[17] = "caster";
		colonyNameSuffixes[18] = "mouth";
		colonyNameSuffixes[19] = "vasser";
		colonyNameSuffixes[20] = " Breeze";
		colonyNameSuffixes[21] = "-on-Naze";
		colonyNameSuffixes[22] = "mountain";
		colonyNameSuffixes[23] = "berry";
		colonyNameSuffixes[24] = "fax";
		colonyNameSuffixes[25] = "bee";
		colonyNameSuffixes[26] = " Grotto";
		colonyNameSuffixes[27] = "borough";
		colonyNameSuffixes[28] = "wing";
		colonyNameSuffixes[29] = "shaft";
		colonyNameSuffixes[30] = "water";
		colonyNameSuffixes[31] = "claw";
		colonyNameSuffixes[32] = "don";
		colonyNameSuffixes[33] = "tol";
		colonyNameSuffixes[34] = "bottom";
		colonyNameSuffixes[35] = "tol";
		colonyNameSuffixes[36] = "point";
		colonyNameSuffixes[37] = "warren";
		colonyNameSuffixes[38] = " Gulch";
	}
	
	public static String getRandomGovernmentName(String systemName) {
		return systemName + govtSuffixes[rnd.nextInt(govtSuffixes.length)];
	}
	
	public static String getRandomColonyName() {
		return colonyNamePrefixes[rnd.nextInt(colonyNamePrefixes.length)] +
		colonyNameMiddles[rnd.nextInt(colonyNameMiddles.length)] +
		colonyNameSuffixes[rnd.nextInt(colonyNameSuffixes.length)];
	}
	
	public static String getRandomCorporationName() {
		return corpNamePrefixes[rnd.nextInt(corpNamePrefixes.length)] +
		corpNameMiddles[rnd.nextInt(corpNameMiddles.length)] +
		corpNameSuffixes[rnd.nextInt(corpNameSuffixes.length)];
	}

}
