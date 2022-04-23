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
package starcorp.server.setup.planets;

import starcorp.common.types.TerrainType;
import starcorp.server.setup.APlanetTemplate;

/**
 * starcorp.server.setup.planets.Rock
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class Rock extends APlanetTemplate {

	private static final int[] atmospheresChance = {
		7,
		0,
		1,
		1,
		10,
		1,
		80
	};
	@Override
	protected String getDefaultAtmosphere() {
		return "vacuum";
	}

	@Override
	protected int[] getAtmosphereChances() {
		return atmospheresChance;
	}

	@Override
	protected int getTerrainChance(TerrainType terrain) {
		String key = terrain.getKey();
		if("desert".equals(key)) {
			return 5;
		}
		else if("hill".equals(key)) {
			return 20;
		}
		else if("mountain".equals(key)) {
			return 20;
		}
		else if("volcanic".equals(key)) {
			return 20;
		}
		else if("ice".equals(key)) {
			return 10;
		}
		return 0;
	}
	
	@Override
	protected String getDefaultTerrain() {
		return "hill";
	}
}
