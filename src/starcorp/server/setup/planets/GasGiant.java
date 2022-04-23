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
 * starcorp.server.setup.planets.GasGiant
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class GasGiant extends APlanetTemplate {

	private static final int[] atmospheresChance = {
		0,
		0,
		10,
		10,
		0,
		80,
		0
	};
	
	@Override
	protected String getDefaultAtmosphere() {
		return "toxic";
	}

	@Override
	protected int[] getAtmosphereChances() {
		return atmospheresChance;
	}

	@Override
	protected int getGravityRating() {
		return 20;
	}

	protected int getSize() {
		return 0; // no planet map for gas giants
	}

	@Override
	protected int getTerrainChance(TerrainType terrain) {
		// gas giants have no map!
		return 0;
	}
	
	@Override
	protected String getDefaultTerrain() {
		return "";
	}

}
