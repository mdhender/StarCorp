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

import starcorp.common.entities.ANamedEntity;
import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.Name
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class Name extends ACommand {
	private static Log log = LogFactory.getLog(Name.class); 

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "name (Entity Class) (ID) (Name)\n\nSets the name of an entity which has a name field. ";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "name";
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
				return "name";
			}
			protected Log getLog() {
				return log;
			}
			protected void doJob() throws Exception {
				String entityClass = args.get(0);
				int ID = args.getAsInt(1);
				String name = args.concat(2);
				if(entityClass == null || ID == 0 || name == null) {
					out.println();
					out.print("Invalid arguments");
				}
				else {
					String className = "starcorp.common.entities." + entityClass;
					Class clazz = Class.forName(className);
					ANamedEntity entity = (ANamedEntity) entityStore.load(clazz, ID);
					entity.setName(name);
					entityStore.update(entity);
					out.println();
					out.println(entity);
					out.print(Shell.PROMPT);
					out.flush();
				}
			}
		};

	}

}
