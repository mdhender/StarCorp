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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * starcorp.common.types.ServiceFacility
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class ServiceFacility extends AFacilityType {

	public static final int MAX_COLONISTS_SERVICED = 10000;
	
	public static List<AFacilityType> listMedical(int quality) {
		List<AFacilityType> types = new ArrayList<AFacilityType>();
		Iterator<AFacilityType> i = AFacilityType.listTypes(ServiceFacility.class).iterator();
		while(i.hasNext()) {
			ServiceFacility facility = (ServiceFacility) i.next();
			if(facility.isMedical() && facility.getQuality() == quality) {
				types.add(facility);
			}
		}
		return types;
	}
	

	public static List<AFacilityType> listFitness(int quality) {
		List<AFacilityType> types = new ArrayList<AFacilityType>();
		Iterator<AFacilityType> i = AFacilityType.listTypes(ServiceFacility.class).iterator();
		while(i.hasNext()) {
			ServiceFacility facility = (ServiceFacility) i.next();
			if(facility.isFitness() && facility.getQuality() == quality) {
				types.add(facility);
			}
		}
		return types;
	}
	
	public static List<AFacilityType> listEntertainment(int quality) {
		List<AFacilityType> types = new ArrayList<AFacilityType>();
		Iterator<AFacilityType> i = AFacilityType.listTypes(ServiceFacility.class).iterator();
		while(i.hasNext()) {
			ServiceFacility facility = (ServiceFacility) i.next();
			if(facility.isEntertainment() && facility.getQuality() == quality) {
				types.add(facility);
			}
		}
		return types;
	}
	
	public static List<AFacilityType> listEducation(int quality) {
		List<AFacilityType> types = new ArrayList<AFacilityType>();
		Iterator<AFacilityType> i = AFacilityType.listTypes(ServiceFacility.class).iterator();
		while(i.hasNext()) {
			ServiceFacility facility = (ServiceFacility) i.next();
			if(facility.isEducation() && facility.getQuality() == quality) {
				types.add(facility);
			}
		}
		return types;
	}
	

	@Override
	public String getSubCategory() {
		return "Quality " + getQuality() +
		(isMedical() ? " medical " : "") + 
		(isFitness() ? " fitness " : "") + 
		(isEntertainment() ? " entertainment " : "") + 
		(isEducation() ? " education " : "");
	}
	
	public int getQuality() {
		return Integer.parseInt(getResource(this, "quality"));
	}
	
	public int getTotalColonistsServiceable(List<?> currentWorkers) {
		double efficiency = getEfficiency(currentWorkers);
		return (int) (MAX_COLONISTS_SERVICED * (efficiency / 100.0));
	}

	public boolean isMedical() {
		return Boolean.parseBoolean(getResource(this, "medical"));
	}

	public boolean isFitness() {
		return Boolean.parseBoolean(getResource(this, "fitness"));
	}
	
	public boolean isEntertainment() {
		return Boolean.parseBoolean(getResource(this, "entertainment"));
	}
	
	public boolean isEducation() {
		return Boolean.parseBoolean(getResource(this, "education"));
	}




}
