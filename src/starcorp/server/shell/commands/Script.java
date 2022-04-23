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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;

/**
 * starcorp.server.shell.commands.Script
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 24 Sep 2007
 */
public class Script extends ACommand {
	private static final Log log = LogFactory.getLog(Script.class);
	
	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "script (Script File)\n\nRuns the specified script file.";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "script";
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
			protected Log getLog() {
				return log;
			}
			protected String getName() {
				return "script";
			}
			protected void doJob() throws Exception {
				String scriptFile = args.concat(0);
				BufferedReader reader = new BufferedReader(new FileReader(scriptFile));
				String line;
				int lineCount = 1;
				while((line = reader.readLine()) != null) {
					int i = line.indexOf('#');
					if(i != -1)
						line = line.substring(0,i);
					AServerTask task = parser.parse(line);
					if(task == null) {
						out.println("script [" + scriptFile + "] error on line " + lineCount);
						out.flush();
					}
					else {
						engine.scheduleAndWait(task);
					}
					lineCount++;
					Thread.yield();
				}
			}
			
		};
	}

}
