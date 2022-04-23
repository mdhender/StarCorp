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
 * starcorp.common.types.ResourceGenerator
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class ResourceGenerator extends AFacilityType {
	
	public boolean canGenerate(AItemType type) {
		if(!(type instanceof Resources)) {
			return false;
		}
		Resources resource = (Resources) type;
		if(resource.isFissile() && isFissile()) {
			return true;
		}
		if(resource.isFuel() && isFuel()) {
			return true;
		}
		if(resource.isGas() && isGas()) {
			return true;
		}
		if(resource.isLiquid() && isLiquid()) {
			return true;
		}
		if(resource.isMetal() && isMetal()) {
			return true;
		}
		if(resource.isMineral() && isMinerals()) {
			return true;
		}
		if(resource.isOrganic() && isOrganic()) {
			return true;
		}
		return false;
	}
	
	public boolean isOrganic() {
		return Boolean.parseBoolean(getResource(this, "organic"));
	}

	public boolean isMinerals() {
		return Boolean.parseBoolean(getResource(this, "mineral"));
	}

	public boolean isMetal() {
		return Boolean.parseBoolean(getResource(this, "metal"));
	}

	public boolean isFissile() {
		return Boolean.parseBoolean(getResource(this, "fissile"));
	}

	public boolean isFuel() {
		return Boolean.parseBoolean(getResource(this, "fuel"));
	}

	public boolean isGas() {
		return Boolean.parseBoolean(getResource(this, "gas"));
	}

	public boolean isLiquid() {
		return Boolean.parseBoolean(getResource(this, "liquid"));
	}

	@Override
	public String getSubCategory() {
		return
		(isOrganic() ? "organics " : "") + 
		(isMinerals() ? "minerals " : "") +
		(isMetal() ? "metals " : "") + 
		(isFissile() ? "fissile " : "") + 
		(isFuel() ? "fuel " : "") +
		(isGas() ? "gas " : "") + 
		(isLiquid() ? "liquids " : "" );
	}
}
