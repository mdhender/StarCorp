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
 * starcorp.common.types.StarshipHull
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 16 Sep 2007
 */
public class StarshipHulls extends AFactoryItem {
	
	public int getConsumerCapacity() {
		return getResourceAsInt(this, "capacity.consumer");
	}
	
	public int getIndustrialCapacity() {
		return getResourceAsInt(this, "capacity.industrial");
	}

	public int getModulesCapacity() {
		return getResourceAsInt(this, "capacity.modules");
	}

	public int getOrganicsCapacity() {
		return getResourceAsInt(this, "capacity.organics");
	}

	public int getLiquidGasCapacity() {
		return getResourceAsInt(this, "capacity.liquidgas");
	}

	public int getThrustPower() {
		return getResourceAsInt(this, "thrust");
	}

	public int getWarpFactor() {
		return getResourceAsInt(this, "warp");
	}

	public boolean isAsteroidMining() {
		return getResourceAsBoolean(this, "mining.asteroid");
	}

	public boolean isGasFieldMining() {
		return getResourceAsBoolean(this, "mining.gasfield");
	}
	
	public int getLongScanner() {
		return getResourceAsInt(this, "scan.long");
	}
	
	public boolean isShortScanner() {
		return getResourceAsBoolean(this, "scan.short");
	}
	
	public boolean isCommand() {
		return getResourceAsBoolean(this, "command");
	}

	public boolean isCrew() {
		return getResourceAsBoolean(this, "crew");
	}
	
	public boolean isSystemProbe() {
		return getResourceAsBoolean(this, "probe.system");
	}
	
	public boolean isPlanetProbe() {
		return getResourceAsBoolean(this, "probe.planet");
	}

	public boolean isBioLab() {
		return getResourceAsBoolean(this, "lab.bio");
	}

	public boolean isPhysicsLab() {
		return getResourceAsBoolean(this, "lab.physics");
	}

	public boolean isGeoLab() {
		return getResourceAsBoolean(this, "lab.geo");
	}

	@Override
	public String getSubCategory() {
		StringBuffer s = new StringBuffer();
		int cargoCapacity = getConsumerCapacity() + getIndustrialCapacity() + getModulesCapacity() + getOrganicsCapacity() + getLiquidGasCapacity(); 
		if(cargoCapacity > 0) {
			s.append(" Cargo Space: ");
			if(getConsumerCapacity() > 0) {
				s.append(getConsumerCapacity());
				s.append("mu Consumer Goods ");
			}
			if(getIndustrialCapacity() > 0) {
				s.append(getIndustrialCapacity());
				s.append("mu Industrial Goods ");
			}
			if(getModulesCapacity() > 0) {
				s.append(getModulesCapacity());
				s.append("mu Modules");
			}
			if(getOrganicsCapacity() > 0) {
				s.append(getOrganicsCapacity());
				s.append("mu Organics");
			}
			if(getLiquidGasCapacity() > 0) {
				s.append(getLiquidGasCapacity());
				s.append("mu Liquids / Gas");
			}
		}
		if(getThrustPower() > 0) {
			s.append(" Thrust (" + getThrustPower() + ") ");
		}
		if(getWarpFactor() > 0) {
			s.append(" Warp (" + getWarpFactor() + ") ");
		}
		if(isAsteroidMining() || isGasFieldMining()) {
			s.append(" Mine" + 
					(isAsteroidMining() ? " asteroids" : "") +
					(isGasFieldMining() ? " gas fields" : ""));
		}
		if(getLongScanner() > 0 || isShortScanner()) {
			s.append(" Scan " + 
					(isShortScanner() ? "short range " : "") +
					getLongScanner() + " long range");
		}
		if(isCommand())
			s.append(" Command");
		if(isCrew())
			s.append(" Crew");
		if(isSystemProbe() || isPlanetProbe())
			s.append(" Probe" + 
				(isSystemProbe() ? " system" : "") +
				(isPlanetProbe() ? " planet" : ""));
		if(isBioLab() || isPhysicsLab() || isGeoLab())
			s.append(" Lab" + 
				(isBioLab() ? " bio" : "") + 
				(isPhysicsLab() ? " physics" : "") +
				(isGeoLab() ? " geo" : ""));
		
		return s.toString().trim();
			
	}
}
