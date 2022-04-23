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
package starcorp.server.setup.systems;

import starcorp.common.entities.Planet;
import starcorp.server.setup.APlanetTemplate;
import starcorp.server.setup.ASystemTemplate;

/**
 * starcorp.server.setup.systems.Standard
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 21 Sep 2007
 */
public class Standard extends ASystemTemplate {

	@Override
	protected int countMoons(APlanetTemplate template,Planet planet) {
		return starcorp.server.Util.rnd.nextInt(planet.getGravityRating());
	}

	@Override
	protected APlanetTemplate getMoonTemplate(APlanetTemplate planetTemplate,Planet planet) {
		int rand = starcorp.server.Util.rnd.nextInt(10);
		switch(rand) {
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
			return APlanetTemplate.getTemplate("Rock");
		case 6:
		case 7:
		case 8:
			return APlanetTemplate.getTemplate("Ice");
		case 9:
		default:
			return APlanetTemplate.getTemplate("Arid");
		}
	}

	@Override
	protected APlanetTemplate hasPlanet(int orbit) {
		if(orbit > 8 && orbit < 12 && starcorp.server.Util.rnd.nextInt(100) <= 25) {
			return APlanetTemplate.getTemplate("GasGiant");
		}
		if(orbit < 6 && starcorp.server.Util.rnd.nextInt(100) <= 5) {
			return APlanetTemplate.getTemplate("Rock");
		}
		if(orbit > 8 && starcorp.server.Util.rnd.nextInt(100) <= 5) {
			return APlanetTemplate.getTemplate("Ice");
		}
		if(orbit > 3 && orbit < 12) {
			int rand = starcorp.server.Util.rnd.nextInt(100);
			if(rand <= 1) {
				return APlanetTemplate.getTemplate("Green");
			}
			else if(rand <= 2) {
				return APlanetTemplate.getTemplate("Oceanic");
			}
			else if(rand <= 3) {
				return APlanetTemplate.getTemplate("Volcanic");
			}
			else if(rand <= 4) {
				return APlanetTemplate.getTemplate("Tropical");
			}
		}
		return null;
	}

}
