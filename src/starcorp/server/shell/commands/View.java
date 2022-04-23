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
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import starcorp.common.entities.IEntity;
import starcorp.server.engine.AServerTask;
import starcorp.server.shell.ACommand;
import starcorp.server.shell.Shell;

/**
 * starcorp.server.shell.commands.View
 *
 * @author Seyed Razavi <monkeyx@gmail.com>
 * @version 20 Sep 2007
 */
public class View extends ACommand {
	private static Log log = LogFactory.getLog(View.class); 

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getHelpText()
	 */
	@Override
	public String getHelpText() {
		return "view (Entity Class) (ID) (xml?)\n\nPrints out the specified entity.  If the third parameter is supplied and is \"true\" then the output is done in xml format.";
	}

	/* (non-Javadoc)
	 * @see starcorp.server.shell.ACommand#getName()
	 */
	@Override
	public String getName() {
		return "view";
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
				return "view";
			}
			protected Log getLog() {
				return log;
			}
			protected void doJob() throws Exception {
				String entityClass = args.get(0);
				int ID = args.getAsInt(1);
				boolean xml = args.isTrue(2) || "xml".equalsIgnoreCase(args.get(2));
				if(entityClass == null || ID == 0) {
					out.println();
					out.print("Invalid arguments");
				}
				else {
					String className = "starcorp.common.entities." + entityClass;
					Class clazz = Class.forName(className);
					IEntity entity = entityStore.load(clazz, ID);
					if(entity != null) {
						if(xml) {
							StringWriter sw = new StringWriter();
							entity.printXML(sw);
							out.println();
							out.println(sw.toString());
						}
						else {
							out.println();
							out.println(entity);
						}
					}
					out.print(Shell.PROMPT);
					out.flush();
				}
			}
		};

	}

}
