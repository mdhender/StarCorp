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

import starcorp.common.types.PopulationClass;
import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.Population
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class Population extends ACommand {
	private static Log log = LogFactory.getLog(Population.class); 

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "population\n\nPrints out PopulationClass names.";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "population";
	}

	public AServerTask task(final Arguments args, final PrintWriter out) {
		return new AServerTask() {
			public String toString() {
				return super.toString() + (args.count() > 0 ?  " [" + args + "]" : "");
			}
			@Override
			public boolean isHighPriority() {
				return true;
			}
			protected String getName() {
				return "population";
			}
			protected Log getLog() {
				return log;
			}
			protected void doJob() throws Exception {
				out.println();
				out.println("List of population classes:");
				for(PopulationClass popClass : PopulationClass.listTypes()) {
					out.println(popClass);
				}
				out.print(Shell.PROMPT);
				out.flush();
			}
		};
	}

}
