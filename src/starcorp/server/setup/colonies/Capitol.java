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
package starcorp.server.setup.colonies;

import starcorp.server.setup.AColonyTemplate;

/**
 * starcorp.server.setup.colonies.Capitol
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 21 Sep 2007
 */
public class Capitol extends AColonyTemplate {

	@Override
	protected int countGenerators() {
		return (starcorp.server.Util.rnd.nextInt(5) + 1) * 20;
	}

	/* (non-Javadoc)
	 * @see starcorp.server.setup.AColonyTemplate#countHeavyFactories()
	 */
	@Override
	protected int countHeavyFactories() {
		return (starcorp.server.Util.rnd.nextInt(3) + 1) * 10;
	}

	/* (non-Javadoc)
	 * @see starcorp.server.setup.AColonyTemplate#countLightFactories()
	 */
	@Override
	protected int countLightFactories() {
		return (starcorp.server.Util.rnd.nextInt(3) + 1) * 10;
	}

	/* (non-Javadoc)
	 * @see starcorp.server.setup.AColonyTemplate#countShipyards()
	 */
	@Override
	protected int countShipyards() {
		return (starcorp.server.Util.rnd.nextInt(10) + 1);
	}

	/* (non-Javadoc)
	 * @see starcorp.server.setup.AColonyTemplate#countSuperHeavyFactories()
	 */
	@Override
	protected int countSuperHeavyFactories() {
		return (starcorp.server.Util.rnd.nextInt(30) + 1);
	}

	/* (non-Javadoc)
	 * @see starcorp.server.setup.AColonyTemplate#hasOrbitalDock()
	 */
	@Override
	protected boolean hasOrbitalDock() {
		return true;
	}

	@Override
	protected int getMarketSize() {
		return 1000000;
	}

}
