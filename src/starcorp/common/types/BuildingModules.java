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

/**
 * starcorp.common.types.BuildingModule
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class BuildingModules extends AFactoryItem {
	
	public boolean isStructural() {
		return getResourceAsBoolean(this, "structural");
	}

	public boolean isIndustrial() {
		return getResourceAsBoolean(this, "industrial");
	}
	
	public boolean isCommercial() {
		return getResourceAsBoolean(this, "commercial");
	}
	
	public boolean isHabitation() {
		return getResourceAsBoolean(this, "habitation");
	}

	public boolean isMilitary() {
		return getResourceAsBoolean(this, "military");
	}

	@Override
	public String getSubCategory() {
		if(isStructural())
			return "Structural";
		if(isIndustrial())
			return "Industrial";
		if(isCommercial())
			return "Commercial";
		if(isHabitation())
			return "Habitation";
		if(isMilitary())
			return "Military";
		return "";
	}
}
