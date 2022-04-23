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

import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;

/**
 * starcorp.server.shell.commands.Quit
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class Quit extends ACommand {
	private static Log log = LogFactory.getLog(Quit.class); 

	public AServerTask task(final Arguments args, final PrintWriter out) {
		return new AServerTask() {
			public String toString() {
				return super.toString() + (args.count() > 0 ?  " [" + args + "]" : "");
			}
			@Override
			public boolean isHighPriority() {
				return true;
			}
			protected Log getLog() {
				return log;
			}
			protected void doJob() throws Exception {
				out.println();
				out.println("Bye!");
				out.flush();
				try {
					engine.shutdown();
				}
				catch(Throwable e) {
					System.err.println(e.getMessage());
				}
			}
		};
	}

	@Override
	public String getName() {
		return "quit";
	}

	@Override
	public String getHelpText() {
		return "quit\n\nExits the server shell.";
	}

}
