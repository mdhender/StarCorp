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

import starcorp.common.entities.IEntity;
import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.Delete
 * 
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class Delete extends ACommand {
	private static Log log = LogFactory.getLog(Delete.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "del (Entity Class) (ID)\n\nDeletes the specified entity (and all child entities associated with it).";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "del";
	}

	public AServerTask task(final Arguments args, final PrintWriter out) {
		return new AServerTask() {
			public String toString() {
				return super.toString() + (args.count() > 0 ?  " [" + args + "]" : "");
			}
			protected String getName() {
				return "del";
			}

			protected Log getLog() {
				return log;
			}

			protected void doJob() throws Exception {
				String entityClass = args.get(0);
				int ID = args.getAsInt(1);
				if (entityClass == null || ID == 0) {
					out.println();
					out.print("Invalid arguments");
				} else {
					String className = "starcorp.common.entities."
							+ entityClass;
					Class<?> clazz = Class.forName(className);
					IEntity o = entityStore.load(clazz, ID);
					if (o == null) {
						out.println();
						out.println("No such entity.");
					} else {
						entityStore.delete(o);
					}
					out.println();
					out.println("Entity " + o + " deleted.");
					out.print(Shell.PROMPT);
					out.flush();
				}
			}
		};
	}
}
