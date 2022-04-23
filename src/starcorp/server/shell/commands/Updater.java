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
package starcorp.server.shell.commands;

import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.server.ServerConfiguration;
import starcorp.server.engine.AServerTask;
import starcorp.server.facilities.FacilityProcessor;
import starcorp.server.npc.NPCProcessor;
import starcorp.server.population.PopulationProcessor;
import starcorp.server.population.UnemployedMigration;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;
import starcorp.server.turns.TurnFetcher;
import starcorp.server.turns.TurnProcessor;

/**
 * starcorp.server.shell.commands.Updater
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class Updater extends ACommand {
	private static Log log = LogFactory.getLog(Updater.class); 

	
	public AServerTask task(final Arguments args, final PrintWriter out) {
		String type = args.get(0);
		if(type == null || type.length() < 1) {
			out.println("Invalid type");
			out.print(Shell.PROMPT);
			out.flush();
		}
		else if("inc".equalsIgnoreCase(type)) {
			return new AServerTask() {
				public String toString() {
					return super.toString() + (args.count() > 0 ?  " [" + args + "]" : "");
				}
				protected String getName() {
					return "updates";
				}
				protected Log getLog() {
					return log;
				}
				protected void doJob() throws Exception {
					ServerConfiguration.incrementDate();
					entityStore.resetFacilityTransactions();
					entityStore.resetShipTimeUnits();
					out.println();
					out.println("Date increment. Transactions / Time Units reset.");
					out.println(Shell.PROMPT);
					out.flush();
				}
			};
		}
		else if("pop".equalsIgnoreCase(type)) {
			return new PopulationProcessor();
		}
		else if("migrate".equalsIgnoreCase(type)) {
			return new UnemployedMigration();
		}
		else if("npc".equalsIgnoreCase(type)) {
			return new NPCProcessor();
		}
		else if("fac".equalsIgnoreCase(type)) {
			return new FacilityProcessor();
		}
		else if("fetch".equalsIgnoreCase(type)) {
			return new TurnFetcher();
		}
		else if("turns".equalsIgnoreCase(type)) {
			return new TurnProcessor();
		}
		else if("all".equalsIgnoreCase(type)) {
			return new AServerTask() {
				public String toString() {
					return super.toString() + (args.count() > 0 ?  " [" + args + "]" : "");
				}
				protected String getName() {
					return "batch-updater";
				}
				protected Log getLog() {
					return log;
				}
				protected void doJob() throws Exception {
					ServerConfiguration.incrementDate();
					entityStore.resetFacilityTransactions();
					entityStore.resetShipTimeUnits();
					engine.scheduleAndWait(new PopulationProcessor());
					engine.scheduleAndWait(new FacilityProcessor());
					engine.scheduleAndWait(new NPCProcessor());
					engine.scheduleAndWait(new TurnFetcher());
					engine.scheduleAndWait(new TurnProcessor());
					out.println();
					out.println("Updates done.");
					out.println(Shell.PROMPT);
					out.flush();
				}
			};
		}
		else {
			out.println("Invalid type");
			out.print(Shell.PROMPT);
			out.flush();
		}
		return null;
	}

	@Override
	public String getName() {
		return "update";
	}

	@Override
	public String getHelpText() {
		return "update (Type)\n\nRuns one of the game processor based on type argument.";
	}

}
