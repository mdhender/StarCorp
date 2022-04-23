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
package starcorp.server.population;

import java.util.List;
import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.Colony;
import starcorp.server.engine.AServerTask;

/**
 * starcorp.server.population.PopulationProcessor
 * 
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 17 Sep 2007
 */
public class PopulationProcessor extends AServerTask {

	private static Log log = LogFactory.getLog(PopulationProcessor.class);

	private Vector<ColonyUpdater> updaters = new Vector<ColonyUpdater>(); 
	
	@Override
	protected void doJob() throws Exception {
		log.info("Starting population processor");
		entityStore.resetHappiness();
		List<Colony> colonies = entityStore.listColonies();
		int size = colonies.size();
		int i = 1;
		log.info(this +": " + size + " colonies to process.");
		for(Colony colony : colonies) {
			if (log.isDebugEnabled())
				log.debug(this + ": Scheduling colony updater " + i + " of " + size);
			ColonyUpdater updater = createUpdater(colony);
			updaters.add(updater);
			engine.schedule(updater);
			i++;
			Thread.yield();
		}
		log.info(this + ": Finished scheduling. Waiting for updaters to complete.");
		while(getUpdatersRunning() != 0) {
			Thread.yield();
		}
		log.info(this + ": Updaters finished.  Processing unemployed migration.");
		engine.schedule(new UnemployedMigration());
		log.info("Finished population processor");
	}
	
	private ColonyUpdater createUpdater(Colony colony) {
		return new ColonyUpdater(this, colony);
	}

	void done(ColonyUpdater updater) {
		updaters.remove(updater);
	}
	
	public int getUpdatersRunning() {
		return updaters.size();
	}

	@Override
	protected Log getLog() {
		return log;
	}

	@Override
	protected String getName() {
		return "Population Processor [" + getUpdatersRunning() + " updaters running]";
	}


}
