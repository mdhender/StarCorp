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
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.Date
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 27 Sep 2007
 */
public class Date extends ACommand {
	private static final Log log = LogFactory.getLog(Date.class);
	
	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "date (Month) (Year)\n\nDisplays the current game date or sets it to the specified values.";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "date";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#task(starcorp.server.shell.ACommand.Arguments, java.io.PrintWriter)
	 */
	@Override
	public AServerTask task(final Arguments args, final PrintWriter out) {
		return new AServerTask() {
			public String toString() {
				return super.toString() + (args.count() > 0 ?  " [" + args + "]" : "");
			}
			protected String getName() {
				return "date";
			}

			protected Log getLog() {
				return log;
			}

			protected void doJob() throws Exception {
				int month = args.getAsInt(0);
				int year = args.getAsInt(1);
				
				if(month > 0 && year > 0) {
					out.println();
					out.println("Current Date: " + ServerConfiguration.setDate(month, year));
				}
				else {
					out.println();
					out.println("Current Date: " + ServerConfiguration.getCurrentDate());
				}
				out.print(Shell.PROMPT);
				out.flush();
			}
		};
	}

}
