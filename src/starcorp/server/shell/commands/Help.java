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
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.Help
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class Help extends ACommand {
	private static Log log = LogFactory.getLog(Help.class); 

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "help\nhelp [command]\n\nPrints command help or a list of commands if command is blank or not found.";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "help";
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
				return "help";
			}
			protected Log getLog() {
				return log;
			}
			protected void doJob() throws Exception {
				ACommand command = null;
				String arg = args.get(0);
				if(arg != null) {
					command = parser.getCommand(arg);
				}
				if(command == null) {
					if(arg != null) {
						out.println();
						out.println("Command " + arg + " not found.");
					}
					out.println();
					out.println("List of commands. Type help [command] for further help.");
					Iterator<String> i = parser.listCommands();
					int count = 0;
					while(i.hasNext()) {
						if(count % 5 == 4) {
							out.println(i.next());
						}
						else {
							out.print(i.next() + "   ");
						}
						count++;
					}
					out.println();
				}
				else {
					out.println(command.getHelpText());
				}
				out.print(Shell.PROMPT);
				out.flush();
			}
		};
	}

}
